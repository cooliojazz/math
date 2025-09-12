package com.up.math.number;

/**
 * Represents an 32.32X fixed point number
 * @author Ricky
 */
public final class IntFixed extends BigFixed<IntFixed> {
//    public record IntFixed(boolean sign, int size, int[] parts, boolean overflow) extends BigFixed<com.up.math.number.IntFixed> {
    // TODO: It seems like it should be more possible to actually have dynamically sized numbers with how this is written now?
    
    final static int FIXED_MAX = 5;
    
    public static final IntFixed ZERO = IntFixed.fromInt(0);
    public static final IntFixed ONE = IntFixed.fromInt(1);
    public static final IntFixed PI = IntFixed.fromBitString(" 00000000000000000000000000000011.001001000011111101101010100010001000010110100011000010001101001100010011000110011000101000101110000000110111000001110011010001001010010000001001");

    private final boolean sign;
    private final int size;
    private final int[] parts;
    private final boolean overflow;
    
    public IntFixed(boolean sign, int size, int[] parts, boolean overflow) {
        this.sign = sign;
        this.size = size;
        this.parts = parts;
        this.overflow = overflow;
    }

	public IntFixed() {
        this(false, 1, new int[FIXED_MAX], false);
	}
    
    @Override
    public IntFixed zero() {
        return ZERO;
    }
    
    @Override
    public IntFixed one() {
        return ONE;
    }
    
    @Override
    public IntFixed pi() {
        return PI;
    }
    
    public boolean sign() {
        return sign;
    }
    
    public int size() {
        return size;
    }

    IntFixed expand() {
        int nSize = size + 1;
        if (nSize > FIXED_MAX) {
//            System.out.println("Warning: Fixed number size exceeded.");
            nSize = FIXED_MAX;
        }
        return new IntFixed(sign, nSize, parts, overflow);
    }

    // TODO: Rewrite these in the newer direct creation format
    IntFixed lshBF(int amount) {
        IntFixed ans = clone();
        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < ans.size; j++) {
                if (j > 0) ans.parts[j - 1] |= ans.parts[j] >>> 31;
                ans.parts[j] = ans.parts[j] << 1;
            }
        }
        return ans.reduce();
    }

    IntFixed rshBF(int amount) {
        IntFixed ans = clone();
        for (int i = 0; i < amount; i++) {
			ans = ans.expand();
            for (int j = ans.size - 1; j >= 0; j--) {
                if (j < ans.size - 1) ans.parts[j + 1] |= (ans.parts[j] & 1) << 31;
                ans.parts[j] = ans.parts[j] >>> 1;
            }
			ans = ans.reduce();
        }
        return ans;
    }

    public IntFixed lshParts(int amount) {
        IntFixed ans = clone();
        for (int i = 0; i < amount; i++) {
            for (int j = 1; j < ans.size; j++) {
                ans.parts[j - 1] = ans.parts[j];
            }
            ans.parts[ans.size - 1] = 0;
        }
        return ans.reduce();
    }

    public IntFixed rshParts(int amount) {
        IntFixed ans = clone();
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
    
    public IntFixed abs() {
        return new IntFixed(false, size, parts, overflow);
    }
    
    public IntFixed reduce() {
        boolean empty = true;
        int nSize = size;
        for (int i = nSize - 1; i > 0 && empty; i--) {
            if (parts[i] == 0) {
                nSize--;
				if (nSize == 0) System.out.println("How is this zero length??");
            } else {
                empty = false;
            }
        }
        return new IntFixed(sign, nSize, parts, overflow);
    }

    public static IntFixed fromDouble(double a) {
        long raw = Double.doubleToRawLongBits(a);
        int exponent = (int)((raw & 0x7FF0000000000000l) >>> 52) - 1023;
        int[] parts = new int[FIXED_MAX];
        parts[0] = 1;
        parts[1] = (int)((raw & 0xFFFFFFFF00000l) >> 20);
        parts[2] = (int)((raw & 0xFFFFFl) << 12);
        // Then shift for days
        IntFixed ans = new IntFixed(raw >>> 63 != 0, 3, parts, false);
        if (exponent < 0) ans = ans.rshBF(-exponent);
        if (exponent > 0) ans = ans.lshBF(exponent);
        return ans.reduce();
    }

    public static IntFixed fromInt(int a) {
        int[] parts = new int[FIXED_MAX];
        boolean sign = (a & 0x80000000) >> 31 != 0;
        if (sign) {
			parts[0] = ~(a - 1);
        } else {
			parts[0] = a;
		}
        return new IntFixed(sign, 1, parts, false);
    }

    public static IntFixed fromShort(short a) {
        int[] parts = new int[FIXED_MAX];
        boolean sign = (a & 0x8000) >> 15 != 0;
        if (sign) {
            parts[0] = ~(a - 1) & 0xFFFF;
        } else {
            parts[0] = a & 0xFFFF;
		}
        return new IntFixed(sign, 1, parts, false);
    }

    public static IntFixed fromBitString(String s) {
        int[] parts = new int[FIXED_MAX];
        int size = (s.length() - 2) / 32;
        for (int i = 0; i < size; i++) {
			for (int b = 0; b < 32; b++) {
				parts[i] |= (s.charAt(i * 32 + b + (i > 0 ? 2 : 1)) == '0' ? 0 : 1) << (31 - b);
			}
		}
        return new IntFixed(s.charAt(0) == '-', size, parts, false);
    }

    public IntFixed negate() {
        return new IntFixed(!sign, size, parts, overflow);
    }

    public IntFixed add(IntFixed b) {
		if (sign != b.sign) return sub(b.negate());
        int nSize = Math.max(size, b.size);
        int[] nParts = new int[FIXED_MAX];
		boolean nOverflow = overflow || b.overflow;
        long carry = 0;
        for (int i = nSize - 1; i >= 0; i--) {
            long part = carry;
            if (i < size) part += parts[i] & 0xFFFFFFFFL;
            if (i < b.size) part += b.parts[i] & 0xFFFFFFFFL;
            carry = part >>> 32;
            nParts[i] = (int)(part & 0xFFFFFFFFL);
        }
		if (carry > 0) {
            nOverflow = true;
		}
        return new IntFixed(sign, nSize, nParts, nOverflow);
    }

    public IntFixed sub(IntFixed b) {
        IntFixed a = clone();
		if (a.sign != b.sign) return add(b.negate());
        int nSize = Math.max(size, b.size);
        boolean nSign = sign;
        int[] nParts = new int[FIXED_MAX];
		boolean nOverflow = overflow || b.overflow;
        if (b.abs().compareTo(a.abs()) > 0) {
            a = b;
            b = clone();
            nSign = !nSign;
        }
        long borrow = 0;
        for (int i = nSize - 1; i >= 0; i--) {
            long part = 0x100000000L - borrow;
            if (i < a.size) part += a.parts[i] & 0xFFFFFFFFL;
            if (i < b.size) part -= b.parts[i] & 0xFFFFFFFFL;
            borrow = (part >>> 32) ^ 1;
            nParts[i] = (int)(part & 0xFFFFFFFFL);
        }
		if (borrow > 0) {
            nOverflow = true;
		}
        return new IntFixed(nSign, nSize, nParts, nOverflow).reduce();
    }

    public IntFixed mult(IntFixed b) {
        IntFixed ans = new IntFixed();
        int nSize = Math.min(FIXED_MAX, size + b.size - 1); // (size - 1 + (b.size - 1)) + 1;
		boolean nOverflow = overflow || b.overflow;
        for (int i = size - 1; i >= 0; i--) {
			IntFixed temp = new IntFixed(false, Math.min(FIXED_MAX, b.size + 1), new int[FIXED_MAX], false);
			long carry = 0;
			for (int j = Math.min(FIXED_MAX - 2, b.size - 1); j >= 0; j--) {
				long part = carry;
				part += (parts[i] & 0xFFFFFFFFL) * (b.parts[j] & 0xFFFFFFFFL);
				carry = part >>> 32;
				temp.parts[j + 1] = (int)(part & 0xFFFFFFFFL);
			}
			temp.parts[0] = (int)(carry & 0xFFFFFFFFL);
			int shift = i - 1;
			if (carry > 0 && shift < 0) {
                nOverflow = true;
			}
			if (shift < 0) {
				ans = ans.add(temp.lshParts(-shift));
			} else if (shift > 0) {
				ans = ans.add(temp.rshParts(shift));
			} else {
				ans = ans.add(temp);
			}

		}
        return new IntFixed(sign ^ b.sign, nSize, ans.parts, nOverflow).reduce();
    }

    public IntFixed div(IntFixed b) {
        // Just trying reusing the inverse code for now, there's probably a better way
        IntFixed div = b.abs();
        IntFixed rem = abs();
        int[] nParts = new int[FIXED_MAX];
        int nSize = 2;
        int pos = -1;
        while (rem.compareTo(div) > 0 && pos > -31) {
            div = div.lshBF(1);
            pos--;
        }
        while (!rem.equals(new IntFixed()) && pos < (FIXED_MAX - 1) * 32) {
            if (rem.compareTo(div) >= 0) {
                rem = rem.sub(div);
                int tpos = pos + 32;
                nParts[tpos / 32] |= 1 << (31 - (tpos % 32));
                if (tpos >= nSize * 32) nSize++;
            }
            div = div.rshBF(1);
            pos++;
        }
        return new IntFixed(sign ^ b.sign, nSize, nParts, false).reduce();
    }
//    
//    /**
//     * Fairly limited by the 32-bit integer part of the number
//     */
//    public BigFixed inverse() {
//        // Only worked for powers of 2
////        int revI = 0;
////        for (int i = 0; i < 16; i++) {
////            revI |= ((parts[0] >> (16 - i)) & 1) << i;
////        }
////        int revF = 0;
////        if (size > 1) {
////            for (int i = 0; i < 16; i++) {
////                revF |= ((parts[1] >> (16 - i)) & 1) << i;
////            }
////        }
////        return new BigFixed(sign, 2, new int[] {(int)revF, (int)revI});
//        
//        BigFixed div = this;
//        BigFixed rem = fromInt(1);
//        BigFixed ans = new BigFixed();
//        ans.size = 2;
//        int pos = -1;
//        while (rem.compareTo(div) > 0 && pos > -15) {
//            div = div.lshBF(1);
//            pos--;
//        }
//        while (!rem.equals(new BigFixed()) && pos < (FIXED_MAX - 1) * 16) {
//            if (rem.compareTo(div) >= 0) {
//                rem = rem.sub(div);
//                ans.parts[(pos + 16) / 16] |= 1 << (15 - ((pos + 16) % 16));
//                if (pos >= ans.size * 16) ans.size++;
//            }
//            div = div.rshBF(1);
//            pos++;
//        }
//        return ans;
//    }

    public IntFixed inverse() {
        return ONE.div(this);
    }

    
    public IntFixed sqrt() {
        IntFixed val = this;
        // TODO: Replace arbitrary precision with some form of convergence testing?
        IntFixed two = IntFixed.fromInt(2);
        for (int i = 0; i < 8; i++) {
//            val = BigFixed.fromDouble(0.5).mult(val.add(this.div(val)));
            val = val.add(this.div(val)).div(two);
        }
        return val;
    }

    public IntFixed square() {
        return mult(this);
    }

    public IntFixed pow(int p) {
        IntFixed ans = this;
        for (int i = 1; i < p; i++) {
            ans = ans.mult(this);
        }
        return ans;
    }

    /**
     * A poor approximation of sin
     * @return
     */
    public IntFixed sin() {
        IntFixed val = this;
        while (val.compareTo(PI.negate()) < 0) {
            val = val.add(PI.mult(fromInt(2)));
        }
        while (val.compareTo(PI) > 0) {
            val = val.sub(PI.mult(fromInt(2)));
        }
        return val.sub(val.pow(3).div(fromInt(6)))
                  .add(val.pow(5).div(fromInt(120)))
                  .sub(val.pow(7).div(fromInt(5040)));
    }

    /**
     * A poor approximation of sinh
     * @return
     */
    public IntFixed sinh() {
        IntFixed val = this;
        return val.add(val.pow(3).div(fromInt(6)))
                  .add(val.pow(5).div(fromInt(120)))
                  .add(val.pow(7).div(fromInt(5040)));
    }

    /**
     * A poor approximation of cosh
     * @return
     */
    public IntFixed cosh() {
        IntFixed val = this;
        return val.add(val.pow(2).div(fromInt(2)))
                  .add(val.pow(4).div(fromInt(24)))
                  .add(val.pow(6).div(fromInt(720)));
    }

    /**
     * Uses the poor approximation of sin
     * @return
     */
    public IntFixed cos() {
        return add(PI.div(fromInt(2))).sin();
    }

    /**
     * A poor approximation of atan
     * @return
     */
    public IntFixed atan() {
        return sub(pow(3).div(fromInt(3))).add(pow(5).div(fromInt(5))).sub(pow(7).div(fromInt(7)));
    }

    public double toDouble() {
		if (abs().compareTo(new IntFixed()) == 0) return 0;
		int firstOne = -1;
		for (int i = 0; i < size && firstOne == -1; i++) {
			for (int b = 31; b >= 0; b--) {
				if (((long)parts[i] & (1L << b)) > 0) {
					firstOne = i * 32 + 31 - b;
					break;
				}
			}
		}
		int shift = 31 - firstOne;
		IntFixed shifted;
		if (shift > 0) {
			shifted = rshBF(shift);
		} else if (shift < 0) {
			shifted = lshBF(-shift);
		} else {
			shifted = clone();
		}
		long bits = (sign ? 1l : 0l) << 63 |
                    (overflow ? 0x7FFL : (1023l + shift)) << 52 |
				    (shifted.parts[1] & 0xFFFFFFFFL) << 20 | (shifted.parts[2] & 0xFFFFF000L) >> 12;
		return Double.longBitsToDouble(bits);
    }

    @Override
    public String toString() {
        String result = toDouble() + " ";
        result += sign ? '-' : " ";
        for (int i = 0; i < size; i++) {
            if (i == 1) result += '.';
            for (int j = 31; j >= 0; j--) {
                result += (parts[i] & (1 << j)) >>> j == 0 ? "0" : "1";
            }
        }
        return result + (overflow ? " overflowed" : "");
    }
    
    @Override
    public IntFixed clone() {
        return new IntFixed(sign, size, parts.clone(), overflow);
    }

    @Override
    public int compareTo(IntFixed o) {
        // this < o == -, this > o == +
		if (overflow && !o.overflow) return sign ? -1 : 1;
		if (!overflow && o.overflow) return o.sign ? -1 : 1;
        
        if (!sign && o.sign) return 1;
        if (sign && !o.sign) return -1;
        
        int comp = 0;
		if (!overflow && !o.overflow) {
			for (int i = 0; i < Math.max(size, o.size); i++) {
				long b1 = i < size ? parts[i] & 0xFFFFFFFFL : 0;
				long b2 = i < o.size ? o.parts[i] & 0xFFFFFFFFL : 0;
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

}

