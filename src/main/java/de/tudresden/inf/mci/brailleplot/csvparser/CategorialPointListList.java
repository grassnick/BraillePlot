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

    public final XType getXType() {
        return XType.CATEGORIAL;
    }

    public CategorialPointListList() {
        mCategoryNames = new ArrayList<>();
    }

    public final void addCategory(final String name) {
        mCategoryNames.add(name);
    }

    public final String getCategoryName(final int index) {
        try {
            return mCategoryNames.get(index);
        } catch (Exception e) {
            return "";
        }
    }

    public final int getCategoryCount() {
        return mCategoryNames.size();
    }

    public final void setCategoryNames(final List<String> categoryNames) {
        this.mCategoryNames = categoryNames;
    }

    public final List<String> getCategoryNames() {
        return mCategoryNames;
    }

    public final double getCategorySum(final int index) {
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

    public final double getMaxYSum() {
        return mMaxYSum;
    }
}
