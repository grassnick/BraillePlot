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
class BarChartTest {

    private static SimplePointListContainerImpl container;

    @BeforeAll
    static void initialize() {
        LinkedList<PointList> outer_list = new LinkedList<>();
        PointList inner_list = new SimplePointListImpl();
        Point2DDouble point = new Point2DDouble(1, 2);
        inner_list.pushBack(point);
        outer_list.add(inner_list);
        container = new SimplePointListContainerImpl(outer_list);
    }

    @Test
    void testBarChart () {
        BarChart bar = new BarChart(container);
        Point2DDouble p = bar.getDataSet().iterator().next().getListIterator().next();
        Assertions.assertEquals(1, p.getX());
        Assertions.assertEquals(2, p.getY());
    }

}
