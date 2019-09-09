package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;

/**
 * Representation of a bar chart with basic data functions. Extends {@link Diagram}.
 * @author Richard Schmidt, Georg Graßnick
 * @version 2019.07.29
 */
public class BarChart extends Diagram {

    public BarChart(final CategoricalPointListContainer<PointList> data) {
        super(data);
    }

    /**
     * Getter for a list with x-y-Pairs: x is the index (always just counts from 0 up), y is the value.
     * @return PointList with the corresponding data set.
     */
    public PointListContainer<PointList> getDataSet() {
        return mData;
    }

    /**
     * Getter for the total number of categories.
     *
     * @return int number of categories
     */
    public int getCategoryCount() {
        return mData.getSize();
    }

}

