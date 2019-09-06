package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;

/**
 * Representation of a bar chart with basic data functions. Extends {@link Diagram}.
 * @author Richard Schmidt, Georg Graßnick
 * @version 2019.07.29
 */
public class BarChart extends Diagram {

    public BarChart(final PointListContainer<PointList> data) {
        super(data);
    }

    /**
     * Getter for the total number of categories.
     *
     * @return Number of categories.
     */
    public int getCategoryCount() {
        return mData.getSize();
    }

}

