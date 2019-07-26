package de.tudresden.inf.mci.brailleplot.csvparser;

import java.text.DecimalFormat;
import java.util.NoSuchElementException;

/**
 * Abstract class for Axis representation.
 */
public abstract class Axis {

    // The following offsets can and shall be overwritten by child classes
    /** X offset of horizontal axis labels. */
    public final double mLabelOffsetHorizontalX;
    /** Y offset of horizontal axis labels. */
    public final double mLabelOffsetHorizontalY;
    /** X offset of vertical axis labels. */
    public final double mLabelOffsetVerticalX;
    /** Y offset of vertical axis labels. */
    public final double mLabelOffsetVerticalY;

    protected double mTicInterval;
    protected Range mTicRange;
    protected double mGridInterval;
    protected Range mRange;
    protected double mLabelInterval;
    protected Range mLabelRange;
    protected final DecimalFormat mDecimalFormat = (DecimalFormat) DecimalFormat.getInstance(Constants.LOCALE);

    protected String mUnit;
    protected String mTitle;

    /** How much the point position shall be shifted - used for nominal axes.*/
    protected final double mPointOffset;

    /**
     * Constructor setting the label and point offsets.
     * @param labelOffsetHorizontalX double
     * @param labelOffsetHorizontalY double
     * @param labelOffsetVerticalX double
     * @param labelOffsetVerticalY double
     * @param pointOffset double
     * @param title String
     * @param unit String
     */
    public Axis(final double labelOffsetHorizontalX, final double labelOffsetHorizontalY, final double labelOffsetVerticalX,
    final double labelOffsetVerticalY, final double pointOffset, final String title, final String unit) {
        this.mLabelOffsetHorizontalX = labelOffsetHorizontalX;
        this.mLabelOffsetHorizontalY = labelOffsetHorizontalY;
        this.mLabelOffsetVerticalX = labelOffsetVerticalX;
        this.mLabelOffsetVerticalY = labelOffsetVerticalY;
        this.mPointOffset = pointOffset;
        this.mTitle = title;
        this.mUnit = unit;
    }

    /**
     * Iterator for tic lines.
     * @return AxisIterator
     */
    public AxisIterator ticLines() {
        return new AxisIterator(mTicRange, mTicInterval);
    }

    /**
     * Iterator for grid lines.
     * @return AxisIterator
     */
    public AxisIterator gridLines() {
        return new AxisIterator(mRange, mGridInterval);
    }

    /**
     * Iterator for label positions.
     * @return AxisIterator
     */
    public AxisIterator labelPositions() {
        return new AxisIterator(mLabelRange, mLabelInterval);
    }

    public abstract String formatForAxisLabel(double value);

    public abstract String formatForAxisAudioLabel(double value);

    public abstract String formatForSymbolAudioLabel(double value);

    /**
     * Iterator for the axis values.
     */
    public static class AxisIterator implements java.util.Iterator<Double>, Iterable<Double> {

        private Range mRange;
        private double mInterval;
        private double mCurrent;

        protected AxisIterator(final Range range, final double interval) {
            this(range, interval, 0);
        }

        protected AxisIterator(final Range range, final double interval, final double offset) {
            this.mRange = range;
            this.mInterval = interval;
            mCurrent = range.getFrom() + offset;
        }

        /**
         * Get the next axis value. There used to be a check for skipping the
         * zero value, but now it is not skipped anymore, because there are axis
         * configurations where the zero tics and gridlines are needed.
         */
        @Override
        public boolean hasNext() {
            return mCurrent <= mRange.getTo();
        }

        @Override
        public Double next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            double nextCurrent = this.mCurrent;
            this.mCurrent += mInterval;
            return nextCurrent;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AxisIterator iterator() {
            return this;
        }

    }

    /**
     * Getter for mTicInterval.
     * @return double mTicInterval
     */
    public double getTicInterval() {
        return mTicInterval;
    }

    /**
     * Getter for mTicRange.
     * @return double mTicRange
     */
    public Range getTicRange() {
        return mTicRange;
    }

    /**
     * Getter for mGridInterval.
     * @return double mGridInterval
     */
    public double getmGridInterval() {
        return mGridInterval;
    }

    /**
     * Getter for mRange.
     * @return Range mRange
     */
    public Range getRange() {
        return mRange;
    }

    /**
     * Getter for mLabelInterval.
     * @return double mLabelInterval
     */
    public double getLabelInterval() {
        return mLabelInterval;
    }

    /**
     * Getter for mLabelRange.
     * @return double mLabelRange
     */
    public Range getLabelRange() {
        return mLabelRange;
    }

    /**
     * Getter for mUnit.
     * @return double mUnit
     */
    public String getUnit() {
        return mUnit;
    }

    /**
     * Getter for mTitle.
     * @return double mTitle
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Getter for mPointOffset.
     * @return double mPointOffset
     */
    public double getPointOffset() {
        return mPointOffset;
    }
}
