package de.tudresden.inf.mci.brailleplot.csvparser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gregor Harlan, Jens Bornschein Idea and supervising by Jens
 *         Bornschein jens.bornschein@tu-dresden.de Copyright by Technische
 *         Universität Dresden / MCI 2014
 *
 */
public class MetricAxis extends Axis {
    public final double mAtom;
    public final int mAtomCount;
    public final List<Double> mIntervalSteps;
    public static final double LABEL_OFFSET_HORIZONTAL_X = -5;
    public static final double LABEL_OFFSET_HORIZONTAL_Y = 20;
    public static final double LABEL_OFFSET_VERTICAL_X = -10;
    public static final double LABEL_OFFSET_VERTICAL_Y = 5;
    public static final double POINT_OFFSET = 0;
    public static final double CONSTANT_0 = 0.5;
    public static final double CONSTANT_1 = 10;
    public static final double CONSTANT_2 = 100;
    public static final double CONSTANT_3 = 2.5;
    public static final double CONSTANT_4 = 0.1;
    public static final double CONSTANT_5 = 0.05;
    public static final double CONSTANT_6 = 0.01;

    public MetricAxis(final Range axisRange, final double size, final String title, final String unit) {

        // Set the label offsets
        super(LABEL_OFFSET_HORIZONTAL_X, LABEL_OFFSET_HORIZONTAL_Y, LABEL_OFFSET_VERTICAL_X, LABEL_OFFSET_VERTICAL_Y, POINT_OFFSET, title, unit);

        boolean finished = false;
        double interval = 0;
        mRange = new Range(0, 0);
        mRange.setName(axisRange.getName());
        int dimensionExp;
        double dimension;
        double factor;
        do {
            /*
             * Calculate how many tics there can maximally be without violating
             * the minimal distance of grid lines constraint.
             */
            int maxTics = (int) (size / Constants.MIN_GRID_DISTANCE);
            // Calculate which interval (virtual) the tics must minimally have.
            interval = axisRange.distance() / maxTics;
            dimensionExp = 0;

            int direction;
            if (interval < 1) {
                direction = -1;
            } else {
                direction = 1;
            }

            while (direction * CONSTANT_0 * Math.pow(CONSTANT_1, dimensionExp) < direction * interval) {
                dimensionExp += direction;
            }
            if (direction == 1) {
                dimensionExp--;
            }
            dimension = Math.pow(CONSTANT_1, dimensionExp);
            factor = getFactorForIntervalAndDimension(interval, dimension);
            finished = true;
            interval = factor * dimension * 2;
            mRange.setFrom(((int) (axisRange.getFrom() / interval)) * interval);
            mRange.setTo(((int) (axisRange.getTo() / interval)) * interval);
            if (mRange.getFrom() > axisRange.getFrom()) {
                axisRange.setFrom(mRange.getFrom() - interval);
                finished = false;
            }
            if (mRange.getTo() < axisRange.getTo()) {
                axisRange.setTo(mRange.getTo() + interval);
                finished = false;
            }
        } while (!finished);

        mGridInterval = interval;

        mTicInterval = interval; // TODO set this to 2 * interval if needed, maybe create an option
        mTicRange = new Range(Math.ceil(mRange.getFrom() / mTicInterval) * mTicInterval,
                Math.floor(mRange.getTo() / mTicInterval) * mTicInterval);

        mLabelRange = mTicRange;
        mLabelInterval = mTicInterval * 2;

        mDecimalFormat.setMaximumFractionDigits(Math.max(0, -dimensionExp + 2));

        mAtom = dimension / CONSTANT_2;
        mAtomCount = (int) (mRange.distance() / mAtom + 1);

        mIntervalSteps = new ArrayList<>();
        calculateIntervalSteps(dimension, factor);
    }

    @Override
    public String formatForAxisLabel(final double value) {
        String str = mDecimalFormat.format(value);
        if ("-0".equals(str)) {
            return "0";
        } else {
            return str;
        }
    }

    @Override
    public String formatForAxisAudioLabel(final double value) {
        return formatForAxisLabel(value);
    }

    @Override
    public String formatForSymbolAudioLabel(final double value) {
        return formatForAxisLabel(value);
    }

    /**
     * @param dimension double
     * @param factor double
     */
    private void calculateIntervalSteps(final double dimension, final double factor) {
        int i = 0;
        if (Math.abs(factor - CONSTANT_3) < Constants.EPSILON) {
            mIntervalSteps.add(i++, CONSTANT_3 * dimension);
            mIntervalSteps.add(i++, dimension);
        } else if (Math.abs(factor - 1) < Constants.EPSILON) {
            mIntervalSteps.add(i++, dimension);
        }

        mIntervalSteps.add(i++, CONSTANT_0 * dimension);
        mIntervalSteps.add(i++, CONSTANT_4 * dimension);
        mIntervalSteps.add(i++, CONSTANT_5 * dimension);
        mIntervalSteps.add(i, CONSTANT_6 * dimension);
    }

    /**
     * @param interval double
     * @param dimension double
     * @return the calculated factor
     */
    private double getFactorForIntervalAndDimension(final double interval, final double dimension) {
        double factor;
        if (interval > dimension) {
            factor = CONSTANT_3;
        } else if (interval > CONSTANT_0 * dimension) {
            factor = 1;
        } else {
            factor = CONSTANT_0;
        }
        return factor;
    }

}
