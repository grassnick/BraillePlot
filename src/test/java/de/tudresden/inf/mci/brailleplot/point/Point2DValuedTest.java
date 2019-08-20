package de.tudresden.inf.mci.brailleplot.point;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Richard Schmidt
 */
public class Point2DValuedTest {

    public static NullPointerException e;

    @BeforeAll
    public static void initialize() {
        e = new NullPointerException();
    }

    @Test
    public void testPoint2DValued () {
        int a = 1;
        int b = 2;
        int value = 3;

        try {
            Point2DValued point = new Point2DValued(a, b, null);
        } catch (Exception exc) {
            Assertions.assertEquals(e.getCause(), exc.getCause());
        }

        Point2DValued point = new Point2DValued(a, b, value);
        Assertions.assertEquals(value, point.getVal());
    }

    @Test
    public void testEquals () {
        Point2DValued point_1 = new Point2DValued(1, 2, 3);
        Point2DValued point_2 = new Point2DValued(1, 2, 3);
        Point2DValued point_3 = new Point2DValued(2, 2, 3);
        Point2DValued point_4 = new Point2DValued(1, 1, 3);
        Point2DValued point_5 = new Point2DValued(1, 2, 2);
        String string = "test";

        Assertions.assertEquals(true, point_1.equals(point_2));
        Assertions.assertEquals(false, point_1.equals(point_3));
        Assertions.assertEquals(false, point_1.equals(point_4));
        Assertions.assertEquals(false, point_1.equals(point_5));
        Assertions.assertEquals(false, point_1.equals(string));
    }
}
