package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;

/**
 * Representation of a bar chart composed from categories of multiple bars each. Implements Renderable.
 * @author Leonard Kupper
 * @version 2019.08.29
 */
public class CategoricalBarChart extends BarChart {

    private CategoricalPointListContainer<PointList> mData;

    public CategoricalBarChart(final CategoricalPointListContainer<PointList> data) {
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
}
