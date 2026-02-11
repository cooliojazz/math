package com.up.math.number;

import java.util.function.Function;

/**
 * Represents an 16.X fixed point number
 * @author Ricky
 */
public class ShortFixed extends BigFixed<ShortFixed, InternalPrecision> {
    // TODO: Rework this into a record class with everything properly immutable, especially since most methods are essentially written that way already.
    //       Actually, just make it follow IntFixed's ways
    final static int FIXED_MAX = 10;

    // While still mutable
    public static ShortFixed PI() {return ShortFixed.fromBitString(" 0000000000000011.001001000011111101101010100010001000010110100011000010001101001100010011000110011000101000101110000000110111000001110011010001001010010000001001");}
//    public static final ShortFixed PI = ShortFixed.fromBitString(" 0000000000000011.001001000011111101101010100010001000010110100011000010001101001100010011000110011000101000101110000000110111000001110011010001001010010000001001");

    int size = 1;
    boolean sign = false;
    short[] parts = new short[FIXED_MAX];
	boolean overflow = false;

	public ShortFixed() {
	}

	public ShortFixed(boolean sign, int size, short[] parts, boolean overflow) {
		this.sign = sign;
		this.size = size;
		this.parts = parts;
        this.overflow = overflow;
	}
    
    public ShortFixed(double d) {
        ShortFixed temp = fromDouble(d);
        this.sign = temp.sign;
        this.size = temp.size;
        this.parts = temp.parts;
        this.overflow = temp.overflow;
    }

    @Override
    public ShortFixed zero() {
        return ShortFixed.fromInt(0);
    }

    @Override
    public ShortFixed one() {
        return ShortFixed.fromInt(1);
    }

    @Override
    public ShortFixed two() {
        return ShortFixed.fromInt(2);
    }

    public ShortFixed halfPi() {
        return PI().div(two());
    }
    
    @Override
    public ShortFixed pi() {
        return PI();
    }
    
    public ShortFixed tau() {
        return PI().mult(two());
    }

    @Override
    public ShortFixed e() {
        // TODO: Add accurate constants
        return fromDouble(Math.E);
    }
    
    public ShortFixed l2e() {
        return ShortFixed.fromDouble(1.442695040888963407359924681);
    }

    @Override
    public ShortFixed sqrt2() {
        return fromDouble(Math.sqrt(2));
    }

    @Override
    public boolean sign() {
        return sign;
    }

    ShortFixed expand() {
        ShortFixed ans = clone();
        ans.size++;
        if (ans.size > FIXED_MAX) {
//            System.out.println("Warning: Fixed number size exceeded.");
            ans.size = FIXED_MAX;
        }
        return ans;
    }

    public ShortFixed lshBits(int amount) {
        if (amount < 0) return rshBits(Math.abs(amount));
        ShortFixed ans = clone();
        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < ans.size; j++) {
                if (j > 0) ans.parts[j - 1] |= (ans.parts[j] & 0xFFFF) >>> 15;
                ans.parts[j] = (short)((ans.parts[j] & 0xFFFF) << 1);
            }
        }
        return ans.reduce();
    }

    public ShortFixed rshBits(int amount) {
        if (amount < 0) return lshBits(Math.abs(amount));
        ShortFixed ans = clone();
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

    public ShortFixed lshParts(int amount) {
        ShortFixed ans = clone();
        for (int i = 0; i < amount; i++) {
            for (int j = 1; j < ans.size; j++) {
                ans.parts[j - 1] = ans.parts[j];
            }
            ans.parts[ans.size - 1] = 0;
        }
        return ans.reduce();
    }

    public ShortFixed rshParts(int amount) {
        ShortFixed ans = clone();
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

    public ShortFixed abs() {
        ShortFixed ans = clone();
        ans.sign = false;
        return ans;
    }

    public ShortFixed reduce() {
        ShortFixed ans = clone();
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

    public static ShortFixed fromDouble(double a) {
        ShortFixed ans = new ShortFixed();
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
        if (exponent < 0) ans = ans.rshBits(-exponent);
        if (exponent > 0) ans = ans.lshBits(exponent);
        return ans.reduce();
    }

    public static ShortFixed fromInt(int a) {
        return fromShort((short)a);
    }

    public static ShortFixed fromShort(short a) {
        ShortFixed ans = new ShortFixed();
        ans.sign = (a & 0x8000) >> 15 != 0;
        ans.size = 1;
        if (ans.sign) {
			ans.parts[0] = (short)(~(a - 1) & 0xFFFF);
        } else {
			ans.parts[0] = (short)(a & 0xFFFF);
		}
        return ans;
    }

    public static ShortFixed fromBitString(String s) {
        ShortFixed ans = new ShortFixed();
        ans.sign = s.charAt(0) == '-';
        ans.size = (s.length() - 2) / 16;
        for (int i = 0; i < ans.size; i++) {
			for (int b = 0; b < 16; b++) {
				ans.parts[i] |= (s.charAt(i * 16 + b + (i > 0 ? 2 : 1)) == '0' ? 0 : 1) << (15 - b);
			}
		}
        return ans;
    }
    
    public static ShortFixed fromHexString(String s) {
        short[] parts = new short[FIXED_MAX];
        int size = Math.min((s.length() - 2) / 8, FIXED_MAX);
        for (int i = 0; i < size; i++) {
            int start = i * 4 + 1 + (i > 0 ? 1 : 0);
            parts[i] = (short)Integer.parseUnsignedInt(s.substring(start, start + 4), 16);
		}
        return new ShortFixed(s.charAt(0) == '-', size, parts, false);
    }

    public ShortFixed negate() {
        ShortFixed ans = clone();
        ans.sign = !ans.sign;
        return ans;
    }

    public ShortFixed add(ShortFixed b) {
        ShortFixed a = clone();
		if (a.sign != b.sign) return sub(b.negate());
        ShortFixed ans = new ShortFixed();
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

    public ShortFixed sub(ShortFixed b) {
        ShortFixed a = clone();
		if (a.sign != b.sign) return add(b.negate());
        ShortFixed ans = new ShortFixed();
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

    // TODO: Not sure if this is working right yet?
    public ShortFixed mult(ShortFixed b) {
        int nSize = Math.min(FIXED_MAX, size + b.size - 1); // (size - 1 + (b.size - 1)) + 1;
        short[] nParts = new short[FIXED_MAX];
		boolean nOverflow = overflow || b.overflow;
        for (int i = size - 1; i >= 0; i--) {
			int carry = 0;
			for (int j = b.size - 1; j >= 0; j--) {
                int pos = i + j;
                int part = carry;
                part += (parts[i] & 0xFFFF) * (b.parts[j] & 0xFFFF);
                if (pos < nSize) {
                    part += nParts[pos] & 0xFFFF;
                    nParts[pos] = (short)(part & 0xFFFF);
                }
				carry = part >>> 16;
            }
            if (i > 0) {
                nParts[i - 1] = (short)(carry & 0xFFFF + nParts[i - 1] & 0xFFFF);
            } else if (carry > 0) {
                nOverflow = true;
            }
		}
        return new ShortFixed(sign ^ b.sign, nSize, nParts, nOverflow).reduce();
        // TODO: Missing overflow param in construct
//        return new ShortFixed(sign ^ b.sign, nSize, nParts, nOverflow).reduce();
    }

    public ShortFixed div(ShortFixed b) {
        // Just trying reusing the inverse code for now, there's probably a better way
        ShortFixed div = b.abs();
        ShortFixed rem = this.abs();
        ShortFixed ans = new ShortFixed();
        ans.size = 2;
        int pos = -1;
        while (rem.compareTo(div) > 0 && pos > -15) {
            div = div.lshBits(1);
            pos--;
        }
        while (!rem.equals(new ShortFixed()) && pos < (FIXED_MAX -1) * 16) {
            if (rem.compareTo(div) >= 0) {
                rem = rem.sub(div);
                int tpos = pos + 16;
                ans.parts[tpos / 16] |= 1 << (15 - (tpos % 16));
                if (tpos >= ans.size * 16) ans.size++;
            }
            div = div.rshBits(1);
            pos++;
        }
        ans.sign = sign ^ b.sign;
        return ans.reduce();
    }

    /**
     * Fairly limited by the 16-bit integral part of the number
     */
    public ShortFixed inverse() {
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
//        return new ShortFixed(sign, 2, new short[] {(short)revF, (short)revI});

        ShortFixed div = this;
        ShortFixed rem = fromInt(1);
        ShortFixed ans = new ShortFixed();
        ans.size = 2;
        int pos = -1;
        while (rem.compareTo(div) > 0 && pos > -15) {
            div = div.lshBits(1);
            pos--;
        }
        while (!rem.equals(new ShortFixed()) && pos < (FIXED_MAX -1) * 16) {
            if (rem.compareTo(div) >= 0) {
                rem = rem.sub(div);
                ans.parts[(pos + 16) / 16] |= 1 << (15 - ((pos + 16) % 16));
                if (pos >= ans.size * 16) ans.size++;
            }
            div = div.rshBits(1);
            pos++;
        }
        return ans;
    }


    public ShortFixed sqrt() {
        ShortFixed val = this;
        // TODO: Replace arbitrary precision with some form of convergence testing?
        for (int i = 0; i < 8; i++) {
            val = fromDouble(0.5).mult(val.add(this.div(val)));
        }
        return val;
    }

    public ShortFixed square() {
        return mult(this);
    }

    public ShortFixed pow(int p) {
        ShortFixed ans = this;
        for (int i = 1; i < p; i++) {
            ans = ans.mult(this);
        }
        return ans;
    }

    public ShortFixed pow(ShortFixed p) {
        // TODO: Faster way
        return p.mult(log2()).exp2();
    }

    /**
     * A poor approximation of sin
     * @return
     */
    public ShortFixed sin() {
        ShortFixed val = this;
        if (val.compareTo(pi().negate()) < 0) {
            val = sub(tau().mult(add(pi()).div(tau()).integ())).add(tau());
        } else {
            val = sub(tau().mult(add(pi()).div(tau()).integ()));
        }
        if (val.abs().compareTo(halfPi()) > 0) {
            return pi().sub(val).sin();
        }
        return val.sub(val.pow(3).div(fromInt(6)))
                  .add(val.pow(5).div(fromInt(120)))
                  .sub(val.pow(7).div(fromInt(5040)));
    }

    /**
     * A poor approximation of sinh
     * @return
     */
    public ShortFixed sinh() {
//        ShortFixed val = this;
//        return val.add(val.pow(3).div(fromInt(6)))
//                  .add(val.pow(5).div(fromInt(120)))
//                  .add(val.pow(7).div(fromInt(5040)));
        return exp().sub(negate().exp()).div(two());
    }

    /**
     * A poor approximation of cosh
     * @return
     */
    public ShortFixed cosh() {
//        ShortFixed val = this;
//        return val.add(val.pow(2).div(fromInt(2)))
//                  .add(val.pow(4).div(fromInt(24)))
//                  .add(val.pow(6).div(fromInt(720)));
        return exp().add(negate().exp()).div(two());
    }

    /**
     * Uses the poor approximation of sin
     * @return
     */
    public ShortFixed cos() {
        return add(PI().div(fromInt(2))).sin();
    }

    /**
     * A poor approximation of atan
     * @return
     */
    public ShortFixed atan() {
        return sub(pow(3).div(fromInt(3))).add(pow(5).div(fromInt(5))).sub(pow(7).div(fromInt(7)));
    }
    
    @Override
    public ShortFixed mod(ShortFixed n) {
        // TODO: Probably a better way to do this
        return n.mult(this.div(n).floor());
    }
    
    // TODO: These are wrong for negatives
    public ShortFixed floor() {
        ShortFixed val = new ShortFixed();
        val.parts[0] = parts[0];
        return val;
    }
    
    public ShortFixed ceil() {
        ShortFixed val = floor();
        if (!val.equals(this)) val.parts[0] = (short)(val.parts[0] + 1);
        return val;
    }
    
    public ShortFixed integ() {
        short[] nParts = new short[FIXED_MAX];
        nParts[0] = parts[0];
        return new ShortFixed(sign, 1, nParts, overflow);
    }
    
    public ShortFixed frac() {
        short[] nParts = new short[FIXED_MAX];
        System.arraycopy(parts, 1, nParts, 1, FIXED_MAX - 1);
        return new ShortFixed(sign, size, nParts, overflow);
    }

    public static ShortFixed atan2(ShortFixed x, ShortFixed y) {
        int x0 = x.compareTo(new ShortFixed());
        int y0 = y.compareTo(new ShortFixed());
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
                return PI().rshBits(1);
            } else if (y0 < 0) {
                return PI().rshBits(1).negate();
            } else {
                return null;
            }
        }
    }
    
    public static ShortFixed exp2(int p) {
        short[] parts = new short[FIXED_MAX];
        if (p > 15) return new ShortFixed(false, 1, parts, true);
        p += (FIXED_MAX - 1) * 16;
        if (p < 0) return new ShortFixed(false, 1, parts, false);
        parts[FIXED_MAX - 1 - p / 16] |= (short)(1l << (p % 16));
        return new ShortFixed(false, FIXED_MAX, parts, false).reduce();
    }
        
//    @Override
//    public IntFixed exp2() {
//        // Only 2 term taylor approximation not extremely accurate but reasonably fast
//        // TODO: offset calculation to put frac() within [-0.5, 0.5] for better accuracy? Done, but did it help?
//        IntFixed f = frac().div(TWO);
//        if (compareTo(fromInt(33)) > 0) return new IntFixed(false, 1, new int[FIXED_MAX], true);
//        // TODO: Move these range checks instead the respective shift functions
//                return one().lshBits(Math.max(-(FIXED_MAX - 1) * 32, Math.min(32, integ().toInt()))).mult(
//                            one()
//                            .add(f.mult(L2))
//                            .add(f.square().mult(L22).div(TWO))
//                            .add(f.pow(3).mult(L32).div(fromInt(6)))
//                            .add(f.pow(4).mult(L42).div(fromInt(24)))
//                        ).square();
//    }
    
    private static ShortFixed[] sqrts;
    static {
        sqrts = new ShortFixed[32];
        sqrts[0] = fromInt(2);
        for (int i = 1; i < sqrts.length; i++) {
            sqrts[i] = sqrts[i - 1].sqrt();
        }
    }
    
    @Override
    public ShortFixed exp2() {
        // TODO: offset calculation to put frac() within [-0.5, 0.5] for better accuracy? Done, but did it help?
        ShortFixed f = frac().div(two());
        if (compareTo(fromInt(33)) > 0) return new ShortFixed(false, 1, new short[FIXED_MAX], true);
        ShortFixed temp = f;
        ShortFixed ans = one();
        int n = 1;
        // TODO: Better option than arbitrary (Up to (FIXED_MAX - 1) * 32 for max accuracy) iterations here?
        for (; temp.compareTo(zero()) != 0 && n < 32; n++) {
            temp = temp.lshBits(1);
            if ((temp.parts[0] & 0x1) > 0) {
                ans = sign ? ans.div(sqrts[n - 1]) : ans.mult(sqrts[n - 1]);
            }
        }
        return ans.mult(exp2(integ().toShort()));
    }
    
    @Override
    public ShortFixed exp() {
        return mult(l2e()).exp2();
    }

    @Override
    public ShortFixed log2() {
        // TODO: Not sure if null is right here
        if (compareTo(zero()) <= 0) return new ShortFixed(true, 1, ShortFixed.fromHexString(" FFFF").parts, true);
        int n = 0;
        ShortFixed temp = this;
        while (temp.compareTo(one()) < 0) {
            temp = temp.lshBits(1);
            n--;
        }
        while (temp.compareTo(two()) >= 0) {
            temp = temp.rshBits(1);
            n++;
        }
        
        ShortFixed frac = zero();
        int factor = 0;
//        while (temp.compareTo(ONE) != 0 && factor > -(precision.getSize() - 1) * 16l) {
        // This seems like a decent amount of accuracy for now for the speed
        while (temp.compareTo(one()) != 0 && factor > -16) {
            int m = 0;
            ShortFixed temp2 = temp;
            while (temp2.compareTo(two()) < 0) {
                temp2 = temp2.square();
                m++;
            }
            temp = temp2.rshBits(1);
            factor -= m;
            frac = frac.add(one().lshBits(factor));
        }
        return ShortFixed.fromInt(n).add(frac);
    }

    @Override
    public ShortFixed log() {
        return log2().div(l2e());
    }
    
    public short toShort() {
        return (short)((sign ? 1 : 0) + (parts[0] & 0x7FFF ^ (sign ? 0xFFFF : 0)));
    }

    public double toDouble() {
		if (abs().compareTo(new ShortFixed()) == 0) return 0;
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
		ShortFixed shifted;
		if (shift > 0) {
			shifted = rshBits(shift);
		} else if (shift < 0) {
			shifted = lshBits(-shift);
		} else {
			shifted = clone();
		}
		long bits = (sign ? 1l : 0l) << 63 | (overflow ? 0x7FF : (1023l + shift)) << 52 |
				(long)(shifted.parts[1] & 0xFFFF) << 36 |
                (parts.length > 2 ? (long)(shifted.parts[2] & 0xFFFF) << 20 : 0) |
                (parts.length > 3 ? (shifted.parts[3] & 0xFFFF) << 4 : 0) |
                (parts.length > 4 ? (shifted.parts[4] & 0xF000) >> 12 : 0);
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
    public ShortFixed clone() {
        ShortFixed ans = new ShortFixed();
        ans.size = size;
        ans.sign = sign;
        ans.parts = parts.clone();
		ans.overflow = overflow;
        return ans;
    }

    @Override
    public int compareTo(ShortFixed o) {
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
    
//    @Override
//    public DoubleConverter<ShortFixed<P>> getConverter() {
//        return d -> fromDouble(precision, d);
//    }
    
    @Override
    public Function<Double, ShortFixed> getConverter() {
        return ShortFixed::fromDouble;
    }

}

