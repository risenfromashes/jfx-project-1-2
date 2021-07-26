package edu.buet.util;

public class Test {
    public static void assertEquals(Object a, Object b) throws AssertionError {
        try {
            assert(a.equals(b));
        } catch (AssertionError e) {
            System.out.println("Expected: " + b + " Got: " + a);
            throw e;
        }
    }
}
