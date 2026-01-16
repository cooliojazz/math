package com.up.math.number;

public interface Precision {
    
    public int getSize();
    
    // TODO: Not yet implemented in fixed classes
    public int getIntegerSize();
    
    
    public class P1_2 extends InternalPrecision {
        public P1_2() {
            super(3, 1);
        }
    }
    
    public static P1_2 P1_2 = new P1_2();
}

class InternalPrecision implements Precision {
    
    final int size, iSize;
    
    public InternalPrecision(int size, int iSize) {
        this.size = size;
        this. iSize = iSize;
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public int getIntegerSize() {
        return iSize;
    }
}
