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
     * Getter for the total number of categories.
     * @return int number of categories
     */
    public int getCategoryCount() {
        return mP.getCategoryCount();
    }

    /**
     * Getter for the category names in a list.
     * @return list with category names as strings
     */
    public List<String> getCategoryNames() {
        return mP.getCategoryNames();
    }

    /**
     * Add a category.
     * @param name String
     */
    public void addCategory(final String name) {
        mP.mCategoryNames.add(name);
    }

    /**
     * Getter for a category name by index.
     * @param index int
     * @return category name as a string
     */
    public String getCategoryName(final int index) {
        return mP.getCategoryName(index);
    }

    /**
     * Getter for the sum of all the values of a category chosen by index.
     * @param index int
     * @return double category sum
     */
    public double getCategorySum(final int index) {
        return mP.getCategorySum(index);
    }

    /**
     * Getter for the the highest sum of y-values.
     * @return double maximum y-sum
     */
    public double getMaxYSum() {
        return mP.getMaxYSum();
    }

    /**
     * Getter for the minimum y-value.
     * @return double minimum y-value
     */
    public double getMinY() {
        return mP.getMinY();
    }

    /**
     * Getter for the maximum y-value.
     * @return double maximum y-value
     */
    public double getMaxY() {
        return mP.getMaxY();
    }

    /**
     * Getter for a list with x-y-Pairs: x is the index (always just counts from 0 up), y is the value.
     * @param index int
     * @return PointList with the corresponding data set
     */
    public PointListList.PointList getDataSet(final int index) {
        return (PointListList.PointList) mP.get(index);
    }

    /**
     * Getter for a data set by index.
     * @param index int
     * @return name of the data set as a string
     */
    public String getDataSetName(final int index) {
        return mP.get(index).getName();
    }

}
