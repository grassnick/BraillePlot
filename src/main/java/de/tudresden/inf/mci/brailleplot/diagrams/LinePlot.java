package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.csvparser.PointListList;

/**
 * Representation of a line plot. Inherits from Diagram.
 * @author Richard Schmidt
 */
public class LinePlot extends Diagram {

    public LinePlot(final PointListList p) {
        this.mP = p;
        p.updateMinMax();
    }
}
