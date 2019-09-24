package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static tec.units.ri.unit.Units.METRE;


/**
 * Abstract parent class for all plotting algorithms. Provides methods for axis drawing and point adding.
 * @param <T> Type of diagram the plotter can plot. Needs to extend {@link Diagram}.
 * @author Richard Schmidt
 */
abstract class AbstractPlotter<T extends Diagram> {

    T mDiagram;
    PlotCanvas mCanvas;
    FloatingPointData<Boolean> mData;

    // arrays with int for axis ticks starting at the origin; last field contains scale factor as power of 10
    int[] mScaleX;
    int[] mScaleY;

    double mResolution;
    double mPageWidth;
    double mPageHeight;
    double mStepSize;
    double mXTickStep;
    double mYTickStep;
    double mLeftMargin;
    double mBottomMargin;
    double mTitleMargin;
    int mNumberXTicks;
    int mNumberYTicks;
    double lengthX;
    double lengthY;
    double mXRange;
    double mYRange;

    // constants
    static final int THREE = 3;
    static final int FOUR = 4;
    static final int FIVE = 5;
    static final int SIX = 6;
    static final int SEVEN = 7;
    static final int TEN = 10;
    static final int TWENTY = 20;
    static final int THIRTY = 30;
    static final double WMULT = 3;
    static final double HMULT = 2;
    static final double TMULT = 2;
    static final double TICKDISTANCE = 35;
    static final double ARROWS1 = 1;
    static final double ARROWS2 = 2;
    static final double ARROWS3 = 3;
    static final double MARGIN = 15;
    static final double TICK1 = 1.5;
    static final double TICK2 = 3;
    static final double TICK3 = 4.5;
    static final double TICK4 = 6;
    static final double CIRCLESCALE = 1.45;
    static final double CIRCLEDIA = 15;


    /**
     * Checks if mStepSize is smaller than mResolution. In that case, mStepSize is set to mResolution.
     */
    void checkResolution() {
        if (mStepSize < mResolution) {
            mStepSize = mResolution;
        }
    }

    /**
     * Calculates ranges of x and y values as a difference of max and min.
     */
    void calculateRanges() {
        mXRange = Math.abs(mDiagram.getMaxX() - mDiagram.getMinX());
        mYRange = Math.abs(mDiagram.getMaxY() - mDiagram.getMinY());
    }


    /**
     * Adds a point by its absolute x- and y-value to the floating point data.
     * @param x Absolute x-value.
     * @param y Absolute y-value.
     */
    void addPoint(final double x, final double y) {
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true));
    }

    /**
     * Scales axis numeration.
     * @param type "x" for x-axis scaling, "y" for y-axis scaling and "z" for bar charts.
     * @return Integer array with an Integer for a tick in each field. Last field contains scale factor as power of 10.
     * @throws IllegalArgumentException If argument is neither "x", "y" nor "z".
     */
    int[] scaleAxis(final String type) throws IllegalArgumentException {

        double calcRange;
        int numberTics;
        double minimum;

        switch (type) {
            case "x":
                calcRange = mXRange;
                numberTics = mNumberXTicks;
                minimum = mDiagram.getMinX();
                break;
            case "y":
                calcRange = mYRange;
                numberTics = mNumberYTicks;
                minimum = mDiagram.getMinY();
                break;
            case "z":
                calcRange = mYRange;
                numberTics = mNumberXTicks;
                minimum = mDiagram.getMinY();
                break;
            default:
                throw new IllegalArgumentException();
        }

        int range;
        int decimalPlaces = 0;
        boolean scaled = false;
        boolean singleDigit = false;

        if (calcRange > 1) {
            range = (int) Math.ceil(calcRange);
        } else {
            decimalPlaces = (int) Math.floor(Math.log10(calcRange));
            range = (int) (calcRange * Math.pow(TEN, -decimalPlaces));
            scaled = true;
        }

        int[] array = new int[numberTics + 1];

        double newMinimum = minimum;
        if (minimum > 0) {
            range += minimum;
            newMinimum = 0;
        }

        // converting range into int array
        String number = String.valueOf(range);
        String[] dummy = number.split("");
        int[] digits = new int[dummy.length];
        for (int i = 0; i < dummy.length; i++) {
            digits[i] = Integer.parseInt(dummy[i]);
        }

        int len = digits.length;

        // rounding
        int newRange;
        if (len == 1) {
            if (digits[0] <= SIX) {
                newRange = FIVE;
                singleDigit = true;
            } else {
                newRange = TEN;
                len = 2;
            }
        } else {
            if (digits[1] < 2) {
                digits[1] = 0;
                if (len > 2) {
                    for (int i = 2; i < len; i++) {
                        digits[i] = 0;
                    }
                }
            } else if (digits[1] < FIVE) {
                digits[1] = FIVE;
                if (len > 2) {
                    for (int i = 2; i < len; i++) {
                        digits[i] = 0;
                    }
                }
            } else {
                digits[0]++;
                for (int i = 1; i < len; i++) {
                    digits[i] = 0;
                }
            }

            // converting back to number value
            newRange = 0;
            for (int i = 0; i < len; i++) {
                newRange = (int) (newRange + digits[i] * Math.pow(TEN, len - i - 1));
            }

        }

        // filling the array
        double distance = (double) newRange / numberTics;
        for (int i = 0; i < numberTics; i++) {
            if (singleDigit) {
                array[i] = (int) (((i + 1) * distance) + newMinimum);
            } else {
                array[i] = (int) ((((i + 1) * distance) + newMinimum) / Math.pow(TEN, len - 2));
            }
        }

        // power of 10 which is used to scale; for legend
        if (scaled) {
            array[numberTics] = decimalPlaces;
        } else if (singleDigit) {
            array[numberTics] = 0;
        } else {
            array[numberTics] = len - 2;
        }

        return array;
    }

    /**
     * Calculates the absolute x-value on the paper.
     * @param x Value as in data.
     * @return Calculated x-value.
     */
    double calculateXValue(final double x) {
        double ratio = mXTickStep / (mScaleX[1] - mScaleX[0]);
        double y = (x / Math.pow(TEN, mScaleX[mScaleX.length - 1]) - mScaleX[0]) * ratio + mLeftMargin + mXTickStep;
        return y;
    }

    /**
     * Calculates the absolute y-value on the paper.
     * @param y Value as in data.
     * @return Calculated y-value.
     */
    double calculateYValue(final double y) {
        double ratio = mYTickStep / (mScaleY[1] - mScaleY[0]);
        return mBottomMargin - mYTickStep - (y / Math.pow(TEN, mScaleY[mScaleY.length - 1]) - mScaleY[0]) * ratio;
    }

    /**
     * Draws x- and y-axis.
     */
    abstract void drawAxes();

    /**
     * Draw a grid with twice as many lines as ticks per axis.
     */
    abstract void drawGrid();

}
