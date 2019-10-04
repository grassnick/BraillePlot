package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;

/**
 * Representation for scatter plots. Inherits from Diagram.
 * @author Richard Schmidt, Georg Graßnick
 * @version 2019.08.26
 */
public class ScatterPlot extends Diagram {

    public ScatterPlot(final PointListContainer<PointList> container) {
        super(container);
    }
}
