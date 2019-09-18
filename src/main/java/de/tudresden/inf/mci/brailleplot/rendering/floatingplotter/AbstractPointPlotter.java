package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static tec.units.ri.unit.Units.METRE;

/**
 * Abstract class to provide methods for dot plotting. {@link LinePlotter} and {@link ScatterPlotter} extend this class.
 * @param <T> Type of diagram the plotter can plot. Needs to extend {@link Diagram}. Can be {@link de.tudresden.inf.mci.brailleplot.diagrams.LinePlot} or {@link de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot}.
 * @author Richard Schmidt
 */
abstract class AbstractPointPlotter<T extends Diagram> extends AbstractPlotter<T> {

    /**
     * Draws x- and y-axis.
     */
    @Override
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
        lengthX = lastValueX - mLeftMargin;
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
        for (double i = 1; i <= 2 * mNumberXTics; i++) {
            if (i % 2 == 0) {
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK2);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK3);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK4);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK2);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK3);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK4);
            } else {
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK2);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK2);
            }
        }

        // y-axis
        double lastValueY = mBottomMargin;
        for (double i = mBottomMargin; i > mTitleMargin; i -= mStepSize) {
            addPoint(mLeftMargin, i);
            lastValueY = i;
        }
        lengthY = mBottomMargin - lastValueY;
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
        for (double i = 1; i <= 2 * mNumberYTics; i++) {
            if (i % 2 == 0) {
                addPoint(mLeftMargin + TICK1, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin + TICK2, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin + TICK3, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin + TICK4, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin - TICK1, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin - TICK2, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin - TICK3, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin - TICK4, mBottomMargin - (i / 2) * mYTickStep);
            } else {
                addPoint(mLeftMargin + TICK1, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin + TICK2, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin - TICK1, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin - TICK2, mBottomMargin - (i / 2) * mYTickStep);
            }
        }
    }

    /**
     * Draw a grid with twice as many lines as ticks per axis.
     */
    @Override
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
     * Adds a point by its absolute x- and y-value to the floating point data. Chooses a corresponding frame.
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
     * Draws a circle frame with absolute xValue and yValue as center.
     * @param xValue Absolute x-value of center.
     * @param yValue Absolute y-value of center.
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
     * Draws an X with absolute xValue and yValue as center.
     * @param xValue Absolute x-value of center.
     * @param yValue Absolute y-value of center.
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
     * Draws a cross with absolute xValue and yValue as center.
     * @param xValue Absolute x-value of center.
     * @param yValue Absolute y-value of center.
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
