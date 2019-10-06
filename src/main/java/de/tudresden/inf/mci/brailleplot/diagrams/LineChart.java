package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;

/**
 * Representation of a line chart with basic data functions. Implements Renderable.
 * @author Andrey Ruzhanskiy
 * @version 2019.08.17
 */
public class LineChart extends Diagram {

    /**
     * Constructor for a line chart.
     * @param data The container, which holds the information about the datapoints.
     */
    public LineChart(final PointListContainer<PointList> data) {
        super(data);
    }

}
