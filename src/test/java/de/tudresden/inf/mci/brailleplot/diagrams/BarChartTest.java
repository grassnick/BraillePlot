package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

/**
 * @author Richard Schmidt
 */
public class BarChartTest {

    public static SimplePointListContainerImpl container;
    public static LinkedList<PointList> outter_list;
    public static PointList inner_list;
    public static Point2DDouble point;

    @BeforeAll
    public static void initialize() {
        outter_list = new LinkedList();
        inner_list = new SimplePointListImpl();
        point = new Point2DDouble(1, 2);
        inner_list.pushBack(point);
        outter_list.add(inner_list);
        container = new SimplePointListContainerImpl(outter_list);
    }

    @Test
    public void testBarChart () {
        BarChart bar = new BarChart(container);
        Point2DDouble p = (Point2DDouble) bar.getDataSet().iterator().next().getListIterator().next();
        Assertions.assertEquals(1, p.getX());
        Assertions.assertEquals(2, p.getY());
    }

}
