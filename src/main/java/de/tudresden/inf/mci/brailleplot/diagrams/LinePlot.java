package de.tudresden.inf.mci.brailleplot.diagrams;


import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;

/**
 * Representation for line plots. Extends {@link Diagram}.
 * @author Richard Schmidt
 */
public class LinePlot extends Diagram {

    public LinePlot(final PointListContainer<PointList> data) {
        super(data);
    }
}
