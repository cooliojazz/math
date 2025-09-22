package com.up.math.number;

/**
 * Represents an 32.32X fixed point number
 * @author Ricky
 */
public final class IntFixed extends BigFixed<IntFixed> {
//    public record IntFixed(boolean sign, int size, int[] parts, boolean overflow) extends BigFixed<com.up.math.number.IntFixed> {
    // TODO: It seems like it should be more possible to actually have dynamically sized numbers with how this is written now?
    
    final static int FIXED_MAX = 3;
    
    public static final IntFixed ZERO = IntFixed.fromInt(0);
    public static final IntFixed ONE = IntFixed.fromInt(1);
    public static final IntFixed TWO = IntFixed.fromInt(2);
    public static final IntFixed PI = IntFixed.fromBitString(" 00000000000000000000000000000011.001001000011111101101010100010001000010110100011000010001101001100010011000110011000101000101110000000110111000001110011010001001010010000001001");
    public static final IntFixed TAU = PI.mult(IntFixed.TWO);
    public static final IntFixed E = IntFixed.fromBitString(" 00000000000000000000000000000010.101101111110000101010001011000101000101011101101001010100110101010111111011100010101100010000000100111001111010011110011110001110110001011100111");
    public static final IntFixed SQ2 = IntFixed.fromBitString(" 00000000000000000000000000000001.011010100000100111100110011001111111001110111100110010010000100010110010111110110001001101100110111010101001010101111101001111100011101011011110");

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
    
    public IntFixed(double d) {
        IntFixed temp = fromDouble(d);
        this.sign = temp.sign;
        this.size = temp.size;
        this.parts = temp.parts;
        this.overflow = temp.overflow;
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
    public IntFixed two() {
        return TWO;
    }
    
    @Override
    public IntFixed pi() {
        return PI;
    }
    
    @Override
    public IntFixed e() {
        return E;
    }
    
    @Override
    public IntFixed sqrt2() {
        return SQ2;
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
    public IntFixed lshBits(int amount) {
        if (amount < 0) return rshBits(Math.abs(amount));
        IntFixed ans = clone();
        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < ans.size; j++) {
                if (j > 0) ans.parts[j - 1] |= ans.parts[j] >>> 31;
                ans.parts[j] = ans.parts[j] << 1;
            }
        }
        return ans.reduce();
    }

    public IntFixed rshBits(int amount) {
        if (amount < 0) return lshBits(Math.abs(amount));
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
        if (amount < 0) return rshParts(Math.abs(amount));
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
        if (amount < 0) return lshParts(Math.abs(amount));
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
        if (parts.length > 2) parts[2] = (int)((raw & 0xFFFFFl) << 12);
        // Then shift for days
        IntFixed ans = new IntFixed(raw >>> 63 != 0, Math.min(3, parts.length), parts, false);
        if (exponent < 0) ans = ans.rshBits(-exponent);
        if (exponent > 0) ans = ans.lshBits(exponent);
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
        int size = Math.min((s.length() - 2) / 32, FIXED_MAX);
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
        int nSize = Math.min(FIXED_MAX, size + b.size - 1); // (size - 1 + (b.size - 1)) + 1;
        int[] nParts = new int[FIXED_MAX];
		boolean nOverflow = overflow || b.overflow;
        for (int i = size - 1; i >= 0; i--) {
			long carry = 0;
			for (int j = b.size - 1; j >= 0; j--) {
                int pos = i + j;
                long part = carry;
                part += (parts[i] & 0xFFFFFFFFL) * (b.parts[j] & 0xFFFFFFFFL);
                if (pos < nSize) {
                    part += nParts[pos] & 0xFFFFFFFFL;
                    nParts[pos] = (int)(part & 0xFFFFFFFFL);
                }
				carry = part >>> 32;
            }
            if (i > 0) {
                nParts[i - 1] = (int)(carry & 0xFFFFFFFFL + nParts[i - 1] & 0xFFFFFFFFL);
            } else if (carry > 0) {
                nOverflow = true;
            }
		}
        return new IntFixed(sign ^ b.sign, nSize, nParts, nOverflow).reduce();
    }

    public IntFixed div(IntFixed b) {
        // Just trying reusing the inverse code for now, there's probably a better way
        IntFixed div = b.abs();
        IntFixed rem = abs();
        int[] nParts = new int[FIXED_MAX];
        int nSize = 2;
        int pos = -1;
        while (rem.compareTo(div) > 0 && pos > -31) {
            div = div.lshBits(1);
            pos--;
        }
        while (!rem.equals(new IntFixed()) && pos < (FIXED_MAX - 1) * 32) {
            if (rem.compareTo(div) >= 0) {
                rem = rem.sub(div);
                int tpos = pos + 32;
                nParts[tpos / 32] |= 1 << (31 - (tpos % 32));
                if (tpos >= nSize * 32) nSize++;
            }
            div = div.rshBits(1);
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
        for (int i = 0; i < 5; i++) {
            val = val.add(this.div(val)).div(TWO);
        }
        return val;
    }
    
    // So far pointless, cant eliminate the useage of pow in pow
//    public IntFixed powNew(IntFixed p) {
//        // Needs inital estimate to converge faster
//        IntFixed val = this;
//        // TODO: Replace arbitrary precision with some form of convergence testing?
//        for (int i = 0; i < 10; i++) {
////            val = val.sub(this.div(val)).div(TWO);
////            val = val.mult(p.sub(ONE).add(this.div(val.pow(p)))).div(p);
////            val = val.mult(p.sub(ONE)).add(this.div(val.pow(p.sub(ONE)))).div(p);
////            val = val.mult(this.div(val.pow(p)).add(p).sub(ONE)).div(p);
//            val = val.mult(ONE.add(p.mult(this.div(val.pow(p.inverse())).sub(ONE))));
//        }
//        return val;
//    }

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

    public IntFixed pow(IntFixed p) {
        // TODO: Faster way
        return p.mult(log2()).exp2();
    }

    /**
     * A poor approximation of sin
     * @return
     */
    public IntFixed sin() {
        IntFixed val = this;
        if (val.compareTo(PI.negate()) < 0) {
            val = sub(TAU.mult(add(PI).div(TAU).integ())).add(TAU);
        } else {
            val = sub(TAU.mult(add(PI).div(TAU).integ()));
        }
        return val.sub(val.pow(3).div(fromInt(6)))
                  .add(val.pow(5).div(fromInt(120)))
                  .sub(val.pow(7).div(fromInt(5040)))
                  .add(val.pow(9).div(fromInt(362880)));
                // TODO: Might be more accurate to arrange the constants as their log_p(c) forms as below to allow multiplying smaller numbers? Or is that too small of a difference to matter?
//                  .add(val.sub(fromDouble(5.82636277243977574348082925758)).pow(9));
    }

    /**
     * A poor approximation of sinh
     * @return
     */
    public IntFixed sinh() {
//        IntFixed val = this;
//        return val.add(val.pow(3).div(fromInt(6)))
//                  .add(val.pow(5).div(fromInt(120)))
//                  .add(val.pow(7).div(fromInt(5040)));
        // Not sure if this is better or worse (accuracy or performance wise)
        return exp().sub(negate().exp()).div(TWO);
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
        // This almost seemed like it worked when diffed with regular atan? But it seemed off when rendering fractals, and mathematically I don't think it checks out, so I'm not sure.
//        IntFixed temp = this;
//        int shift = 0;
//        while (temp.abs().compareTo(ONE) > 0) {
//            temp = temp.rshBits(1);
//            shift++;
//        }
//        return temp
//               .sub(temp.pow(3).div(fromInt(3).mult(ONE.lshBits(shift).pow(3))))
//               .add(temp.pow(5).div(fromInt(5).mult(ONE.lshBits(shift).pow(5))))
//               .add(temp.pow(7).div(fromInt(7).mult(ONE.lshBits(shift).pow(7))));
        if (this.compareTo(ONE) > 0) return PI.div(TWO).sub(this.inverse().atan());
        if (this.compareTo(ONE.negate()) < 0) return PI.div(TWO).negate().sub(this.inverse().atan());
        
        if (this.abs().compareTo(fromDouble(0.5)) > 0) {
            // Formula to move the input from [0.5,1] -> [0,0.5] before the atan then correct after
            return this.square().inverse().add(ONE).sqrt().sub(this.inverse()).atan().mult(TWO);
        }
        // TODO: Potentially still an issue with returning answers PI away in two corners
        IntFixed ans = this;
        // Arbitrarily chosen points to add more accuracy. Gives ~-3e10 of accuracy currently
        if (this.abs().compareTo(fromDouble(0.1)) > 0) {
            ans = ans.sub(pow(3).div(fromInt(3)));
        }
        if (this.abs().compareTo(fromDouble(0.2)) > 0) {
            ans = ans.add(pow(5).div(fromInt(5)));
        }
        if (this.abs().compareTo(fromDouble(0.3)) > 0) {
            ans = ans.sub(pow(7).div(fromInt(7)));
        }
        if (this.abs().compareTo(fromDouble(0.4)) > 0) {
            ans = ans.sub(pow(9).div(fromInt(9)));
        }
        return ans;
    }
    
    public IntFixed integ() {
        int[] nParts = new int[FIXED_MAX];
        nParts[0] = parts[0];
        return new IntFixed(sign, 1, nParts, overflow);
    }
    
    public IntFixed frac() {
        int[] nParts = new int[FIXED_MAX];
        System.arraycopy(parts, 1, nParts, 1, FIXED_MAX - 1);
        return new IntFixed(sign, size, nParts, overflow);
    }
    
    // Obviously too much precision for a double here, rework to an exact initializer later
    private static final IntFixed L2 = ZERO.fromDouble(0.69314718055994530941723212145817656807550013436025525412068000949339362196);
    private static final IntFixed L22 = ZERO.fromDouble(0.48045301391820142466710252632666497173055295159454558686686413362366538225);
    private static final IntFixed L32 = ZERO.fromDouble(0.33302465198892947971885358261173054415612648534860665239121184302385252460);
    private static final IntFixed L42 = ZERO.fromDouble(0.23083509858308345188749771776781277151831629255853082377615679723706711050);
    
    public static IntFixed exp2(int p) {
        int[] parts = new int[FIXED_MAX];
        if (p > 31) return new IntFixed(false, 1, parts, true);
        p += (FIXED_MAX - 1) * 32;
        if (p < 0) return new IntFixed(false, 1, parts, false);
        parts[FIXED_MAX - 1 - p / 32] |= (int)(1l << (p % 32));
        return new IntFixed(false, FIXED_MAX, parts, false).reduce();
    }
    
//    @Override
//    public IntFixed exp2() {
//        // Only 2 term taylor approximation not extremely accurate but reasonably fast
//        // TODO: offset calculation to put frac() within [-0.5, 0.5] for better accuracy?
//        IntFixed f = frac();
//        // TODO: Move these range checks instead the respective shft functions
//        return one().lshBits(Math.max(-FIXED_MAX * 32, Math.min(FIXED_MAX * 32, integ().toInt()))).mult(one().add(f.mult(E2).add(f.square().mult(E22))));
//    }
    
    @Override
    public IntFixed exp2() {
        // Only 2 term taylor approximation not extremely accurate but reasonably fast
        // TODO: offset calculation to put frac() within [-0.5, 0.5] for better accuracy? Done, but did it help?
        IntFixed f = frac().div(TWO);
        if (compareTo(fromInt(33)) > 0) return new IntFixed(false, 1, new int[FIXED_MAX], true);
        // TODO: Move these range checks instead the respective shift functions
//        return one().lshBits(Math.max(-(FIXED_MAX - 1) * 32, Math.min(32, integ().toInt()))).mult(
//                    one()
//                    .add(f.mult(L2))
//                    .add(f.square().mult(L22).div(TWO))
//                    .add(f.pow(3).mult(L32).div(fromInt(6)))
//                    .add(f.pow(4).mult(L42).div(fromInt(24)))
//                ).square();
        // So this is very slow now with all the square roots, but it is accurate? Hmm...
        // To be fair, now that it is only up to n sqrts instead of n^2, it might be somewhat reasonable?
        // TODO Maybe: Actually, could even statically precompute the table of successive sqrts for maximum speed
        IntFixed temp = f;
        IntFixed ans = ONE;
        int n = 1;
        int sqtn = 1;
        IntFixed sqrts = TWO;
        // TODO: Better option than arbitrary (Up to (FIXED_MAX - 1) * 32 for max accuracy) iterations here?
        for (; temp.compareTo(ZERO) != 0 && n < 64; n++) {
            temp = temp.lshBits(1);
            if ((temp.parts[0] & 0x1) > 0) {
//                IntFixed sqrts = TWO;
//                for (int i = 1; i < n; i++) {
                for (; sqtn < n; sqtn++) {
                    sqrts = sqrts.sqrt();
                }
                ans = sign ? ans.div(sqrts) : ans.mult(sqrts);
            }
        }
        return ans.mult(exp2(integ().toInt()));
    }
    
//    private static final IntFixed L2 = ZERO.fromDouble(0.69314718055994530941723212145817656807550013436025525412068000949339362196);
    private static final IntFixed L2E = ZERO.fromDouble(1.44269504088896340735992468100189213742664595415298593413544940693110921918);
    @Override
    public IntFixed log2() {
        // TODO: Not sure if null is right here
        if (compareTo(ZERO) == 0) return null;
        int n = 0;
        IntFixed temp = this;
        while (temp.compareTo(ONE) < 0) {
            temp = temp.lshBits(1);
            n--;
        }
        while (temp.compareTo(TWO) >= 0) {
            temp = temp.rshBits(1);
            n++;
        }
        
        IntFixed frac = ZERO;
        long factor = 0;
//        while (temp.compareTo(ONE) != 0 && factor > -(FIXED_MAX - 1) * 32l) {
        // This seems like a decent amount of accuracy for now for the speed
        while (temp.compareTo(ONE) != 0 && factor > -8) {
            int m = 0;
            IntFixed temp2 = temp;
            while (temp2.compareTo(TWO) < 0) {
                temp2 = temp2.square();
                m++;
            }
            temp = temp2.rshBits(1);
            factor -= m;
            frac = frac.add(ONE.lshBits((int)factor));
        }
        return IntFixed.fromInt(n).add(frac);
    }
    
    @Override
    public IntFixed exp() {
        return mult(L2E).exp2();
    }
    
    @Override
    public IntFixed log() {
        return log2().div(L2E);
    }
    
    public int toInt() {
//        return (parts[0] & 0x7FFFFFFF) | (sign ? 0x80000000 : 0);
        return ((parts[0] & 0x7FFFFFFF) ^ (sign ? 0xFFFFFFFF : 0x0)) + (sign ? 1 : 0);
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
			shifted = rshBits(shift);
		} else if (shift < 0) {
			shifted = lshBits(-shift);
		} else {
			shifted = clone();
		}
		long bits = (sign ? 1l : 0l) << 63 |
                    (overflow ? 0x7FFL : (1023l + shift)) << 52 |
                    (1023l + shift) << 52 |
				    (shifted.parts[1] & 0xFFFFFFFFL) << 20 | (shifted.parts.length > 2 ? shifted.parts[2] & 0xFFFFF000L : 0) >> 12;
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
		if (!overflow && o.overflow) return o.sign ? 1 : -1;
        
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

