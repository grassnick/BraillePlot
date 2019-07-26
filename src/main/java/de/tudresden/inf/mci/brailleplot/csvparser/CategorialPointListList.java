package de.tudresden.inf.mci.brailleplot.csvparser;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link PointListList} storing a list of category names. The mX values of the
 * added points should correspond to the index of their category in the category
 * list.
 */
public class CategorialPointListList extends PointListList {

    private static final long serialVersionUID = -1291194891140659342L;

    public List<String> mCategoryNames;
    private double mMaxYSum = Double.NEGATIVE_INFINITY;

    /**
     * Getter for XType.
     * @return XType
     */
    public XType getXType() {
        return XType.CATEGORIAL;
    }

    public CategorialPointListList() {
        mCategoryNames = new ArrayList<>();
    }

    /**
     * Adds a category name to mCategoryNames.
     * @param name String
     */
    public void addCategory(final String name) {
        mCategoryNames.add(name);
    }

    /**
     * Getter for category name by index.
     * @param index int
     * @return String name
     */
    public String getCategoryName(final int index) {
        try {
            return mCategoryNames.get(index);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Getter for category count.
     * @return int count
     */
    public int getCategoryCount() {
        return mCategoryNames.size();
    }

    /**
     * Setter for category names.
     * @param categoryNames List(String)
     */
    public void setCategoryNames(final List<String> categoryNames) {
        this.mCategoryNames = categoryNames;
    }

    /**
     * Getter for list with category names as strings.
     * @return List(String) with category names.
     */
    public List<String> getCategoryNames() {
        return mCategoryNames;
    }

    /**
     * Getter for category sum by index.
     * @param index int
     * @return double sum.
     */
    public double getCategorySum(final int index) {
        double sum = 0;
        for (PointList pointList : this) {
            if (pointList.size() > index) {
                sum += pointList.get(index).getY();
            }
        }
        return sum;
    }

    @Override
    public void updateMinMax() {
        super.updateMinMax();
        for (int i = 0; i < mCategoryNames.size(); i++) {
            mMaxYSum = Math.max(mMaxYSum, getCategorySum(i));
        }
    }

    /**
     * Getter for maximum y-value sum.
     * @return double sum
     */
    public double getMaxYSum() {
        return mMaxYSum;
    }
}
