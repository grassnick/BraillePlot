package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;

/**
 * Representation of a bar chart composed from categories of multiple bars each. Implements Renderable.
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
}
