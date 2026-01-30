package com.up.math.number;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Represents an 32.32X fixed point number
 * @author Ricky
 */
public final class IntFixed<P extends Precision> extends BigFixed<IntFixed<P>, P> {
//    public record IntFixed(boolean sign, int size, int[] parts, boolean overflow) extends BigFixed<com.up.math.number.IntFixed> {
    // TODO: It seems like it should be more possible to actually have dynamically sized numbers with how this is written now?
    //       Although adding in the type parameter for the stats kind of moved away from that so idk.
    //       Or it's not a bad idea, but should be a seperate implementation, like IntDynamic or something
    
    public static final HashMap<Precision, IntFixed> ZERO = new HashMap<>();
    public static final HashMap<Precision, IntFixed> ONE = new HashMap<>();
    public static final HashMap<Precision, IntFixed> TWO = new HashMap<>();
    public static final HashMap<Precision, IntFixed> E = new HashMap<>();
    public static final HashMap<Precision, IntFixed> HALF_PI = new HashMap<>();
    public static final HashMap<Precision, IntFixed> PI = new HashMap<>();
    public static final HashMap<Precision, IntFixed> TAU = new HashMap<>();
    public static final HashMap<Precision, IntFixed> SQRT2 = new HashMap<>();
    public static final HashMap<Precision, IntFixed> L2E = new HashMap<>();
    
    private final P precision;
    private final boolean sign;
    private final int size;
    private final int[] parts;
    private final boolean overflow;
    
    public IntFixed(P precision, boolean sign, int size, int[] parts, boolean overflow) {
        this.precision = precision;
        this.sign = sign;
        this.size = size;
        this.parts = parts;
        this.overflow = overflow;
        checkConstants();
    }
    
    // TODO: This broke the Real constructor. Is there any way to fix it without requiring subclassing to obtain the P class? Would that even work?
    public IntFixed(P precision, double d) {
        IntFixed<P> temp = fromDouble(precision, d);
        this.precision = precision;
        this.sign = temp.sign;
        this.size = temp.size;
        this.parts = temp.parts;
        this.overflow = temp.overflow;
        checkConstants();
    }

	public IntFixed(P precision) {
        this(precision, false, 1, new int[precision.getSize()], false);
	}
    
    private static HashMap<Precision, Boolean> makingConstants = new HashMap<>();
    private void checkConstants() {
        if (!ZERO.containsKey(precision) && !makingConstants.getOrDefault(precision, false)) {
            makingConstants.put(precision, true);
            ZERO.put(precision, fromInt(precision, 0));
            ONE.put(precision, fromInt(precision, 1));
            TWO.put(precision, fromInt(precision, 2));
            E.put(precision, fromHexString(precision, " 00000002.b7e151628aed2a6abf7158809cf4f3c7"));
            PI.put(precision, fromHexString(precision, " 00000003.243f6a8885a308d313198a2e03707344"));
            HALF_PI.put(precision, pi().div(two()));
            TAU.put(precision, pi().mult(two()));
            SQRT2.put(precision, fromHexString(precision, " 00000001.6a09e667f3bcc908b2fb1366ea957d3e"));
            L2E.put(precision, fromDouble(precision, 1.44269504088896340735992468100189213742664595415298593413544940693110921918));
            makingConstants.put(precision, false);
        }
    }
    
    @Override
    public IntFixed<P> zero() {
        return ZERO.get(precision);
    }
    
    @Override
    public IntFixed<P> one() {
        return ONE.get(precision);
    }
    
    @Override
    public IntFixed<P> two() {
        return TWO.get(precision);
    }
    
    @Override
    public IntFixed<P> pi() {
        return PI.get(precision);
    }
    
    @Override
    public IntFixed<P> e() {
        return E.get(precision);
    }
    
    @Override
    public IntFixed<P> sqrt2() {
        return SQRT2.get(precision);
    }
    
    public IntFixed<P> tau() {
        return TAU.get(precision);
    }
    
    public IntFixed<P> halfPi() {
        return HALF_PI.get(precision);
    }
    
    public IntFixed<P> l2e() {
        return L2E.get(precision);
    }
    
    public boolean sign() {
        return sign;
    }
    
    public int size() {
        return size;
    }

    IntFixed<P> expand() {
        int nSize = size + 1;
        if (nSize > precision.getSize()) {
//            System.out.println("Warning: Fixed number size exceeded.");
            nSize = precision.getSize();
        }
        return new IntFixed<>(precision, sign, nSize, parts, overflow);
    }

    // TODO: Rewrite these in the newer direct creation format
    public IntFixed<P> lshBits(int amount) {
        if (amount < 0) return rshBits(Math.abs(amount));
        IntFixed<P> ans = clone();
        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < ans.size; j++) {
                if (j > 0) ans.parts[j - 1] |= ans.parts[j] >>> 31;
                ans.parts[j] = ans.parts[j] << 1;
            }
        }
        return ans.reduce();
    }

    public IntFixed<P> rshBits(int amount) {
        if (amount < 0) return lshBits(Math.abs(amount));
        IntFixed<P> ans = clone();
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

    public IntFixed<P> lshParts(int amount) {
        if (amount < 0) return rshParts(Math.abs(amount));
        IntFixed<P> ans = clone();
        for (int i = 0; i < amount; i++) {
            for (int j = 1; j < ans.size; j++) {
                ans.parts[j - 1] = ans.parts[j];
            }
            ans.parts[ans.size - 1] = 0;
        }
        return ans.reduce();
    }

    public IntFixed<P> rshParts(int amount) {
        if (amount < 0) return lshParts(Math.abs(amount));
        IntFixed<P> ans = clone();
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
    
    public IntFixed<P> abs() {
        return new IntFixed<>(precision, false, size, parts, overflow);
    }
    
    public IntFixed<P> reduce() {
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
        return new IntFixed<>(precision, sign, nSize, parts, overflow);
    }

    public static <P extends Precision> IntFixed<P> fromDouble(P precision, double a) {
        long raw = Double.doubleToRawLongBits(a);
        int exponent = (int)((raw & 0x7FF0000000000000l) >>> 52) - 1023;
        int[] parts = new int[precision.getSize()];
        parts[0] = 1;
        parts[1] = (int)((raw & 0xFFFFFFFF00000l) >> 20);
        if (parts.length > 2) parts[2] = (int)((raw & 0xFFFFFl) << 12);
        // Then shift for days
        IntFixed<P> ans = new IntFixed<>(precision, raw >>> 63 != 0, Math.min(3, parts.length), parts, false);
        if (exponent < 0) ans = ans.rshBits(-exponent);
        if (exponent > 0) ans = ans.lshBits(exponent);
        return ans.reduce();
    }

    public static <P extends Precision> IntFixed<P> fromInt(P precision, int a) {
        int[] parts = new int[precision.getSize()];
        boolean sign = (a & 0x80000000) >> 31 != 0;
        if (sign) {
			parts[0] = ~(a - 1);
        } else {
			parts[0] = a;
		}
        return new IntFixed<>(precision, sign, 1, parts, false);
    }

    public static <P extends Precision> IntFixed<P> fromShort(P precision, short a) {
        int[] parts = new int[precision.getSize()];
        boolean sign = (a & 0x8000) >> 15 != 0;
        if (sign) {
            parts[0] = ~(a - 1) & 0xFFFF;
        } else {
            parts[0] = a & 0xFFFF;
		}
        return new IntFixed<>(precision, sign, 1, parts, false);
    }

    public static <P extends Precision> IntFixed<P> fromBitString(P precision, String s) {
        int[] parts = new int[precision.getSize()];
        int size = Math.min((s.length() - 2) / 32, precision.getSize());
        for (int i = 0; i < size; i++) {
			for (int b = 0; b < 32; b++) {
				parts[i] |= (s.charAt(i * 32 + b + (i > 0 ? 2 : 1)) == '0' ? 0 : 1) << (31 - b);
			}
		}
        return new IntFixed<>(precision, s.charAt(0) == '-', size, parts, false);
    }

    public static <P extends Precision> IntFixed<P> fromHexString(P precision, String s) {
        int[] parts = new int[precision.getSize()];
        int size = Math.min((s.length() - 2) / 8, precision.getSize());
        for (int i = 0; i < size; i++) {
            int start = i * 8 + 1 + (i > 0 ? 1 : 0);
            parts[i] = Integer.parseUnsignedInt(s.substring(start, start + 8), 16);
		}
        return new IntFixed<>(precision, s.charAt(0) == '-', size, parts, false);
    }

    public IntFixed<P> negate() {
        return new IntFixed<>(precision, !sign, size, parts, overflow);
    }

    public IntFixed<P> add(IntFixed<P> b) {
		if (sign != b.sign) return sub(b.negate());
        int nSize = Math.max(size, b.size);
        int[] nParts = new int[precision.getSize()];
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
        return new IntFixed<>(precision, sign, nSize, nParts, nOverflow);
    }

    public IntFixed<P> sub(IntFixed<P> b) {
        IntFixed<P> a = clone();
		if (a.sign != b.sign) return add(b.negate());
        int nSize = Math.max(size, b.size);
        boolean nSign = sign;
        int[] nParts = new int[precision.getSize()];
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
        return new IntFixed<>(precision, nSign, nSize, nParts, nOverflow).reduce();
    }

    public IntFixed<P> mult(IntFixed<P> b) {
        int nSize = Math.min(precision.getSize(), size + b.size - 1); // (size - 1 + (b.size - 1)) + 1;
        int[] nParts = new int[precision.getSize()];
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
        return new IntFixed<>(precision, sign ^ b.sign, nSize, nParts, nOverflow).reduce();
    }

    public IntFixed<P> div(IntFixed<P> b) {
        // Just trying reusing the inverse code for now, there's probably a better way
        IntFixed<P> div = b.abs();
        IntFixed<P> rem = abs();
        int[] nParts = new int[precision.getSize()];
        int nSize = 2;
        int pos = -1;
        while (rem.compareTo(div) > 0 && pos > -31) {
            div = div.lshBits(1);
            pos--;
        }
        while (!rem.equals(new IntFixed<>(precision)) && pos < (precision.getSize() - 1) * 32) {
            if (rem.compareTo(div) >= 0) {
                rem = rem.sub(div);
                int tpos = pos + 32;
                nParts[tpos / 32] |= 1 << (31 - (tpos % 32));
                if (tpos >= nSize * 32) nSize++;
            }
            div = div.rshBits(1);
            pos++;
        }
        return new IntFixed<>(precision, sign ^ b.sign, nSize, nParts, false).reduce();
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
//        while (!rem.equals(new BigFixed()) && pos < (precision.getSize() - 1) * 16) {
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

    public IntFixed<P> inverse() {
        return one().div(this);
    }

    
    public IntFixed<P> sqrt() {
        IntFixed<P> val = this.rshBits(1);
        // TODO: Replace arbitrary precision with some form of convergence testing?
        for (int i = 0; i < 8; i++) {
            val = val.add(this.div(val)).rshBits(1);
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

    public IntFixed<P> square() {
        return mult(this);
    }

    public IntFixed<P> pow(int p) {
        IntFixed<P> ans = this;
        for (int i = 1; i < p; i++) {
            ans = ans.mult(this);
        }
        return ans;
    }

    public IntFixed<P> pow(IntFixed<P> p) {
        // TODO: Faster way
        return p.mult(log2()).exp2();
    }

    /**
     * A poor approximation of sin
     * @return
     */
    public IntFixed<P> sin() {
        IntFixed<P> val = this;
        if (val.compareTo(pi().negate()) < 0) {
            val = sub(tau().mult(add(pi()).div(tau()).integ())).add(tau());
        } else {
            val = sub(tau().mult(add(pi()).div(tau()).integ()));
        }
        if (val.abs().compareTo(halfPi()) > 0) {
            return pi().sub(val).sin();
        }
        return val.sub(val.pow(3).div(fromInt(precision, 6)))
                  .add(val.pow(5).div(fromInt(precision, 120)))
                  .sub(val.pow(7).div(fromInt(precision, 5040)));
//                  .add(val.pow(9).div(fromInt(362880)));
                // TODO: Might be more accurate to arrange the constants as their log_p(c) forms as below to allow multiplying smaller numbers? Or is that too small of a difference to matter?
//                  .add(val.sub(fromDouble(5.82636277243977574348082925758)).pow(9));
    }

    /**
     * 
     * @return
     */
    public IntFixed<P> sinh() {
//        IntFixed val = this;
//        return val.add(val.pow(3).div(fromInt(6)))
//                  .add(val.pow(5).div(fromInt(120)))
//                  .add(val.pow(7).div(fromInt(5040)));
        // Not sure if this is better or worse (accuracy or performance wise)
        return exp().sub(negate().exp()).div(two());
    }

    /**
     * 
     * @return
     */
    public IntFixed<P> cosh() {
//        IntFixed val = this;
//        return val.add(val.pow(2).div(fromInt(2)))
//                  .add(val.pow(4).div(fromInt(24)))
//                  .add(val.pow(6).div(fromInt(720)));
        return exp().add(negate().exp()).div(two());
    }

    /**
     * U
     * @return
     */
    public IntFixed<P> cos() {
        return add(halfPi()).sin();
    }

    /**
     * A poor approximation of atan
     * @return
     */
    public IntFixed<P> atan() {
        if (this.compareTo(one()) > 0) return halfPi().sub(this.inverse().atan());
//        if (this.compareTo(TWO.negate()) < 0) return HALF_PI.negate().sub(this.inverse().atan()).add(PI);
        if (this.compareTo(one().negate()) < 0) return halfPi().negate().sub(this.inverse().atan());
        
        if (this.abs().compareTo(fromDouble(precision,0.5)) > 0) {
            // Formula to move the input from [0.5,1] -> [0,0.5] before the atan then correct after
            return this.square().inverse().add(one()).sqrt().sub(this.inverse()).atan().mult(two()).sub(this.compareTo(zero()) < 0 ? pi() : zero());
        }
        // TODO: Potentially still an issue with returning answers PI away in two corners
        IntFixed<P> ans = this;
//        ans = ans.sub(pow(3).div(fromInt(3)));
        // Arbitrarily chosen points to add more accuracy. Gives ~-3e10 of accuracy currently
        if (this.abs().compareTo(fromDouble(precision, 0.1)) > 0) {
            ans = ans.sub(pow(3).div(fromInt(precision, 3)));
        }
        if (this.abs().compareTo(fromDouble(precision, 0.2)) > 0) {
            ans = ans.add(pow(5).div(fromInt(precision, 5)));
        }
        if (this.abs().compareTo(fromDouble(precision, 0.3)) > 0) {
            ans = ans.sub(pow(7).div(fromInt(precision, 7)));
        }
        if (this.abs().compareTo(fromDouble(precision, 0.4)) > 0) {
            ans = ans.sub(pow(9).div(fromInt(precision, 9)));
        }
        return ans;
    }
    
    @Override
    public IntFixed<P> mod(IntFixed<P> n) {
        // TODO: Probably a better way to do this
        return n.mult(this.div(n).floor());
    }
    
    // TODO: These are wrong for negatives
    // TODO: Match format of other IntFixed functions
    public IntFixed<P> floor() {
        IntFixed<P> val = new IntFixed<>(precision);
        val.parts[0] = parts[0];
        return val;
    }
    
    public IntFixed<P> ceil() {
        IntFixed<P> val = floor();
        if (!val.equals(this)) val.parts[0] = (short)(val.parts[0] + 1);
        return val;
    }
    
    public IntFixed<P> integ() {
        int[] nParts = new int[precision.getSize()];
        nParts[0] = parts[0];
        return new IntFixed<>(precision, sign, 1, nParts, overflow);
    }
    
    public IntFixed<P> frac() {
        int[] nParts = new int[precision.getSize()];
        System.arraycopy(parts, 1, nParts, 1, precision.getSize() - 1);
        return new IntFixed<>(precision, sign, size, nParts, overflow);
    }
    
//    // TODO: Obviously too much precision for a double here, rework to an exact initializer later
//    private static final IntFixed L2 = fromDouble(0.69314718055994530941723212145817656807550013436025525412068000949339362196);
//    private static final IntFixed L22 = fromDouble(0.48045301391820142466710252632666497173055295159454558686686413362366538225);
//    private static final IntFixed L32 = fromDouble(0.33302465198892947971885358261173054415612648534860665239121184302385252460);
//    private static final IntFixed L42 = fromDouble(0.23083509858308345188749771776781277151831629255853082377615679723706711050);
    
    public static <P extends Precision> IntFixed<P> exp2(P precision, int p) {
        int[] parts = new int[precision.getSize()];
        if (p > 31) return new IntFixed<>(precision, false, 1, parts, true);
        p += (precision.getSize() - 1) * 32;
        if (p < 0) return new IntFixed<>(precision, false, 1, parts, false);
        parts[precision.getSize() - 1 - p / 32] |= (int)(1l << (p % 32));
        return new IntFixed<>(precision, false, precision.getSize(), parts, false).reduce();
    }
    
//    @Override
//    public IntFixed exp2() {
//        // Only 2 term taylor approximation not extremely accurate but reasonably fast
//        // TODO: offset calculation to put frac() within [-0.5, 0.5] for better accuracy? Done, but did it help?
//        IntFixed f = frac().div(TWO);
//        if (compareTo(fromInt(33)) > 0) return new IntFixed(false, 1, new int[precision.getSize()], true);
//        // TODO: Move these range checks instead the respective shift functions
//                return one().lshBits(Math.max(-(precision.getSize() - 1) * 32, Math.min(32, integ().toInt()))).mult(
//                            one()
//                            .add(f.mult(L2))
//                            .add(f.square().mult(L22).div(TWO))
//                            .add(f.pow(3).mult(L32).div(fromInt(6)))
//                            .add(f.pow(4).mult(L42).div(fromInt(24)))
//                        ).square();
//    }

    private static HashMap<Precision, IntFixed[]> sqrtSets = new HashMap<>();
    private IntFixed<P>[] getSqrts() {
        if (!sqrtSets.containsKey(precision)) {
            IntFixed<P>[] sqrts = new IntFixed[32];
            sqrts[0] = two();
            for (int i = 1; i < sqrts.length; i++) {
                sqrts[i] = sqrts[i - 1].sqrt();
            }
            sqrtSets.put(precision, sqrts);
        }
        return (IntFixed<P>[])sqrtSets.get(precision);
    }
    
    @Override
    public IntFixed<P> exp2() {
        // TODO: offset calculation to put frac() within [-0.5, 0.5] for better accuracy? Done, but did it help?
        IntFixed<P> f = frac().div(two());
        if (compareTo(fromInt(precision, 33)) > 0) return new IntFixed<>(precision, false, 1, new int[precision.getSize()], true);
        IntFixed<P> temp = f;
        IntFixed<P> ans = one();
        int n = 1;
//        int sqtn = 1;
//        IntFixed sqrts = TWO;
        IntFixed<P>[] sqrts = getSqrts();
        // TODO: Better option than arbitrary (Up to (precision.getSize() - 1) * 32 for max accuracy) iterations here?
        for (; temp.compareTo(zero()) != 0 && n < 32; n++) {
            temp = temp.lshBits(1);
            if ((temp.parts[0] & 0x1) > 0) {
//                IntFixed sqrts = TWO;
//                for (int i = 1; i < n; i++) {
//                for (; sqtn < n; sqtn++) {
//                    sqrts = sqrts.sqrt();
//                }
//                ans = sign ? ans.div(sqrts) : ans.mult(sqrts);
                ans = sign ? ans.div(sqrts[n - 1]) : ans.mult(sqrts[n - 1]);
            }
        }
        return ans.mult(exp2(precision, integ().toInt()));
    }
    
    @Override
    public IntFixed<P> log2() {
        // TODO: Not sure if null is right here
        if (compareTo(zero()) <= 0) return new IntFixed<>(precision, true, 1, IntFixed.fromHexString(precision, " FFFFFFFF").parts, true);
        int n = 0;
        IntFixed<P> temp = this;
        while (temp.compareTo(one()) < 0) {
            temp = temp.lshBits(1);
            n--;
        }
        while (temp.compareTo(two()) >= 0) {
            temp = temp.rshBits(1);
            n++;
        }
        
        IntFixed<P> frac = zero();
        long factor = 0;
//        while (temp.compareTo(ONE) != 0 && factor > -(precision.getSize() - 1) * 32l) {
        // This seems like a decent amount of accuracy for now for the speed
        while (temp.compareTo(one()) != 0 && factor > -32) {
            int m = 0;
            IntFixed<P> temp2 = temp;
            while (temp2.compareTo(two()) < 0) {
                temp2 = temp2.square();
                m++;
            }
            temp = temp2.rshBits(1);
            factor -= m;
            frac = frac.add(one().lshBits((int)factor));
        }
        return IntFixed.fromInt(precision, n).add(frac);
    }
    
    @Override
    public IntFixed<P> exp() {
        return mult(l2e()).exp2();
    }
    
    @Override
    public IntFixed<P> log() {
        return log2().div(l2e());
    }
    
    public int toInt() {
        return ((parts[0] & 0x7FFFFFFF) ^ (sign ? 0xFFFFFFFF : 0x0)) + (sign ? 1 : 0);
    }
    
    public double toDouble() {
		if (abs().compareTo(new IntFixed<P>(precision)) == 0) return 0;
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
		IntFixed<P> shifted;
		if (shift > 0) {
			shifted = rshBits(shift);
		} else if (shift < 0) {
			shifted = lshBits(-shift);
		} else {
			shifted = clone();
		}
		long bits = (sign ? 1l : 0l) << 63 |
//                    (overflow ? 0x7FFL : (1023l + shift)) << 52 |
                    (1023l + shift) << 52 |
				    (shifted.parts[1] & 0xFFFFFFFFL) << 20 | (shifted.parts.length > 2 ? shifted.parts[2] & 0xFFFFF000L : 0) >> 12;
		return Double.longBitsToDouble(bits);
    }

    @Override
    public String toString() {
        String result = toDouble() + " ";
        result += sign ? '-' : " ";
//        for (int i = 0; i < size; i++) {
//            if (i == 1) result += '.';
//            for (int j = 31; j >= 0; j--) {
//                result += (parts[i] & (1 << j)) >>> j == 0 ? "0" : "1";
//            }
//        }
        for (int i = 0; i < size; i++) {
            if (i == 1) result += '.';
            result += Integer.toHexString(parts[i]);
        }
        return result + (overflow ? " overflowed" : "");
    }
    
    @Override
    public IntFixed<P> clone() {
        return new IntFixed<>(precision, sign, size, parts.clone(), overflow);
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
    
    @Override
    public Function<Double, IntFixed<P>> getConverter() {
        return d -> fromDouble(precision, d);
    }

}

