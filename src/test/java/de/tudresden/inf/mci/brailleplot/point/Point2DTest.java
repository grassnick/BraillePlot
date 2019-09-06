package de.tudresden.inf.mci.brailleplot.point;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Richard Schmidt
 */
class Point2DTest {

    @Test
    void testPoint2D () {
        int a = 1;
        int b = 2;
        Point2D<Integer> p;

        Assertions.assertThrows(NullPointerException.class, () -> {new Point2D<>(null, b);});
        Assertions.assertThrows(NullPointerException.class, () -> {new Point2D<>(a, null);});

        p = new Point2D<>(a, b);
        Assertions.assertEquals(a, p.getX());
        Assertions.assertEquals(b, p.getY());
    }

    @Test
    void testEquals () {
        Point2D<Integer> point_1 = new Point2D<>(1, 2);
        Point2D<Integer> point_2 = new Point2D<>(1, 2);
        Point2D<Integer> point_3 = new Point2D<>(1, 3);
        Point2D<Integer> point_4 = new Point2D<>(2, 2);
        String string = "test";

        Assertions.assertTrue(point_1.equals(point_2));
        Assertions.assertFalse(point_1.equals(point_3));
        Assertions.assertFalse(point_1.equals(point_4));
        Assertions.assertFalse(point_1.equals(string));
    }
}
