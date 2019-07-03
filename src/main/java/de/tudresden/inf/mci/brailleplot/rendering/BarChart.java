package de.tudresden.inf.mci.brailleplot.rendering;

/**
 * A dummy class for BarCharts.
 * @author Leonard Kupper
 * @version 2019.06.28
 */
public class BarChart extends DiagramStub {

    private double[] dummy = {0.7, 3.1, -1.65, 1.4, -3.8, -2};

    final int getNumberOfCategories() {
        return dummy.length;
    }

    final double getValue(final int categoryIndex) {
        return dummy[categoryIndex];
    }

}
