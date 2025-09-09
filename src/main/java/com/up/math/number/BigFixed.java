package com.up.math.number;

import java.util.function.Function;

/**
 * Represents an 16.X fixed point number
 * @author Ricky
 */
public class BigFixed implements Cloneable, Comparable<BigFixed> {
    // TODO: Rework this into a record class with everything properly immutable, especially since most methods are essentially written that way already.
    final static int FIXED_MAX = 10;
    
    // While still mutable
    public static BigFixed PI() {return BigFixed.fromBitString(" 0000000000000011.001001000011111101101010100010001000010110100011000010001101001100010011000110011000101000101110000000110111000001110011010001001010010000001001");}
//    public static final BigFixed PI = BigFixed.fromBitString(" 0000000000000011.001001000011111101101010100010001000010110100011000010001101001100010011000110011000101000101110000000110111000001110011010001001010010000001001");
    
    int size = 1;
    boolean sign = false;
    short[] parts = new short[FIXED_MAX];
	boolean overflow = false;

	public BigFixed() {
	}

	public BigFixed(boolean sign, int size, short[] parts) {
		this.sign = sign;
		this.size = size;
		this.parts = parts;
	}

    BigFixed expand() {
        BigFixed ans = clone();
        ans.size++;
        if (ans.size > FIXED_MAX) {
//            System.out.println("Warning: Fixed number size exceeded.");
            ans.size = FIXED_MAX;
        }
        return ans;
    }

    BigFixed lshBF(int amount) {
        BigFixed ans = clone();
        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < ans.size; j++) {
                if (j > 0) ans.parts[j - 1] |= (ans.parts[j] & 0xFFFF) >>> 15;
                ans.parts[j] = (short)((ans.parts[j] & 0xFFFF) << 1);
            }
        }
        return ans.reduce();
    }

    BigFixed rshBF(int amount) {
        BigFixed ans = clone();
        for (int i = 0; i < amount; i++) {
			ans = ans.expand();
            for (int j = ans.size - 1; j >= 0; j--) {
                if (j < ans.size - 1) ans.parts[j + 1] |= (ans.parts[j] & 1) << 15;
                ans.parts[j] = (short)((ans.parts[j] & 0xFFFF) >>> 1);
            }
			ans = ans.reduce();
        }
        return ans;
    }

    BigFixed lshParts(int amount) {
        BigFixed ans = clone();
        for (int i = 0; i < amount; i++) {
            for (int j = 1; j < ans.size; j++) {
                ans.parts[j - 1] = ans.parts[j];
            }
            ans.parts[ans.size - 1] = 0;
        }
        return ans.reduce();
    }

    BigFixed rshParts(int amount) {
        BigFixed ans = clone();
        for (int i = 0; i < amount; i++) {
			ans = ans.expand();
            for (int j = ans.size - 1; j >= 0; j--) {
                if (j < ans.size - 1) ans.parts[j + 1] = ans.parts[j];
            }
            ans.parts[0] = 0;
			ans = ans.reduce();
        }
        return ans;
    }
    
    public BigFixed abs() {
        BigFixed ans = clone();
        ans.sign = false;
        return ans;
    }
    
    public BigFixed reduce() {
        BigFixed ans = clone();
        boolean empty = true;
        for (int i = ans.size - 1; i > 0 && empty; i--) {
            if (ans.parts[i] == (short)0) {
                ans.size--;
				if (ans.size == 0) System.out.println("How is this zero length??");
            } else {
                empty = false;
            }
        }
        return ans;
    }

    public static BigFixed fromDouble(double a) {
        BigFixed ans = new BigFixed();
        long raw = Double.doubleToRawLongBits(a);
        ans.sign = raw >>> 63 != 0;
        int exponent = (int)((raw & 0x7FF0000000000000l) >>> 52) - 1023;
        ans.size = 5;
        ans.parts[0] = 1;
        ans.parts[1] = (short)((raw & 0xFFFF000000000l) >> 36);
        ans.parts[2] = (short)((raw & 0xFFFF00000l) >> 20);
        ans.parts[3] = (short)((raw & 0xFFFF0l) >> 4);
        ans.parts[4] = (short)(raw & 0xF);
        // Then shift for days
        if (exponent < 0) ans = ans.rshBF(-exponent);
        if (exponent > 0) ans = ans.lshBF(exponent);
        return ans.reduce();
    }

    public static BigFixed fromInt(int a) {
        return fromShort((short)a);
    }

    public static BigFixed fromShort(short a) {
        BigFixed ans = new BigFixed();
        ans.sign = (a & 0x8000) >> 15 != 0;
        ans.size = 1;
        if (ans.sign) {
			ans.parts[0] = (short)(~(a - 1) & 0xFFFF);
        } else {
			ans.parts[0] = (short)(a & 0xFFFF);
		}
        return ans;
    }

    public static BigFixed fromBitString(String s) {
        BigFixed ans = new BigFixed();
        ans.sign = s.charAt(0) == '-';
        ans.size = (s.length() - 2) / 16;
        for (int i = 0; i < ans.size; i++) {
			for (int b = 0; b < 16; b++) {
				ans.parts[i] |= (s.charAt(i * 16 + b + (i > 0 ? 2 : 1)) == '0' ? 0 : 1) << (15 - b);
			}
		}
        return ans;
    }

    public BigFixed negate() {
        BigFixed ans = clone();
        ans.sign = !ans.sign;
        return ans;
    }

    public BigFixed add(BigFixed b) {
        BigFixed a = clone();
		if (a.sign != b.sign) return sub(b.negate());
        BigFixed ans = new BigFixed();
        ans.size = Math.max(a.size, b.size);
        ans.sign = a.sign;
		ans.overflow = a.overflow || b.overflow;
        int carry = 0;
        for (int i = ans.size - 1; i >= 0; i--) {
            int part = carry;
            if (i < a.size) part += a.parts[i] & 0xFFFF;
            if (i < b.size) part += b.parts[i] & 0xFFFF;
            carry = part >>> 16;
            ans.parts[i] = (short)(part & 0xFFFF);
        }
		if (carry > 0) {
//			System.out.println("Addition overflow");
			ans.overflow = true;
		}
//        ans.sign = carry > 1 ? !ans.sign : ans.sign;
        return ans.reduce();
    }
	
    public BigFixed sub(BigFixed b) {
        BigFixed a = clone();
		if (a.sign != b.sign) return add(b.negate());
        BigFixed ans = new BigFixed();
        ans.size = Math.max(a.size, b.size);
        ans.sign = a.sign;
		ans.overflow = a.overflow || b.overflow;
        if (b.abs().compareTo(a.abs()) > 0) {
            a = b;
            b = clone();
			ans.sign = !ans.sign;
        }
        int borrow = 0;
        for (int i = ans.size - 1; i >= 0; i--) {
            int part = 0x10000 - borrow;
            if (i < a.size) part += a.parts[i] & 0xFFFF;
            if (i < b.size) part -= b.parts[i] & 0xFFFF;
            borrow = (part >>> 16) ^ 1;
            ans.parts[i] = (short)(part & 0xFFFF);
        }
		if (borrow > 0) {
//			System.out.println("Subtraction overflow");
			ans.overflow = true;
		}
        return ans.reduce();
    }

    public BigFixed mult(BigFixed b) {
        BigFixed a = clone();
        BigFixed ans = new BigFixed();
        ans.size = Math.min(FIXED_MAX, a.size + b.size - 1); // (size - 1 + (b.size - 1)) + 1;
		ans.overflow = a.overflow || b.overflow;
        for (int i = a.size - 1; i >= 0; i--) {
			BigFixed temp = new BigFixed();
			temp.size = Math.min(FIXED_MAX, b.size + 1);
			int carry = 0;
			for (int j = Math.min(FIXED_MAX - 2, b.size - 1); j >= 0; j--) {
				int part = carry;
				part += (a.parts[i] & 0xFFFF) * (b.parts[j] & 0xFFFF);
				carry = part >>> 16;
				temp.parts[j + 1] = (short)(part & 0xFFFF);
			}
			temp.parts[0] = (short)(carry & 0xFFFF);
			int shift = i - 1;
			if (carry > 0 && shift < 0) {
//				System.out.println("Multiplication overflow");
				ans.overflow = true;
			}
			if (shift < 0) {
				ans = ans.add(temp.lshParts(-shift));
			} else if (shift > 0) {
				ans = ans.add(temp.rshParts(shift));
			} else {
				ans = ans.add(temp);
			}
			
		}
		ans.sign = a.sign ^ b.sign;
        return ans.reduce();
    }

    public BigFixed div(BigFixed b) {
        // Just trying reusing the inverse code for now, there's probably a better way
        BigFixed div = b.abs();
        BigFixed rem = this.abs();
        BigFixed ans = new BigFixed();
        ans.size = 2;
        int pos = -1;
        while (rem.compareTo(div) > 0 && pos > -15) {
            div = div.lshBF(1);
            pos--;
        }
        while (!rem.equals(new BigFixed()) && pos < (FIXED_MAX -1) * 16) {
            if (rem.compareTo(div) >= 0) {
                rem = rem.sub(div);
                int tpos = pos + 16;
                ans.parts[tpos / 16] |= 1 << (15 - (tpos % 16));
                if (tpos >= ans.size * 16) ans.size++;
            }
            div = div.rshBF(1);
            pos++;
        }
        ans.sign = sign ^ b.sign;
        return ans.reduce();
    }
    
    /**
     * Fairly limited by the 16-bit integral part of the number
     */
    public BigFixed inverse() {
        // Only worked for powers of 2
//        int revI = 0;
//        for (int i = 0; i < 16; i++) {
//            revI |= ((parts[0] >> (16 - i)) & 1) << i;
//        }
//        int revF = 0;
//        if (size > 1) {
//            for (int i = 0; i < 16; i++) {
//                revF |= ((parts[1] >> (16 - i)) & 1) << i;
//            }
//        }
//        return new BigFixed(sign, 2, new short[] {(short)revF, (short)revI});
        
        BigFixed div = this;
        BigFixed rem = fromInt(1);
        BigFixed ans = new BigFixed();
        ans.size = 2;
        int pos = -1;
        while (rem.compareTo(div) > 0 && pos > -15) {
            div = div.lshBF(1);
            pos--;
        }
        while (!rem.equals(new BigFixed()) && pos < (FIXED_MAX -1) * 16) {
            if (rem.compareTo(div) >= 0) {
                rem = rem.sub(div);
                ans.parts[(pos + 16) / 16] |= 1 << (15 - ((pos + 16) % 16));
                if (pos >= ans.size * 16) ans.size++;
            }
            div = div.rshBF(1);
            pos++;
        }
        return ans;
    }

    
    public BigFixed sqrt() {
        BigFixed val = this;
        // TODO: Replace arbitrary precision with some form of convergence testing?
        for (int i = 0; i < 8; i++) {
            val = BigFixed.fromDouble(0.5).mult(val.add(this.div(val)));
        }
        return val;
    }
    
    public BigFixed square() {
        return mult(this);
    }
    
    public BigFixed pow(int p) {
        BigFixed ans = this;
        for (int i = 1; i < p; i++) {
            ans = ans.mult(this);
        }
        return ans;
    }
    
    /**
     * A poor approximation of sin
     * @return
     */
    public BigFixed sin() {
        BigFixed val = this;
        while (val.compareTo(PI().negate()) < 0) {
            val = val.add(PI().mult(fromInt(2)));
        }
        while (val.compareTo(PI()) > 0) {
            val = val.sub(PI().mult(fromInt(2)));
        }
        return val.sub(val.pow(3).div(fromInt(6)))
                  .add(val.pow(5).div(fromInt(120)))
                  .sub(val.pow(7).div(fromInt(5040)));
    }
    
    /**
     * A poor approximation of sinh
     * @return
     */
    public BigFixed sinh() {
        BigFixed val = this;
        return val.add(val.pow(3).div(fromInt(6)))
                  .add(val.pow(5).div(fromInt(120)))
                  .add(val.pow(7).div(fromInt(5040)));
    }
    
    /**
     * A poor approximation of cosh
     * @return
     */
    public BigFixed cosh() {
        BigFixed val = this;
        return val.add(val.pow(2).div(fromInt(2)))
                  .add(val.pow(4).div(fromInt(24)))
                  .add(val.pow(6).div(fromInt(720)));
    }
    
    /**
     * Uses the poor approximation of sin
     * @return
     */
    public BigFixed cos() {
        return add(PI().div(fromInt(2))).sin();
    }
    
    /**
     * A poor approximation of atan
     * @return
     */
    public BigFixed atan() {
        return sub(pow(3).div(fromInt(3))).add(pow(5).div(fromInt(5))).sub(pow(7).div(fromInt(7)));
    }
    
    public static BigFixed atan2(BigFixed x, BigFixed y) {
        int x0 = x.compareTo(new BigFixed());
        int y0 = y.compareTo(new BigFixed());
        if (x0 > 0) {
            return y.div(x).atan();
        } else if (x0 < 0) {
            if (y0 >= 0) {
                return y.div(x).atan().add(PI());
            } else {
                return y.div(x).atan().sub(PI());
            } 
        } else {
            if (y0 > 0) {
                return PI().rshBF(1);
            } else if (y0 < 0) {
                return PI().rshBF(1).negate();
            } else {
                return null;
            }
        }
    }

    public double toDouble() {
		if (compareTo(new BigFixed()) == 0) return 0;
		int firstOne = -1;
		for (int i = 0; i < size && firstOne == -1; i++) {
			for (int b = 15; b >= 0; b--) {
				if (((parts[i] & 0xFFFF) & (1 << b)) > 0) {
					firstOne = i * 16 + 15 - b;
					break;
				}
			}
		}
		int shift = 15 - firstOne;
		BigFixed shifted;
		if (shift > 0) {
			shifted = rshBF(shift);
		} else if (shift < 0) {
			shifted = lshBF(-shift);
		} else {
			shifted = clone();
		}
		long bits = (sign ? 1l : 0l) << 63 | (overflow ? 0x7FF : (1023l + shift)) << 52 |
				(long)(shifted.parts[1] & 0xFFFF) << 36 |
                (parts.length > 2 ? (long)(shifted.parts[2] & 0xFFFF) << 20 : 0) |
                (parts.length > 2 ? (shifted.parts[3] & 0xFFFF) << 4 : 0) |
                (parts.length > 2 ? (shifted.parts[4] & 0xF000) >> 12 : 0);
		return Double.longBitsToDouble(bits);
    }

    @Override
    public String toString() {
        String result = toDouble() + " ";
        result += sign ? '-' : " ";
        for (int i = 0; i < size; i++) {
            if (i == 1) result += '.';
            for (int j = 15; j >= 0; j--) {
                result += (parts[i] & 0xFFFF & (1 << j)) >>> j == 0 ? "0" : "1";
            }
        }
        return result + (overflow ? " overflowed" : "");
    }
    
    @Override
    public BigFixed clone() {
        BigFixed ans = new BigFixed();
        ans.size = size;
        ans.sign = sign;
        ans.parts = parts.clone();
		ans.overflow = overflow;
        return ans;
    }

    @Override
    public int compareTo(BigFixed o) {
        // this < o == -, this > o == +
		if (overflow && !o.overflow) return sign ? -1 : 1;
		if (!overflow && o.overflow) return o.sign ? -1 : 1;
        
        if (!sign && o.sign) return 1;
        if (sign && !o.sign) return -1;
        
        int comp = 0;
		if (!overflow && !o.overflow) {
			for (int i = 0; i < Math.max(size, o.size); i++) {
				int b1 = i < size ? parts[i] & 0xFFFF : 0;
				int b2 = i < o.size ? o.parts[i] & 0xFFFF : 0;
				if (b1 > b2) {
                    comp = 1;
                    break;
                }
				if (b2 > b1) {
                    comp = -1;
                    break;
                }
			}
		}
        if (sign && o.sign) return -comp;
        return comp;
    }
	
	public byte[] toClBytes() {
		byte[] bytes = new byte[clByteSize()];
		for (int i = 0; i < FIXED_MAX; i++) {
			bytes[i * 2] = (byte)(parts[i] & 0xFF);
			bytes[i * 2 + 1] = (byte)(parts[i] >> 8 & 0xFF);
		}
		bytes[FIXED_MAX * 2] = (byte)(size & 0xFF);
		bytes[FIXED_MAX * 2 + 1] = (byte)(size >> 8 & 0xFF);
		bytes[FIXED_MAX * 2 + 2] = (byte)(size >> 16 & 0xFF);
		bytes[FIXED_MAX * 2 + 3] = (byte)(size >> 24 & 0xFF);
		bytes[FIXED_MAX * 2 + 4] = sign ? (byte)1 : (byte)0;
		bytes[FIXED_MAX * 2 + 5] = 0;
		bytes[FIXED_MAX * 2 + 6] = 0;
		bytes[FIXED_MAX * 2 + 7] = 0;
		return bytes;
	}
	
	public static int clByteSize() {
		return FIXED_MAX * 2 + 5 + 3;
	}

}

