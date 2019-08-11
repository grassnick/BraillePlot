package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

/**
 * @author Richard Schmidt
 */
public class AbstractPointContainerTest {

    public static SimplePointListContainerImpl container;
    public static PointList list_1;
    public static PointList list_2;

    @BeforeAll
    public static void initialize() {
        container = new SimplePointListContainerImpl();
        list_1 = new SimplePointListImpl();
        list_1.pushBack(new Point2DDouble(1.0, 2.0));
        list_2 = new SimplePointListImpl();
        list_2.pushBack(new Point2DDouble(3.0, 4.0));
    }

    @Test
    public void testPushBack() {
        container.pushBack(list_1);
        container.pushBack(list_2);

        Iterator container_it = container.iterator();
        PointList list = (PointList) container_it.next();
        Iterator list_it = list.getListIterator();
        Point2DDouble point = (Point2DDouble) list_it.next();
        Assertions.assertEquals(point.getX(), 1.0);
        Assertions.assertEquals(point.getY(), 2.0);

        list = (PointList) container_it.next();
        list_it = list.getListIterator();
        point = (Point2DDouble) list_it.next();
        Assertions.assertEquals(point.getX(), 3.0);
        Assertions.assertEquals(point.getY(), 4.0);
    }

}
