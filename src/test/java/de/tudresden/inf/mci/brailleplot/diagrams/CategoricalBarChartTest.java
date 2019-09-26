package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimpleCategoricalPointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

/**
 * @author Richard Schmidt
 */
class CategoricalBarChartTest {

    private static SimpleCategoricalPointListContainerImpl container;

    @BeforeAll
    static void initialize() {
        LinkedList<PointList> outer_list = new LinkedList<>();
        PointList inner_list = new SimplePointListImpl();
        Point2DDouble point = new Point2DDouble(1, 2);
        inner_list.pushBack(point);
        point = new Point2DDouble(2, 2);
        inner_list.pushBack(point);
        outer_list.add(inner_list);
        LinkedList<String> cat_list = new LinkedList<>();
        container = new SimpleCategoricalPointListContainerImpl(outer_list, cat_list);
    }

    @Test
    void testBarChart () {
        CategoricalBarChart bar = new CategoricalBarChart(container);
        Point2DDouble p = bar.getDataSet().iterator().next().getListIterator().next();
        Assertions.assertEquals(1, p.getX());
        Assertions.assertEquals(2, p.getY());
        Assertions.assertEquals(4, bar.getCumulatedMaxY());
    }

}
