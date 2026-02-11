package com.up.math.number;

public interface Precision {
    
    public int getSize();
    
    public int getIntegralSize();
    
    // TODO: Just an idea for merging all the the commented out overlflow code.
    //       Also this isn't clear whether "can overflow" means it will wrap or if it will set the overflow flag, so probably needs a better name.
//    public boolean canOverflow();
    
    public default int getFractionalSize() {
        return getSize() - getIntegralSize();
    }
    
    public class P1_1 extends InternalPrecision {
        public P1_1() {
            super(2, 1);
        }
    }
    public static P1_1 P1_1 = new P1_1();
    
    public class P1_2 extends InternalPrecision {
        public P1_2() {
            super(3, 1);
        }
    }
    public static P1_2 P1_2 = new P1_2();
    
    public class P1_3 extends InternalPrecision {
        public P1_3() {
            super(4, 1);
        }
    }
    public static P1_3 P1_3 = new P1_3();
    
    public class P2_0 extends InternalPrecision {
        public P2_0() {
            super(2, 2);
        }
    }
    public static P2_0 P2_0 = new P2_0();
    
    public class P2_1 extends InternalPrecision {
        public P2_1() {
            super(3, 2);
        }
    }
    public static P2_1 P2_1 = new P2_1();
    
    public class P2_2 extends InternalPrecision {
        public P2_2() {
            super(4, 2);
        }
    }
    public static P2_2 P2_2 = new P2_2();
}

class InternalPrecision implements Precision {
    
    final int size, iSize;
    
    public InternalPrecision(int size, int iSize) {
        this.size = size;
        this.iSize = iSize;
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public int getIntegralSize() {
        return iSize;
    }
}
