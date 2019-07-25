package de.tudresden.inf.mci.brailleplot.csvparser;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Axis for displaying nominals.
 */
public class NominalAxis extends Axis {

    static final Logger LOG = LoggerFactory.getLogger(NominalAxis.class);

    /**
     * The mCategories displayed on the Axis in order.
     */
    protected List<String> mCategories;

    public static final double LABEL_OFFSET_HORIZONTAL_X = 15;
    public static final double LABEL_OFFSET_HORIZONTAL_Y = -10;
    public static final double POINT_OFFSET = 0.5;

    /**
     * The nominal axis gets constructed so that each tic corresponds to one
     * category. The tics are however shown.
     *
     * @param categories
     * @param size
     * @param unit
     */
    public NominalAxis(final List<String> categories, final double size, final String unit) {
        // TODO: upon implementation of vertical nominal axes set the values
        // correctly
        super(Constants.CHAR_WIDTH, LABEL_OFFSET_HORIZONTAL_X, LABEL_OFFSET_HORIZONTAL_Y, -Constants.CHAR_WIDTH, POINT_OFFSET, null, unit);

        this.mCategories = categories;

        double categorySize = size / categories.size();
        int maxLabelLength = 0;
        for (String label : categories) {
            if (label.length() > maxLabelLength) {
                maxLabelLength = label.length();
            }
        }

        if (categorySize < Constants.CHAR_WIDTH * (maxLabelLength + 1)) {
            LOG.warn(
                    "Der Platz reicht nicht aus, um Achsenbeschriftungen darzustellen. Die längste Beschriftung hat eine Länge von "
                            + maxLabelLength + " Zeichen.");
        }

        // Subdivide the axis into sections for each category
        mTicInterval = 1;
        mGridInterval = 1;
        mLabelInterval = 1;

        mTicRange = new Range(0, categories.size());
        mRange = mTicRange;
        mLabelRange = new Range(0, categories.size() - 1);

    }

    @Override
    public String formatForAxisLabel(final double value) {
        int categoryNumber = (int) Math.round(value);
        if (mCategories.size() <= categoryNumber || categoryNumber < 0) {
            return null;
        }
        // throw new IllegalArgumentException("The selected category does not
        // exist");
        return mCategories.get(categoryNumber);
    }

    @Override
    public String formatForAxisAudioLabel(final double value) {
        int categoryNumber = (int) Math.round(value);

        if (categoryNumber == 0) {
            return "|" + mCategories.get(categoryNumber);
        } else if (categoryNumber == mCategories.size()) {
            return mCategories.get(categoryNumber - 1) + "|";
        } else if (categoryNumber > 0 && categoryNumber < mCategories.size()) {
            return mCategories.get(categoryNumber - 1) + "|" + mCategories.get(categoryNumber);
        } else {
            return null;
        }
    }

    @Override
    public String formatForSymbolAudioLabel(final double value) {
        return formatForAxisLabel(value);
    }

}
