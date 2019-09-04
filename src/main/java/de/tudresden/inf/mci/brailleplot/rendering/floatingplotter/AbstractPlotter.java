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
 * @author Richard Schmidt
 * @version 28.08.2019
 */
abstract class AbstractPlotter {

    PlotCanvas mCanvas;
    FloatingPointData<Boolean> mData;
    // arrays with int for axis ticks starting at the origin; last field contains scale factor as power of 10
    int[] scaleX;
    int[] scaleY;

    double mResolution;
    double mPageWidth;
    double mPageHeight;
    double xRange;
    double yRange;
    double xTickStep;
    double yTickStep;
    double leftMargin;
    double bottomMargin;
    double titleMargin;
    double lastValueX;
    double lastValueY;
    double mStepSize;
    int mNumberXTics;
    int mNumberYTics;

    // constants
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
    private static final int FIVE = 5;
    private static final int TEN = 10;


    AbstractPlotter() {
        scaleX = new int[mNumberXTics + 1];
        scaleY = new int[mNumberYTics + 1];
    }

    void calculateRanges(final Diagram diagram) {
        xRange = Math.abs(diagram.getMaxX() - diagram.getMinX());
        yRange = Math.abs(diagram.getMaxY() - diagram.getMinY());
    }

    void drawAxes() {
        // margin left of y-axis
        leftMargin = WMULT * mCanvas.getCellWidth() + WMULT * mCanvas.getCellDistHor();
        // margin from bottom to x-axis
        bottomMargin = mPageHeight - (HMULT * mCanvas.getCellHeight() + HMULT * mCanvas.getCellDistVer());
        // margin from top for title
        titleMargin = TMULT * mCanvas.getCellHeight() + TMULT * mCanvas.getCellDistVer();

        // x-axis
        lastValueX = leftMargin;
        for (double i = leftMargin; i <= mPageWidth; i += mStepSize) {
            addPoint(i, bottomMargin);
            lastValueX = i;
        }
        double lengthX = lastValueX - leftMargin;
        mNumberXTics = (int) Math.floor(lengthX / TICKDISTANCE);

        // arrows on x-axis
        addPoint(lastValueX - ARROWS1, bottomMargin + ARROWS1);
        addPoint(lastValueX - ARROWS2, bottomMargin + ARROWS2);
        addPoint(lastValueX - ARROWS3, bottomMargin + ARROWS3);
        addPoint(lastValueX - ARROWS1, bottomMargin - ARROWS1);
        addPoint(lastValueX - ARROWS2, bottomMargin - ARROWS2);
        addPoint(lastValueX - ARROWS3, bottomMargin - ARROWS3);

        // tick marks on x-axis
        xTickStep = (lastValueX - MARGIN - leftMargin) / mNumberXTics;
        for (int i = 1; i <= mNumberXTics; i++) {
            addPoint(leftMargin + i * xTickStep, bottomMargin + TICK1);
            addPoint(leftMargin + i * xTickStep, bottomMargin + TICK2);
            addPoint(leftMargin + i * xTickStep, bottomMargin + TICK3);
            // addPoint(leftMargin + i * xTickStep, bottomMargin + TICK4);
            addPoint(leftMargin + i * xTickStep, bottomMargin - TICK1);
            addPoint(leftMargin + i * xTickStep, bottomMargin - TICK2);
            addPoint(leftMargin + i * xTickStep, bottomMargin - TICK3);
            // addPoint(leftMargin + i * xTickStep, bottomMargin - TICK4);
        }

        // y-axis:
        lastValueY = bottomMargin;
        for (double i = bottomMargin; i > titleMargin; i -= mStepSize) {
            addPoint(leftMargin, i);
            lastValueY = i;
        }
        double lengthY = bottomMargin - lastValueY;
        mNumberYTics = (int) Math.floor(lengthY / TICKDISTANCE);

        // arrows on y-axis
        addPoint(leftMargin - ARROWS1, lastValueY + ARROWS1);
        addPoint(leftMargin - ARROWS2, lastValueY + ARROWS2);
        addPoint(leftMargin - ARROWS3, lastValueY + ARROWS3);
        addPoint(leftMargin + ARROWS1, lastValueY + ARROWS1);
        addPoint(leftMargin + ARROWS2, lastValueY + ARROWS2);
        addPoint(leftMargin + ARROWS3, lastValueY + ARROWS3);


        // tick marks on y-axis
        yTickStep = (bottomMargin - lastValueY - MARGIN) / mNumberYTics;
        for (int i = 1; i <= mNumberYTics; i++) {
            addPoint(leftMargin + TICK1, bottomMargin - i * yTickStep);
            addPoint(leftMargin + TICK2, bottomMargin - i * yTickStep);
            addPoint(leftMargin + TICK3, bottomMargin - i * yTickStep);
            // addPoint(leftMargin + TICK4, bottomMargin - i * yTickStep);
            addPoint(leftMargin - TICK1, bottomMargin - i * yTickStep);
            addPoint(leftMargin - TICK2, bottomMargin - i * yTickStep);
            addPoint(leftMargin - TICK3, bottomMargin - i * yTickStep);
            // addPoint(leftMargin - TICK4, bottomMargin - i * yTickStep);
        }
    }

    void addPoint(final double x, final double y) {
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true));
    }

    int[] scaleAxes(final Double calcRange, final Integer numberTics, final Double minimum) {

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

    void drawGrid() {
        FloatingPointData<Boolean> grid = mCanvas.getNewPage();

        // x-axis
        for (int i = 1; i <= mNumberXTics; i++) {
            for (double j = bottomMargin; j > titleMargin; j -= mStepSize) {
                grid.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(leftMargin + i * xTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true));
            }
        }

        // y-axis
        for (int i = 1; i <= mNumberYTics; i++) {
            for (double j = leftMargin; j <= mPageWidth; j += mStepSize) {
                grid.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(bottomMargin - i * yTickStep, MetricPrefix.MILLI(METRE)), true));
            }
        }
    }

}
