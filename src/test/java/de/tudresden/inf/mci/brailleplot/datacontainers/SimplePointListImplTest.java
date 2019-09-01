package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Richard Schmidt
 */
public class SimplePointListImplTest {

    public static String name;
    public static List<Point2DDouble> initial_elements;
    public static Point2DDouble point_1;
    public static Point2DDouble point_2;
    public static SimplePointListImpl plist;

    @BeforeAll
    public static void initialize() {
        name = "test_name";
        initial_elements = new LinkedList<>();
        point_1 = new Point2DDouble(2, 3);
        point_2 = new Point2DDouble(4, 5);
        initial_elements.add(point_1);
        initial_elements.add(point_2);
    }

    @Test
    public void testSimplePointListImpl() {

        Assertions.assertThrows(NullPointerException.class, () -> {new SimplePointListImpl(null, initial_elements);});
        Assertions.assertThrows(NullPointerException.class, () -> {new SimplePointListImpl(name, null);});

        plist = new SimplePointListImpl(name, initial_elements);
        Assertions.assertEquals(name, plist.getName());

        Iterator<Point2DDouble> it = plist.getListIterator();
        Point2DDouble p = it.next();
        Assertions.assertEquals(2, p.getX());
        Assertions.assertEquals(3, p.getY());

        p = it.next();

        Assertions.assertEquals(4, p.getX());
        Assertions.assertEquals(5, p.getY());

    }
}
