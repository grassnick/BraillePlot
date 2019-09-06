package de.tudresden.inf.mci.brailleplot.point;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Richard Schmidt
 */
class Point2DValuedTest {

    @Test
    void testPoint2DValued () {
        int a = 1;
        int b = 2;
        int value = 3;

        Assertions.assertThrows(NullPointerException.class, () -> {new Point2DValued<Integer, Integer>(a, b, null);});

        Point2DValued<Integer, Integer> point = new Point2DValued<>(a, b, value);
        Assertions.assertEquals(value, point.getVal());
    }

    @Test
    void testEquals () {
        Point2DValued<Integer, Integer> point_1 = new Point2DValued<>(1, 2, 3);
        Point2DValued<Integer, Integer> point_2 = new Point2DValued<>(1, 2, 3);
        Point2DValued<Integer, Integer> point_3 = new Point2DValued<>(2, 2, 3);
        Point2DValued<Integer, Integer> point_4 = new Point2DValued<>(1, 1, 3);
        Point2DValued<Integer, Integer> point_5 = new Point2DValued<>(1, 2, 2);
        String string = "test";

        Assertions.assertTrue(point_1.equals(point_2));
        Assertions.assertFalse(point_1.equals(point_3));
        Assertions.assertFalse(point_1.equals(point_4));
        Assertions.assertFalse(point_1.equals(point_5));
        Assertions.assertFalse(point_1.equals(string));
    }
}
