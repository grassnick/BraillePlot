package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;

/**
 * Representation of a stacked bar chart.
 * @author Richard Schmidt
 */
public class StackedBarChart extends CategoricalBarChart {

    public StackedBarChart(final CategoricalPointListContainer<PointList> data) {
        super(data);
    }
}
