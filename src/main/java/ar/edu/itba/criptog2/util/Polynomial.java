package ar.edu.itba.criptog2.util;

import java.math.BigInteger;

/******************************************************************************
 *  Compilation:  javac Polynomial.java
 *  Execution:    java Polynomial
 *
 *  Polynomials with integer coefficients.
 *
 *  % java Polynomial
 *  zero(x) =     0
 *  p(x) =        4x^3 + 3x^2 + 2x + 1
 *  q(x) =        3x^2 + 5
 *  p(x) + q(x) = 4x^3 + 6x^2 + 2x + 6
 *  p(x) * q(x) = 12x^5 + 9x^4 + 26x^3 + 18x^2 + 10x + 5
 *  p(q(x))     = 108x^6 + 567x^4 + 996x^2 + 586
 *  0 - p(x)    = -4x^3 - 3x^2 - 2x - 1
 *  p(3)        = 142
 *  p'(x)       = 12x^2 + 6x + 2
 *  p''(x)      = 24x + 6
 *
 ******************************************************************************/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Polynomial implementation, used for Lagrange interpolation.
 *
 * @see <a href="http://introcs.cs.princeton.edu/java/92symbolic/Polynomial.java.html">Source</a>
 */
public class Polynomial {
    private BigInteger[] coef;  // coefficients
    private int deg;     // degree of polynomial (0 for the zero polynomial)

    // a * x^b
    public Polynomial(int a, int b) {
        coef = new BigInteger[b+1];
        for (int i = 0; i < b; i++) {
        	coef[i] = BigInteger.ZERO;
        }
        coef[b] = BigInteger.valueOf(a);
        
        deg = degree();
    }
    
    public Polynomial(BigInteger a, int b) {
      coef = new BigInteger[b+1];
      for (int i = 0; i < b; i++) {
      	coef[i] = BigInteger.ZERO;
      }
      coef[b] = a;
      deg = degree();
  }

    // return the degree of this polynomial (0 for the zero polynomial)
    private int degree() {
        int d = 0;
        for (int i = 0; i < coef.length; i++)
            if (!coef[i].equals(0)) d = i;
        return d;
    }

    // return c = a + b
    public Polynomial plus(Polynomial b) {
        Polynomial a = this;
        Polynomial c = new Polynomial(0, Math.max(a.deg, b.deg));
        for (int i = 0; i <= a.deg; i++) c.coef[i] = c.coef[i].add(a.coef[i]);
        for (int i = 0; i <= b.deg; i++) c.coef[i] = c.coef[i].add(b.coef[i]);
        c.deg = c.degree();
        return c;
    }

    // return (a - b)
    public Polynomial minus(Polynomial b) {
        Polynomial a = this;
        Polynomial c = new Polynomial(0, Math.max(a.deg, b.deg));
        for (int i = 0; i <= a.deg; i++) c.coef[i] = c.coef[i].add(a.coef[i]);
        for (int i = 0; i <= b.deg; i++) c.coef[i] = c.coef[i].subtract(b.coef[i]);
        c.deg = c.degree();
        return c;
    }

    // return (a * b)
    public Polynomial times(Polynomial b) {
        Polynomial a = this;
        Polynomial c = new Polynomial(0, a.deg + b.deg);
        for (int i = 0; i <= a.deg; i++)
            for (int j = 0; j <= b.deg; j++)
                c.coef[i+j] = c.coef[i+j].add(a.coef[i].multiply(b.coef[j]));
        c.deg = c.degree();
        return c;
    }

    // return a(b(x))  - compute using Horner's method
    public Polynomial compose(Polynomial b) {
        Polynomial a = this;
        Polynomial c = new Polynomial(0, 0);
        for (int i = a.deg; i >= 0; i--) {
            Polynomial term = new Polynomial(a.coef[i], 0);
            c = term.plus(b.times(c));
        }
        return c;
    }


    // do a and b represent the same polynomial?
    public boolean eq(Polynomial b) {
        Polynomial a = this;
        if (a.deg != b.deg) return false;
        for (int i = a.deg; i >= 0; i--)
            if (a.coef[i] != b.coef[i]) return false;
        return true;
    }


    // use Horner's method to compute and return the polynomial evaluated at x
    public BigInteger evaluate(BigInteger x) {
        BigInteger p = BigInteger.valueOf(0);
        for (int i = deg; i >= 0; i--)
            p = coef[i].add(x.multiply(p));
        return p;
    }

    // differentiate this polynomial and return it
    public Polynomial differentiate() {
        if (deg == 0) return new Polynomial(0, 0);
        Polynomial deriv = new Polynomial(0, deg - 1);
        deriv.deg = deg - 1;
        for (int i = 0; i < deg; i++)
            deriv.coef[i] = coef[i + 1].multiply(BigInteger.valueOf(i + 1));//  (i + 1) * ;
        return deriv;
    }

    // convert to string representation
    public String toString() {
        if (deg ==  0) return "" + coef[0];
        if (deg ==  1) return coef[1] + "x + " + coef[0];
        String s = coef[deg] + "x^" + deg;
        for (int i = deg-1; i >= 0; i--) {
            if      (coef[i].equals(0)) continue;
            else if (coef[i].compareTo(BigInteger.valueOf(0))  > 0) s = s + " + " + ( coef[i]);
            else if (coef[i].compareTo(BigInteger.valueOf(0))  < 0) s = s + " - " + (coef[i].multiply(BigInteger.valueOf(-1)));
            if      (i == 1) s = s + "x";
            else if (i >  1) s = s + "x^" + i;
        }
        return s;
    }

    public BigInteger[] getCoefficients() {
        return Arrays.copyOf(coef, coef.length);
    }
    
    public BigInteger getCoefficientAt(int index) {
    	return coef[index];
    }
    
    public Polynomial alterCoefficientAt(int index, BigInteger value) {
    	coef[index] = value;
    	return this;
    }

    public int getDegree() {
        return deg;
    }

    // test client
    public static void main(String[] args) {
        List<Point> blah = new ArrayList<>();
        blah.add(new Point(1, 3));
        blah.add(new Point(2, 5));
        blah.add(new Point(4, 0));
        Polynomial blahblah = new LagrangeInterpolator().interpolate(blah, 7);


        Polynomial zero = new Polynomial(0, 0);

        Polynomial p1   = new Polynomial(4, 3);
        Polynomial p2   = new Polynomial(3, 2);
        Polynomial p3   = new Polynomial(1, 0);
        Polynomial p4   = new Polynomial(2, 1);
        Polynomial p    = p1.plus(p2).plus(p3).plus(p4);   // 4x^3 + 3x^2 + 1

        Polynomial q1   = new Polynomial(3, 2);
        Polynomial q2   = new Polynomial(5, 0);
        Polynomial q    = q1.plus(q2);                     // 3x^2 + 5


        Polynomial r    = p.plus(q);
        Polynomial s    = p.times(q);
        Polynomial t    = p.compose(q);

        System.out.println("zero(x) =     " + zero);
        System.out.println("p(x) =        " + p);
        System.out.println("q(x) =        " + q);
        System.out.println("p(x) + q(x) = " + r);
        System.out.println("p(x) * q(x) = " + s);
        System.out.println("p(q(x))     = " + t);
        System.out.println("0 - p(x)    = " + zero.minus(p));
        System.out.println("p(3)        = " + p.evaluate(BigInteger.valueOf(3)));
        System.out.println("p'(x)       = " + p.differentiate());
        System.out.println("p''(x)      = " + p.differentiate().differentiate());
    }

}
