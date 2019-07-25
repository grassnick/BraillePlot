package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.csvparser.PointListList;

/**
 * Representation for scatter plots. Inherits from Diagram.
 * @author Richard Schmidt
 */
public class ScatterPlot extends Diagram {

    public ScatterPlot(final PointListList p) {
        this.mP = p;
        p.updateMinMax();
    }
}
