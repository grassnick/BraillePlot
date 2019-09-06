package de.tudresden.inf.mci.brailleplot.diagrams;


import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;

/**
 * Representation for scatter plots. Extends {@link Diagram}.
 * @author Richard Schmidt
 */
public class ScatterPlot extends Diagram {

    public ScatterPlot(final PointListContainer<PointList> data) {
        super(data);
    }
}
