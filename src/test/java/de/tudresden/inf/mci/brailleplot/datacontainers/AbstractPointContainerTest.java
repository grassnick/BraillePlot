package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

/**
 * @author Richard Schmidt
 */
class AbstractPointContainerTest {

    private static SimplePointListContainerImpl container;
    private static PointList list_1;
    private static PointList list_2;

    @BeforeAll
    static void initialize() {
        container = new SimplePointListContainerImpl();
        list_1 = new SimplePointListImpl();
        list_1.pushBack(new Point2DDouble(1.0, 2.0));
        list_2 = new SimplePointListImpl();
        list_2.pushBack(new Point2DDouble(3.0, 4.0));
    }

    @Test
    void testPushBack() {
        container.pushBack(list_1);
        container.pushBack(list_2);

        Iterator<PointList> container_it = container.iterator();
        PointList list = container_it.next();
        Iterator<Point2DDouble> list_it = list.getListIterator();
        Point2DDouble point = list_it.next();
        Assertions.assertEquals(point.getX(), 1.0);
        Assertions.assertEquals(point.getY(), 2.0);

        list = container_it.next();
        list_it = list.getListIterator();
        point = list_it.next();
        Assertions.assertEquals(point.getX(), 3.0);
        Assertions.assertEquals(point.getY(), 4.0);
    }

}
