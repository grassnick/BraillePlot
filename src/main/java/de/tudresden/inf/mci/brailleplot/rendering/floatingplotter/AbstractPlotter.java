package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
import de.tudresden.inf.mci.brailleplot.rendering.Legend;
import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import java.util.Objects;

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
    Legend mLegend;
    char[] mSymbols;

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
    double mTitleMargin;
    double mXTickDistance;
    double mXTickStep;
    double mYRange;
    double mYTickStep;
    private int mDecimalPlaces;
    private int mLen;
    private int mNewRange;
    private int mNumberTicks;
    private double mXRange;
    private boolean mSingleDigit;
    private boolean mStartOrigin;
    boolean mGrid;
    boolean mAxesDerivation;
    private int[] mDigits;

    // arrays with int for axis ticks starting at the origin; last field contains scale factor as power of 10
    double[] mScaleX;
    double[] mScaleY;

    // constants
    static final double COMPARE3 = 3;
    static final double COMPARE4 = 4;
    static final double COMPARE5 = 5;
    static final double COMPARE6 = 6;
    static final double SLOPE3 = 3;
    static final double SLOPE4 = 4;
    static final double SLOPE5 = 5;
    static final double SLOPE6 = 6;
    static final double SLOPE7 = 7;
    static final double SLOPE8 = 8;
    static final double SLOPE9 = 9;
    static final double SLOPE10 = 10;
    static final double RECTSCALE = 1.7;
    static final double STAIRSCALE2 = 2.5;
    static final int THREE = 3;
    static final int DISTYAXISNAMES2 = 3;
    static final int CIRCLESCALE2 = 3;
    static final int DASHEDLINESCALE = 3;
    static final int DOTTEDLINESCALE = 3;
    static final int UPPEREND = 5;
    static final int DISTXAXISNAMES = 5;
    static final int DISTYAXISNAMES = 5;
    static final int FULLSCALE = 5;
    static final int VERTSCALE = 5;
    static final int VERTSCALE2 = 3;
    static final int DIAGSCALE = 5;
    static final int GRIDSCALE = 5;
    static final int GRIDSCALE2 = 3;
    static final int DOTSCALE = 5;
    static final int DOTSCALE2 = 3;
    static final int STAIRSCALE = 5;
    static final int STAIRSCALE3 = 3;
    static final int XTICKS1 = 6;
    static final int DISTSECAXIS = 6;
    static final int DISTDIAGONALS = 6;
    static final int SCALESTAIRS = 7;
    static final int TEN = 10;
    static final int XTICKS2 = 11;
    static final int FIFTEEN = 15;
    static final int XTICKS3 = 16;
    static final int XTICKSEND = 21;
    static final int XTICKS4 = 5;
    static final int XTICKS5 = 10;
    static final int XTICKS6 = 15;
    static final int XTICKSEND2 = 20;
    // bar thickness on legend
    static final int BAR = 30;
    // min tick distance for x-axis
    static final int MINXTICKDISTANCE = 30;
    static final int SCALE2 = 35;
    static final int SCALE1 = 60;
    static final double HMULT = 3;
    static final double TMULT = 2;
    static final double WMULT = 4;
    static final double MARGIN = 15;
    static final double TICK1 = 2.5;
    static final double TICK2 = 5;
    static final double TICK3 = 7.5;
    static final double TICK4 = 10;
    // tick distance for y-axis
    static final double YTICKDISTANCE = 30;
    static final int MARGINSCALE = 3;

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
     * Calculates the absolute x-value on the paper.
     * @param x Value as in data.
     * @return Calculated x-value.
     */
    double calculateXValue(final double x) {
        double ratio = mXTickStep / (mScaleX[1] - mScaleX[0]);
        return (x / Math.pow(TEN, mScaleX[mScaleX.length - 1]) - mScaleX[0]) * ratio + mLeftMargin + mXTickStep;
    }

    /**
     * Adds a point by its absolute x- and y-value to the floating point data.
     * @param x Absolute x-value.
     * @param y Absolute y-value.
     */
    void addPoint(final double x, final double y) {
        mData.addPointIfNotExisting(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true));
    }

    /**
     * Scales axis numeration.
     * @param type "x" for x-axis scaling, "y" for y-axis scaling and "z" for bar charts.
     * @return Integer array with an Integer for a tick in each field. Last field contains scale factor as power of 10.
     * @throws IllegalArgumentException If argument is neither "x", "y" nor "z".
     */
    double[] scaleAxis(final String type) throws IllegalArgumentException {

        double calcRange;
        double minimum;
        String name;

        switch (type) {
            case "x":
                calcRange = mXRange;
                mNumberTicks = mNumberXTicks;
                minimum = mDiagram.getMinX();
                mStartOrigin = false;
                name = "x-axis";
                break;
            case "y":
                calcRange = mYRange;
                mNumberTicks = mNumberYTicks;
                minimum = mDiagram.getMinY();
                mStartOrigin = false;
                name = "y-axis";
                break;
            case "z":
                calcRange = mYRange;
                mNumberTicks = mNumberXTicks;
                minimum = mDiagram.getMinY();
                mStartOrigin = true;
                name = "x-axis";
                break;
            default:
                throw new IllegalArgumentException("The argument must be either 'x', 'y' or 'z'.");
        }

        int range;
        mDecimalPlaces = 0;
        double distance;
        boolean scaled = false;
        boolean negative = false;
        mSingleDigit = false;

        if (calcRange > 1) {
            range = (int) Math.ceil(calcRange);
        } else {
            mDecimalPlaces = (int) Math.floor(Math.log10(calcRange));
            range = (int) (calcRange * Math.pow(TEN, -mDecimalPlaces));
            scaled = true;
        }

        double[] array = new double[mNumberTicks + 1];

        double newMinimum = minimum;
        if (minimum > 0 && mStartOrigin) {
            range += minimum;
            newMinimum = 0;
        } else if (minimum < 0 && mStartOrigin) {
            negative = true;
        }


        // converting range into int array
        String number = String.valueOf(range);
        String[] dummy = number.split("");
        mDigits = new int[dummy.length];
        for (int i = 0; i < dummy.length; i++) {
            mDigits[i] = Integer.parseInt(dummy[i]);
        }

        mLen = mDigits.length;
        roundRange();

        // filling the array
        if (mStartOrigin && !negative) {
            distance = (double) mNewRange / (mNumberTicks);
            for (int i = 0; i < mNumberTicks; i++) {
                if (mSingleDigit) {
                    if (distance != Math.round(distance)) {
                        array[i] = (((i + 1) * distance) / Math.pow(TEN, -1));
                    } else {
                        array[i] = ((i + 1) * distance);
                    }
                } else {
                    if (distance != Math.round(distance)) {
                        array[i] = (((i + 1) * distance) / Math.pow(TEN, mLen - THREE));
                    } else {
                        array[i] = (((i + 1) * distance) / Math.pow(TEN, mLen - 2));
                    }
                }
            }
        } else {
            distance = (double) mNewRange / (mNumberTicks - 1);
            for (int i = 0; i < mNumberTicks; i++) {
                if (mSingleDigit) {
                    if (distance != Math.round(distance)) {
                        array[i] = (((i * distance) + newMinimum * Math.pow(TEN, -mDecimalPlaces)) / Math.pow(TEN, -1));
                    } else {
                        array[i] = ((i * distance) + newMinimum * Math.pow(TEN, -mDecimalPlaces));
                    }
                } else {
                    if (distance != Math.round(distance)) {
                        array[i] = (((i * distance) + newMinimum * Math.pow(TEN, -mDecimalPlaces)) / Math.pow(TEN, mLen - THREE));
                    } else {
                        array[i] = (((i * distance) + newMinimum * Math.pow(TEN, -mDecimalPlaces)) / Math.pow(TEN, mLen - 2));
                    }
                }
            }
        }

        // power of 10 which is used to scale; for legend
        if (scaled) {
            array[mNumberTicks] = mDecimalPlaces;
        } else if (mSingleDigit) {
            if (distance != Math.round(distance)) {
                array[mNumberTicks] = -1;
            } else {
                array[mNumberTicks] = mDecimalPlaces;
            }
        } else if (distance != Math.round(distance)) {
            array[mNumberTicks] = mLen - THREE;
        } else {
            array[mNumberTicks] = mLen - 2;
        }

        for (int i = 0; i < array.length; i++) {
            mLegend.addSymbolExplanation(name, Integer.toString(i), Integer.toString((int) Math.round(array[i])));
        }

        return array;
    }

    /**
     * Rounds the range to 5 or 10, if the range is a single digit.
     * Rounds the range so that the second digit is either 0 or 5 and all following digits are 0.
     */
    private void roundRange() {
        if ((mStartOrigin && mNumberTicks == FIFTEEN) || (!mStartOrigin && mNumberTicks == XTICKS3)) {
            if (mLen == 1) {
                mNewRange = mDigits[0] * TEN;
                mSingleDigit = true;
                mDecimalPlaces--;
            } else {
                mNewRange = mDigits[0] * TEN + mDigits[1];
            }
            mNewRange = closestNumber(mNewRange, FIFTEEN);
            if (mLen > 2) {
                for (int i = 2; i < mLen; i++) {
                    mNewRange = mNewRange * TEN;
                }
            }
        } else {

            // rounding
            if (mLen == 1) {
                if (mDigits[0] <= UPPEREND) {
                    mNewRange = UPPEREND;
                    mSingleDigit = true;
                } else {
                    mNewRange = TEN;
                    mLen = 2;
                }
            } else {
                if (mDigits[1] < 2) {
                    mDigits[1] = 0;
                    if (mLen > 2) {
                        for (int i = 2; i < mLen; i++) {
                            mDigits[i] = 0;
                        }
                    }
                } else if (mDigits[1] < UPPEREND) {
                    mDigits[1] = UPPEREND;
                    if (mLen > 2) {
                        for (int i = 2; i < mLen; i++) {
                            mDigits[i] = 0;
                        }
                    }
                } else {
                    mDigits[0]++;
                    for (int i = 1; i < mLen; i++) {
                        mDigits[i] = 0;
                    }
                }

                // converting back to number value
                mNewRange = 0;
                for (int i = 0; i < mLen; i++) {
                    mNewRange = (int) (mNewRange + mDigits[i] * Math.pow(TEN, mLen - i - 1));
                }

            }
        }
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
     * @throws InsufficientRenderingAreaException If a translation error occurs.
     */
    void nameXAxis() throws InsufficientRenderingAreaException {

        mSymbols = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        double startY = mBottomMargin + mCanvas.getCellDistVer() + DISTXAXISNAMES * mStepSize;
        double height = mCanvas.getCellHeight();
        double width = mCanvas.getCellWidth();

        LiblouisBrailleTextPlotter tplotter = new LiblouisBrailleTextPlotter(mCanvas.getPrinter());

        if (mAxesDerivation) {
            for (int i = 0; i < mNumberXTicks; i++) {
                Rectangle rect = new Rectangle(mLeftMargin + (i + 1) * mXTickStep - mCanvas.getCellDistHor() / 2, startY, width, height);
                BrailleText text = new BrailleText(Character.toString(mSymbols[i]), rect, BrailleLanguage.Language.DE_BASISSCHRIFT);
                tplotter.plot(text, mCanvas);
            }
        } else {
            for (int i = 0; i < mNumberXTicks; i++) {
                Rectangle rect;
                if (mScaleX[i] < TEN) {
                    // two digits
                    rect = new Rectangle(mLeftMargin + (i + 1) * mXTickStep - width - mCanvas.getCellDistHor() / 2, startY, width, height);
                } else {
                    // three digits
                    rect = new Rectangle(mLeftMargin + (i + 1) * mXTickStep - width - mCanvas.getCellDistHor() - width / 2, startY, width, height);

                }
                BrailleText text = new BrailleText(Integer.toString((int) Math.round(mScaleX[i])), rect, BrailleLanguage.Language.DE_BASISSCHRIFT);
                tplotter.plot(text, mCanvas);
            }
        }
    }

    /**
     * Puts the title above the diagram.
     * @throws InsufficientRenderingAreaException If a translation error occurs.
     */
    void nameTitle() throws InsufficientRenderingAreaException {

        int k = 0;
        double height = mCanvas.getCellHeight();
        double width = mCanvas.getCellWidth();
        double stepHor = width + mCanvas.getCellDistHor();
        double stepVer = height + mCanvas.getCellDistVer();
        //char[] title = mDiagram.getTitle().toCharArray();
        char[] title = "dummy".toCharArray();

        LiblouisBrailleTextPlotter tplotter = new LiblouisBrailleTextPlotter(mCanvas.getPrinter());

        loop:
        for (int i = 0; i < 2; i++) {
            for (double j = mCanvas.getCellDistHor(); j < mCanvas.getPrintableWidth() - THREE * (width + mCanvas.getCellDistHor()) + mCanvas.getCellDistHor(); j += stepHor) {
                if (k < title.length) {

                    // check if line break in necessary
                    int m = 0;

                    for (int l = k; l < title.length; l++) {
                        if (Character.toString(title[l]).equals(" ")) {
                            break;
                        }
                        m++;
                    }

                    if (j > mCanvas.getPrintableWidth() - (m + 1) * (width + mCanvas.getCellDistHor())) {
                        continue loop;
                    }

                    Rectangle rect = new Rectangle(j, mCanvas.getDotDistVer() + i * stepVer, width, height);
                    BrailleText text = new BrailleText(Character.toString(title[k]), rect, BrailleLanguage.Language.DE_BASISSCHRIFT);
                    k++;
                    j = tplotter.plot(text, mCanvas);
                } else {
                    break loop;
                }
            }
        }
    }

    /**
     * Plots the legend.
     * @throws InsufficientRenderingAreaException If there are more data series than frames, lines styles or textures.
     */
    void plotLegend() throws InsufficientRenderingAreaException {
        LegendPlotter plotter = new LegendPlotter();
        plotter.setPlotter(this);
        plotter.plot(mLegend, mCanvas);
    }

    /**
     * Sets mCanvas.
     * @param canvas PlotCanvas to be set.
     */
    void setCanvas(final PlotCanvas canvas) {
        mCanvas = Objects.requireNonNull(canvas);
    }

    /**
     * Sets mData by getting the current page of mCanvas.
     */
    void setData() {
        mData = mCanvas.getCurrentPage();
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
     * @throws InsufficientRenderingAreaException If a translation error occurs.
     */
    abstract void nameYAxis() throws InsufficientRenderingAreaException;

}
