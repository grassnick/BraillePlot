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
 * @version 28.08.2019
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
    private double mXRange;
    private double mYRange;
    private double mXTickStep;
    private double mYTickStep;
    private double mLeftMargin;
    private double mBottomMargin;
    private double mTitleMargin;
    private int mNumberXTics;
    private int mNumberYTics;

    // constants
    static final int THREE = 3;
    static final int FOUR = 4;
    static final int FIVE = 5;
    static final int TEN = 10;
    private static final double WMULT = 3;
    private static final double HMULT = 2;
    private static final double TMULT = 2;
    private static final double TICKDISTANCE = 35;
    private static final double ARROWS1 = 1;
    private static final double ARROWS2 = 2;
    private static final double ARROWS3 = 3;
    private static final double MARGIN = 15;
    private static final double TICK1 = 1.5;
    private static final double TICK2 = 3;
    private static final double TICK3 = 4.5;
    private static final double TICK4 = 6;
    private static final double CIRCLESCALE = 1.45;
    private static final double CIRCLEDIA = 15;


    /**
     * Calculates ranges of x and y values as a difference of max and min.
     */
    void calculateRanges() {
        mXRange = Math.abs(mDiagram.getMaxX() - mDiagram.getMinX());
        mYRange = Math.abs(mDiagram.getMaxY() - mDiagram.getMinY());
    }

    /**
     * Draws x- and y-axis.
     */
    void drawAxes() {

        // margin left of y-axis
        mLeftMargin = WMULT * mCanvas.getCellWidth() + WMULT * mCanvas.getCellDistHor();
        // margin from bottom to x-axis
        mBottomMargin = mPageHeight - (HMULT * mCanvas.getCellHeight() + HMULT * mCanvas.getCellDistVer());
        // margin from top for title
        mTitleMargin = TMULT * mCanvas.getCellHeight() + TMULT * mCanvas.getCellDistVer();

        // x-axis
        double lastValueX = mLeftMargin;
        for (double i = mLeftMargin; i <= mPageWidth; i += mStepSize) {
            addPoint(i, mBottomMargin);
            lastValueX = i;
        }
        double lengthX = lastValueX - mLeftMargin;
        mNumberXTics = (int) Math.floor(lengthX / TICKDISTANCE);
        if (mNumberXTics < 2) {
            mNumberXTics = 2;
        } else if (mNumberXTics <= FIVE) {
            mNumberXTics = FIVE;
        } else {
            mNumberXTics = TEN;
        }

        mScaleX = new int[mNumberXTics + 1];

        // arrows on x-axis
        addPoint(lastValueX - ARROWS1, mBottomMargin + ARROWS1);
        addPoint(lastValueX - ARROWS2, mBottomMargin + ARROWS2);
        addPoint(lastValueX - ARROWS3, mBottomMargin + ARROWS3);
        addPoint(lastValueX - ARROWS1, mBottomMargin - ARROWS1);
        addPoint(lastValueX - ARROWS2, mBottomMargin - ARROWS2);
        addPoint(lastValueX - ARROWS3, mBottomMargin - ARROWS3);

        // tick marks on x-axis
        mXTickStep = (lastValueX - MARGIN - mLeftMargin) / mNumberXTics;
        for (int i = 1; i <= mNumberXTics; i++) {
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin + TICK1);
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin + TICK2);
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin + TICK3);
            // addPoint(mLeftMargin + i * mXTickStep, mBottomMargin + TICK4);
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin - TICK1);
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin - TICK2);
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin - TICK3);
            // addPoint(mLeftMargin + i * mXTickStep, mBottomMargin - TICK4);
        }

        // y-axis:
        double lastValueY = mBottomMargin;
        for (double i = mBottomMargin; i > mTitleMargin; i -= mStepSize) {
            addPoint(mLeftMargin, i);
            lastValueY = i;
        }
        double lengthY = mBottomMargin - lastValueY;
        mNumberYTics = (int) Math.floor(lengthY / TICKDISTANCE);
        if (mNumberYTics < 2) {
            mNumberYTics = 2;
        } else if (mNumberYTics <= FIVE) {
            mNumberYTics = FIVE;
        } else {
            mNumberYTics = TEN;
        }

        mScaleY = new int[mNumberYTics + 1];

        // arrows on y-axis
        addPoint(mLeftMargin - ARROWS1, lastValueY + ARROWS1);
        addPoint(mLeftMargin - ARROWS2, lastValueY + ARROWS2);
        addPoint(mLeftMargin - ARROWS3, lastValueY + ARROWS3);
        addPoint(mLeftMargin + ARROWS1, lastValueY + ARROWS1);
        addPoint(mLeftMargin + ARROWS2, lastValueY + ARROWS2);
        addPoint(mLeftMargin + ARROWS3, lastValueY + ARROWS3);


        // tick marks on y-axis
        mYTickStep = (mBottomMargin - lastValueY - MARGIN) / mNumberYTics;
        for (int i = 1; i <= mNumberYTics; i++) {
            addPoint(mLeftMargin + TICK1, mBottomMargin - i * mYTickStep);
            addPoint(mLeftMargin + TICK2, mBottomMargin - i * mYTickStep);
            addPoint(mLeftMargin + TICK3, mBottomMargin - i * mYTickStep);
            // addPoint(mLeftMargin + TICK4, mBottomMargin - i * mYTickStep);
            addPoint(mLeftMargin - TICK1, mBottomMargin - i * mYTickStep);
            addPoint(mLeftMargin - TICK2, mBottomMargin - i * mYTickStep);
            addPoint(mLeftMargin - TICK3, mBottomMargin - i * mYTickStep);
            // addPoint(mLeftMargin - TICK4, mBottomMargin - i * mYTickStep);
        }
    }

    /**
     * Adds a point by its x- and y-value to the floating point data.
     * @param x X-value.
     * @param y Y-value.
     */
    void addPoint(final double x, final double y) {
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true));
    }

    /**
     * Scales axis numeration.
     * @param type "x" for x-axis scaling and "y" for y-axis scaling.
     * @return Integer array with an Integer for a tic in each field. Last field contains scale factor as power of 10.
     * @throws IllegalArgumentException If argument is neither "x" nor "y".
     */
    int[] scaleAxis(final String type) throws IllegalArgumentException {

        double calcRange;
        int numberTics;
        double minimum;

        if (type.equals("x")) {
            calcRange = mXRange;
            numberTics = mNumberXTics;
            minimum = mDiagram.getMinX();
        } else if (type.equals("y")) {
            calcRange = mYRange;
            numberTics = mNumberYTics;
            minimum = mDiagram.getMinY();
        } else {
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
            if (digits[0] < FIVE) {
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
     * Draw a grid with twice as many lines as ticks per axis.
     */
    void drawGrid() {
        FloatingPointData<Boolean> grid = mCanvas.getNewPage();

        // x-axis
        for (double i = 1; i <= 2 * mNumberXTics; i++) {
            for (double j = mBottomMargin; j > mTitleMargin; j -= mStepSize) {
                Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                if (!mData.checkPoint(point)) {
                    grid.addPoint(point);
                }
            }
        }

        // y-axis
        for (double i = 1; i <= 2 * mNumberYTics; i++) {
            for (double j = mLeftMargin; j <= mPageWidth; j += mStepSize) {
                Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(mBottomMargin - (i / 2) * mYTickStep, MetricPrefix.MILLI(METRE)), true);
                if (!mData.checkPoint(point)) {
                    grid.addPoint(point);
                }
            }
        }
    }


    /**
     * Calculates the absolute x-value on the paper.
     * @param x Value as in data.
     * @return Calculated x-value.
     */
    double calculateXValue(final double x) {
        double ratio = mXTickStep / (mScaleX[1] - mScaleX[0]);
        return (x / Math.pow(TEN, mScaleX[mScaleX.length - 1]) - mScaleX[0]) * ratio + mLeftMargin + mXTickStep;
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
     * Adds a point by its x- and y-value to the floating point data. Chooses a corresponding frame.
     * @param xValue Must be the absolute x-value on the paper.
     * @param yValue Must be the absolute y-value on the paper.
     * @param i Links to the data series, thus choosing one frame per data series.
     */
    void drawPoint(final double xValue, final double yValue, final int i) {
        addPoint(xValue, yValue);
        if (i == 0) {
            drawCircle(xValue, yValue);
        } else if (i == 1) {
            drawX(xValue, yValue);
        } else if (i == 2) {
            drawCross(xValue, yValue);
        }
    }

    /**
     * Draws a circle frame with xValue and yValue as center.
     * @param xValue X-value of center.
     * @param yValue Y-value of center.
     */
    private void drawCircle(final double xValue, final double yValue) {
        double lastX = 0;

        for (double x = xValue - CIRCLEDIA / 2; x <= xValue + CIRCLEDIA / 2; x += mStepSize) {
            double root = Math.sqrt(Math.pow(CIRCLEDIA / 2, 2) - Math.pow(x - xValue, 2));
            double y1 = yValue + root;
            double y2 = yValue - root;
            addPoint(x, y1);
            addPoint(x, y2);
            lastX = x;
        }

        addPoint(lastX - mStepSize / THREE, yValue + CIRCLESCALE * mStepSize);
        addPoint(lastX - mStepSize / THREE, yValue - CIRCLESCALE * mStepSize);
        addPoint(xValue - CIRCLEDIA / 2 + mStepSize / THREE, yValue + CIRCLESCALE * mStepSize);
        addPoint(xValue - CIRCLEDIA / 2 + mStepSize / THREE, yValue - CIRCLESCALE * mStepSize);
    }

    /**
     * Draws an X with xValue and yValue as center.
     * @param xValue X-value of center.
     * @param yValue Y-value of center.
     */
    private void drawX(final double xValue, final double yValue) {
        addPoint(xValue + mStepSize, yValue + mStepSize);
        addPoint(xValue + 2 * mStepSize, yValue + 2 * mStepSize);
        addPoint(xValue + THREE * mStepSize, yValue + THREE * mStepSize);

        addPoint(xValue - mStepSize, yValue - mStepSize);
        addPoint(xValue - 2 * mStepSize, yValue - 2 * mStepSize);
        addPoint(xValue - THREE * mStepSize, yValue - THREE * mStepSize);

        addPoint(xValue + mStepSize, yValue - mStepSize);
        addPoint(xValue + 2 * mStepSize, yValue - 2 * mStepSize);
        addPoint(xValue + THREE * mStepSize, yValue - THREE * mStepSize);

        addPoint(xValue - mStepSize, yValue + mStepSize);
        addPoint(xValue - 2 * mStepSize, yValue + 2 * mStepSize);
        addPoint(xValue - THREE * mStepSize, yValue + THREE * mStepSize);
    }

    /**
     * Draws a cross with xValue and yValue as center.
     * @param xValue X-value of center.
     * @param yValue Y-value of center.
     */
    private void drawCross(final double xValue, final double yValue) {
        addPoint(xValue, yValue + mStepSize);
        addPoint(xValue, yValue + 2 * mStepSize);
        addPoint(xValue, yValue + THREE * mStepSize);

        addPoint(xValue, yValue - mStepSize);
        addPoint(xValue, yValue - 2 * mStepSize);
        addPoint(xValue, yValue - THREE * mStepSize);

        addPoint(xValue + mStepSize, yValue);
        addPoint(xValue + 2 * mStepSize, yValue);
        addPoint(xValue + THREE * mStepSize, yValue);

        addPoint(xValue - mStepSize, yValue);
        addPoint(xValue - 2 * mStepSize, yValue);
        addPoint(xValue - THREE * mStepSize, yValue);
    }

}
