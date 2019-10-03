package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;

/**
 * Representation of a grouped bar chart.
 * @author Richard Schmidt
 */
public class GroupedBarChart extends CategoricalBarChart {

    public GroupedBarChart(final CategoricalPointListContainer<PointList> data) {
        super(data);
    }
}
