package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.csvparser.PointListList;

/**
 * Representation of a line plot.
 * @author Richard Schmidt
 */
public class LinePlot extends Diagram {

    public LinePlot(final PointListList p) {
        this.p = p;
        p.updateMinMax();
    }
}
