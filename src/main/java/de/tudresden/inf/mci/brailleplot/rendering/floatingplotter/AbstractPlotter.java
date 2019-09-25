package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
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

    PlotCanvas mCanvas;
    FloatingPointData<Boolean> mData;
    T mDiagram;


    int mNumberXTicks;
    int mNumberYTicks;
    double mBottomMargin;
    double mLeftMargin;
    double mLengthX;
    double mLengthY;
    double mPageHeight;
    double mPageWidth;
    double mResolution;
    double mStepSize;
    double mTickDistance;
    double mTitleMargin;
    double mXTickStep;
    double mYRange;
    double mYTickStep;
    private int decimalPlaces;
    private int len;
    private int newRange;
    private int numberTicks;
    private double mXRange;
    private boolean singleDigit;
    private boolean startOrigin;
    private int[] digits;

    // arrays with int for axis ticks starting at the origin; last field contains scale factor as power of 10
    int[] mScaleX;
    int[] mScaleY;

    // constants
    static final int THREE = 3;
    static final int FOUR = 4;
    static final int FIVE = 5;
    static final int SIX = 6;
    static final int SEVEN = 7;
    static final int TEN = 10;
    static final int ELEVEN = 11;
    static final int FIFTEEN = 15;
    static final int SIXTEEN = 16;
    static final int TWENTY = 20;
    static final int TWENTYONE = 21;
    static final int THIRTY = 30;
    static final double CIRCLEDIA = 15;
    static final double CIRCLESCALE = 1.45;
    static final double HMULT = 2;
    static final double TMULT = 2;
    static final double WMULT = 3;
    static final double MARGIN = 15;
    static final double TICK1 = 1.5;
    static final double TICK2 = 3;
    static final double TICK3 = 4.5;
    static final double TICK4 = 6;

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
        double minimum;

        switch (type) {
            case "x":
                calcRange = mXRange;
                numberTicks = mNumberXTicks;
                minimum = mDiagram.getMinX();
                startOrigin = false;
                break;
            case "y":
                calcRange = mYRange;
                numberTicks = mNumberYTicks;
                minimum = mDiagram.getMinY();
                startOrigin = false;
                break;
            case "z":
                calcRange = mYRange;
                numberTicks = mNumberXTicks;
                minimum = mDiagram.getMinY();
                startOrigin = true;
                break;
            default:
                throw new IllegalArgumentException("The argument must be either 'x', 'y' or 'z'.");
        }

        int range;
        decimalPlaces = 0;
        double distance;
        boolean scaled = false;
        singleDigit = false;

        if (calcRange > 1) {
            range = (int) Math.ceil(calcRange);
        } else {
            decimalPlaces = (int) Math.floor(Math.log10(calcRange));
            range = (int) (calcRange * Math.pow(TEN, -decimalPlaces));
            scaled = true;
        }

        int[] array = new int[numberTicks + 1];

        double newMinimum = minimum;
        if (minimum > 0 && startOrigin) {
            range += minimum;
            newMinimum = 0;
        }


        // converting range into int array
        String number = String.valueOf(range);
        String[] dummy = number.split("");
        digits = new int[dummy.length];
        for (int i = 0; i < dummy.length; i++) {
            digits[i] = Integer.parseInt(dummy[i]);
        }

        len = digits.length;
        roundRange();

        // filling the array
        if (startOrigin) {
            distance = (double) newRange / (numberTicks);
            for (int i = 0; i < numberTicks; i++) {
                if (singleDigit) {
                    if (distance != Math.round(distance)) {
                        array[i] = (int) (((i + 1) * distance) / Math.pow(TEN, -1));
                    } else {
                        array[i] = (int) ((i + 1) * distance);
                    }
                } else {
                    if (distance != Math.round(distance)) {
                        array[i] = (int) (((i + 1) * distance) / Math.pow(TEN, len - THREE));
                    } else {
                        array[i] = (int) (((i + 1) * distance) / Math.pow(TEN, len - 2));
                    }
                }
            }
        } else {
            distance = (double) newRange / (numberTicks - 1);
            for (int i = 0; i < numberTicks; i++) {
                if (singleDigit) {
                    if (distance != Math.round(distance)) {
                        array[i] = (int) (((i * distance) + newMinimum * Math.pow(TEN, -decimalPlaces)) / Math.pow(TEN, -1));
                    } else {
                        array[i] = (int) ((i * distance) + newMinimum * Math.pow(TEN, -decimalPlaces));
                    }
                } else {
                    if (distance != Math.round(distance)) {
                        array[i] = (int) (((i * distance) + newMinimum * Math.pow(TEN, -decimalPlaces)) / Math.pow(TEN, len - THREE));
                    } else {
                        array[i] = (int) (((i * distance) + newMinimum * Math.pow(TEN, -decimalPlaces)) / Math.pow(TEN, len - 2));
                    }
                }
            }
        }

        // power of 10 which is used to scale; for legend
        if (scaled) {
            array[numberTicks] = decimalPlaces;
        } else if (singleDigit) {
            if (distance != Math.round(distance)) {
                array[numberTicks] = -1;
            } else {
                array[numberTicks] = decimalPlaces;
            }
        } else if (distance != Math.round(distance)) {
            array[numberTicks] = len - THREE;
        } else {
            array[numberTicks] = len - 2;
        }

        return array;
    }

    /**
     * Rounds the range to 5 or 10, if the range is a single digit.
     * Rounds the range so that the second digit is either 0 or 5 and all following digits are 0.
     */
    private void roundRange() {
        if ((startOrigin && numberTicks == FIFTEEN) || (!startOrigin && numberTicks == SIXTEEN)) {
            if (len == 1) {
                newRange = digits[0] * TEN;
                singleDigit = true;
                decimalPlaces--;
            } else {
                newRange = digits[0] * TEN + digits[1];
            }
            newRange = closestNumber(newRange, FIFTEEN);
            if (len > 2) {
                for (int i = 2; i < len; i++) {
                    newRange = newRange * TEN;
                }
            }
        } else {

            // rounding
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
     * Function to find the number closest to n and divisible by m.
     * Source: https://www.geeksforgeeks.org/find-number-closest-n-divisible-m/; 24.09.2019
     * @param n Range.
     * @param m Number of ticks.
     * @return Clostest int to n divisible by m.
     */
    private int closestNumber(final int n, final int m) {
        // find the quotient
        int q = n / m;

        // 1st possible closest number
        int n1 = m * q;

        // 2nd possible closest number
        int n2 = m * (q + 1);

        // if true, then n1 is the required closest number
        if (n1 > n2) {
            return n1;
        }

        // else n2 is the required closest number
        return n2;
    }

    /**
     * Names the axis ticks on x-axis.
     */
    void nameXAxis() {

        double startY = mBottomMargin + mCanvas.getCellDistVer();
        double height = mCanvas.getCellHeight();
        double width = mCanvas.getCellWidth();
        double halfCell = (width - mCanvas.getDotDiameter()) / 2;

        LiblouisBrailleTextPlotter tplotter = new LiblouisBrailleTextPlotter(mCanvas.getPrinter());

        for (int i = 0; i < mNumberXTicks; i++) {
            Rectangle rect = new Rectangle(mLeftMargin + (i + 1) * mXTickStep - halfCell, startY, width, height);
            BrailleText text = new BrailleText(Integer.toString(mScaleX[i]), rect);
            tplotter.plot(text, mCanvas);
        }
    }

    /**
     * Puts the title above the diagram.
     */
    void nameTitle() {

        int k = 0;
        double height = mCanvas.getCellHeight();
        double width = mCanvas.getCellWidth();
        double stepHor = width + mCanvas.getCellDistHor();
        double stepVer = height + mCanvas.getCellDistVer();
        String[] title = "Hallo".split("");

        LiblouisBrailleTextPlotter tplotter = new LiblouisBrailleTextPlotter(mCanvas.getPrinter());

        loop:
        for (int i = 0; i < 2; i++) {
            for (double j = mCanvas.getCellDistHor() + mCanvas.getDotDiameter() / 2; j < mCanvas.getPageWidth() - width - mCanvas.getCellDistHor(); j += stepHor) {
                if (k < title.length) {
                    Rectangle rect = new Rectangle(j, mCanvas.getCellDistVer() + i * stepVer, width, height);
                    BrailleText text = new BrailleText(title[k], rect);
                    k++;
                    j = tplotter.plot(text, mCanvas);
                } else {
                    break loop;
                }
            }
        }
    }

    /**
     * Draws x- and y-axis.
     */
    abstract void drawAxes();

    /**
     * Draw a grid with twice as many lines as ticks per axis.
     */
    abstract void drawGrid();

    /**
     * Names the y-axis.
     */
    abstract void nameYAxis();

}
