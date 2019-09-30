package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.Iterator;

/**
 * Representation of a bar chart composed from categories of multiple bars each. Implements {@link de.tudresden.inf.mci.brailleplot.rendering.Renderable}.
 * @author Leonard Kupper
 * @version 2019.09.02
 */
public class CategoricalBarChart extends BarChart {

    private CategoricalPointListContainer<PointList> mData;

    public CategoricalBarChart(final CategoricalPointListContainer<PointList> data) {
        /*
            This constructor is supposed to create a bar chart with categories. Since it is just an extension of
            the normal BarChart, the super() constructor is called first. The mData member is then set to "hide" the
            parents PointListContainer member.
            (Because the reference to the data of a CategoricalBarChart must be a CategoricalPointListContainer instead.)
         */
        super(data);
        mData = data;
    }

    /**
     * Gets the name of the category at the specified index.
     * @param index The index of the category.
     * @return The name of the category.
     */
    public String getCategoryName(final int index) {
        return mData.getCategory(index);
    }

    /**
     * Gets the total number of categories.
     * @return Number of categories as int.
     */
    public int getNumberOfCategories() {
        return mData.getNumberOfCategories();
    }

    /**
     * Getter for a list with x-y-Pairs: x is the index (always just counts from 0 up), y is the value.
     * @return PointList with the corresponding data set.
     */
    public CategoricalPointListContainer<PointList> getDataSet() {
        return mData;
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
                if (point.getY() >= 0) {
                    current += point.getY();
                }
            }
            if (current > maxY) {
                maxY = current;
            }
        }

        return maxY;
    }
}
