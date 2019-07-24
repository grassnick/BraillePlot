package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.parser.PointListList;

/**
 * Representation for scatter plots.
 * @author Richard Schmidt
 */
public class ScatterPlot extends Diagram {

    public ScatterPlot(final PointListList p) {
        this.p = p;
        p.updateMinMax();
    }
}
