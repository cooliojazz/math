package com.up.math;

import com.up.math.matrix.AffineMatrix2;
import com.up.math.matrix.ComplexAffineMatrix2;
import com.up.math.matrix.ComplexMatrix2;
import com.up.math.matrix.Matrix2;

public class MatrixTest {
    
    public static void main(String[] args) {
        roundedEquals();
        compose2();
        expLog2();
        expLogAffine2();
    }
    
    public static void roundedEquals() {
        ComplexMatrix2 m1 = new Matrix2(1, 2, 3, 4).asComplex();
        ComplexMatrix2 m2 = new Matrix2(1.01, 2.002, 3.0003, 4.0004).asComplex();
        assert m1.roundedEquals(m2, 1);
        assert !m1.roundedEquals(m2, 10);
    }
    
    public static void compose2() {
        ComplexMatrix2 m1 = new Matrix2(0, 1, 2, 3).asComplex();
        ComplexMatrix2 m2 = new Matrix2(4, 5, 6, 7).asComplex();
        assert m1.compose(m2).roundedEquals(new Matrix2(6, 7, 26, 31).asComplex(), 10);
    }
    
    public static void expLog2() {
        ComplexMatrix2 m = new Matrix2(1, 2, 3, 4).asComplex();
        ComplexMatrix2 me = m.exp();
        ComplexMatrix2 ml = me.log();
        assert m.roundedEquals(ml, 10);
    }
    
    public static void expLogAffine2() {
        ComplexAffineMatrix2 m = new AffineMatrix2(1, 2, 3, 4, 5, 6).asComplex();
        ComplexAffineMatrix2 me = m.exp();
        ComplexAffineMatrix2 ml = me.log();
        assert m.roundedEquals(ml, 10);
    }
    
    
}
