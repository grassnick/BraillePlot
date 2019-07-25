package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.csvparser.CategorialPointListList;
import de.tudresden.inf.mci.brailleplot.csvparser.PointListList;

import java.util.List;

/**
 * Representation of a bar chart with basic data functions.
 * @author Richard Schmidt
 */
public class BarChart /*implements Renderable*/ {
    private CategorialPointListList mP;

    public BarChart(final CategorialPointListList p) {
        this.mP = p;
        p.updateMinMax();
    }

    /**
     * Get total number of categories.
     * @return
     */
    public int getCategoryCount() {
        return mP.getCategoryCount();
    }

    /**
     * Get category names in a list.
     * @return
     */
    public List<String> getCategoryNames() {
        return mP.getCategoryNames();
    }

    /**
     * Add a category.
     * @param name
     */
    public void addCategory(final String name) {
        mP.mCategoryNames.add(name);
    }

    /**
     * Get category name by index.
     * @param index
     * @return
     */
    public String getCategoryName(final int index) {
        return mP.getCategoryName(index);
    }

    /**
     * Get sum of all the values of a category chosen by index.
     * @param index
     * @return
     */
    public double getCategorySum(final int index) {
        return mP.getCategorySum(index);
    }

    /**
     * Get the the highest sum of y-values.
     * @return
     */
    public double getMaxYSum() {
        return mP.getMaxYSum();
    }

    /**
     * Get the minimum y-value.
     * @return
     */
    public double getMinY() {
        return mP.getMinY();
    }

    /**
     * Get the maximum y-value.
     * @return
     */
    public double getMaxY() {
        return mP.getMaxY();
    }

    /**
     * Returns a list with x-y-Pairs: x is the index (always just counts from 0 up), y is the value.
     * @param index
     * @return
     */
    public PointListList.PointList getDataSet(final int index) {
        return (PointListList.PointList) mP.get(index);
    }

    /**
     * Returns a data set by index.
     * @param index
     * @return
     */
    public String getDataSetName(final int index) {
        return mP.get(index).getName();
    }

}
