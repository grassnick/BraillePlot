package de.tudresden.inf.mci.brailleplot.point;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Richard Schmidt
 */
public class Point2DTest {

    public static NullPointerException e;

    @BeforeAll
    public static void initialize() {
        e = new NullPointerException();
    }

    @Test
    public void testPoint2D () {
        int a = 1;
        int b = 2;
        Point2D p;

        try {
            p = new Point2D(null, b);
        } catch (Exception exc) {
            Assertions.assertEquals(e.getCause(), exc.getCause());
        }

        try {
            p = new Point2D(a, null);
        } catch (Exception exc) {
            Assertions.assertEquals(e.getCause(), exc.getCause());
        }

        p = new Point2D(a, b);
        Assertions.assertEquals(a, p.getX());
        Assertions.assertEquals(b, p.getY());
    }

    @Test
    public void testEquals () {
        Point2DDouble point_1 = new Point2DDouble(1, 2);
        Point2DDouble point_2 = new Point2DDouble(1, 2);
        Point2DDouble point_3 = new Point2DDouble(1, 3);
        Point2DDouble point_4 = new Point2DDouble(2, 2);
        String string = "test";

        Assertions.assertEquals(true, point_1.equals(point_2));
        Assertions.assertEquals(false, point_1.equals(point_3));
        Assertions.assertEquals(false, point_1.equals(point_4));
        Assertions.assertEquals(false, point_1.equals(string));
    }
}
