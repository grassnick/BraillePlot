package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.Iterator;

/**
 * Representation of a bar chart with basic data functions. Extends {@link Diagram}.
 * @author Richard Schmidt, Georg Graßnick
 * @version 2019.09.02
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

    /**
     * Adds all y-values of on data series.
     * @return double maximum y-value
     */
    public double getCumulatedMaxY() {
        double current;
        double maxY = 0;

        for (PointList list : mData) {
            Iterator<Point2DDouble> smallIt = list.getListIterator();
            current = 0;
            while (smallIt.hasNext()) {
                Point2DDouble point = smallIt.next();
                current += point.getY();
            }
            if (current > maxY) {
                maxY = current;
            }
        }

        return maxY;
    }

}

