package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static tec.units.ri.unit.Units.METRE;

/**
 * Abstract class to provide methods for dot plotting. {@link LinePlotter} and {@link ScatterPlotter} extend this class.
 * @param <T> Type of diagram the plotter can plot. Needs to extend {@link Diagram}.
 * Can be {@link de.tudresden.inf.mci.brailleplot.diagrams.LinePlot} or {@link de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot}.
 * @author Richard Schmidt
 */
abstract class AbstractPointPlotter<T extends Diagram> extends AbstractPlotter<T> {

    boolean mFrames = true;
    private boolean mRightAxis;

    /**
     * Draws x- and y-axis.
     */
    @Override
    void drawAxes() {

        // margin left of y-axis
        mLeftMargin = (WMULT + 1) * mCanvas.getCellWidth() + WMULT * mCanvas.getCellDistHor();
        // margin from bottom to x-axis
        mBottomMargin = mPageHeight - (HMULT * mCanvas.getCellHeight() + HMULT * mCanvas.getCellDistVer());
        // margin from top for title
        mTitleMargin = TMULT * mCanvas.getCellHeight() + TMULT * mCanvas.getCellDistVer();

        mTickDistance = mLeftMargin;
        if (mTickDistance < THIRTY) {
            mTickDistance = THIRTY;
        }

        mRightAxis = mCanvas.getSecondAxis();

        double secondAxis = 0;
        if (mRightAxis) {
            secondAxis = mLeftMargin;
        }

        // x-axis
        double lastValueX = mLeftMargin;
        for (double i = mLeftMargin; i <= mPageWidth - secondAxis; i += mStepSize) {
            addPoint(i, mBottomMargin);
            lastValueX = i;
        }
        mLengthX = lastValueX - mLeftMargin;
        mNumberXTicks = (int) Math.floor(mLengthX / mTickDistance);
        if (mNumberXTicks < 2) {
            mNumberXTicks = 2;
        } else if (mNumberXTicks <= SIX) {
            mNumberXTicks = SIX;
        } else if (mNumberXTicks <= ELEVEN) {
            mNumberXTicks = ELEVEN;
        } else if (mNumberXTicks <= SIXTEEN) {
            mNumberXTicks = SIXTEEN;
        } else {
            mNumberXTicks = TWENTYONE;
        }

        mScaleX = new int[mNumberXTicks + 1];

        // tick marks on x-axis
        mXTickStep = (lastValueX - MARGIN - mLeftMargin) / mNumberXTicks;
        for (double i = 1; i <= 2 * mNumberXTicks; i++) {
            if (i % 2 == 0) {
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK2);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK3);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK4);
            } else {
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK2);
            }
        }

        // y-axis on left side
        double lastValueY = mBottomMargin;
        for (double i = mBottomMargin; i > mTitleMargin; i -= mStepSize) {
            addPoint(mLeftMargin, i);
            lastValueY = i;
        }
        mLengthY = mBottomMargin - lastValueY;
        mNumberYTicks = (int) Math.floor(mLengthY / mTickDistance);
        if (mNumberYTicks < 2) {
            mNumberYTicks = 2;
        } else if (mNumberYTicks <= SIX) {
            mNumberYTicks = SIX;
        } else if (mNumberYTicks <= ELEVEN) {
            mNumberYTicks = ELEVEN;
        } else if (mNumberYTicks <= SIXTEEN) {
            mNumberYTicks = SIXTEEN;
        } else {
            mNumberYTicks = TWENTYONE;
        }

        mScaleY = new int[mNumberYTicks + 1];

        // y-axis on right side
        if (mRightAxis) {
            for (double i = mBottomMargin; i > mTitleMargin; i -= mStepSize) {
                addPoint(lastValueX, i);
            }
        }

        // tick marks on y-axis
        mYTickStep = (mBottomMargin - lastValueY - MARGIN) / mNumberYTicks;
        for (double i = 1; i <= 2 * mNumberYTicks; i++) {
            if (i % 2 == 0) {
                addPoint(mLeftMargin - TICK1, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin - TICK2, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin - TICK3, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin - TICK4, mBottomMargin - (i / 2) * mYTickStep);

                if (mRightAxis) {
                    addPoint(lastValueX + TICK1, mBottomMargin - (i / 2) * mYTickStep);
                    addPoint(lastValueX + TICK2, mBottomMargin - (i / 2) * mYTickStep);
                    addPoint(lastValueX + TICK3, mBottomMargin - (i / 2) * mYTickStep);
                    addPoint(lastValueX + TICK4, mBottomMargin - (i / 2) * mYTickStep);
                }
            } else {
                addPoint(mLeftMargin - TICK1, mBottomMargin - (i / 2) * mYTickStep);
                addPoint(mLeftMargin - TICK2, mBottomMargin - (i / 2) * mYTickStep);

                if (mRightAxis) {
                    addPoint(lastValueX + TICK1, mBottomMargin - (i / 2) * mYTickStep);
                    addPoint(lastValueX + TICK2, mBottomMargin - (i / 2) * mYTickStep);
                }
            }
        }
    }

    /**
     * Draw a grid with twice as many lines as ticks per axis.
     */
    @Override
    void drawGrid() {
        FloatingPointData<Boolean> grid = mCanvas.getNewPage();

        double secondAxis = 0;
        if (mRightAxis) {
            secondAxis = mLeftMargin;
        }

        // x-axis
        for (double i = 1; i <= 2 * mNumberXTicks; i++) {
            for (double j = mBottomMargin - mStepSize; j > mTitleMargin; j -= mStepSize) {
                Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                if (!mData.checkPoint(point)) {
                    grid.addPoint(point);
                }
            }
        }

        // y-axis
        for (double i = 1; i <= 2 * mNumberYTicks; i++) {
            for (double j = mLeftMargin + mStepSize; j <= mPageWidth - secondAxis; j += mStepSize) {
                Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(mBottomMargin - (i / 2) * mYTickStep, MetricPrefix.MILLI(METRE)), true);
                if (!mData.checkPoint(point)) {
                    grid.addPoint(point);
                }
            }
        }
    }


    /**
     * Adds a point by its absolute x- and y-value to the floating point data. Chooses a corresponding frame. Add new frames in if statement.
     * @param xValue Must be the absolute x-value on the paper.
     * @param yValue Must be the absolute y-value on the paper.
     * @param i Links to the data series, thus choosing one frame per data series.
     * @throws InsufficientRenderingAreaException If there are more data series than frames.
     */
    void drawPoint(final double xValue, final double yValue, final int i) throws InsufficientRenderingAreaException {
        addPoint(xValue, yValue);
        // new frames are added here
        if (mFrames) {
            if (i == 0) {
                drawCircle(xValue, yValue);
            } else if (i == 1) {
                drawX(xValue, yValue);
            } else if (i == 2) {
                drawCross(xValue, yValue);
            } else {
                throw new InsufficientRenderingAreaException("There are more data series than frames.");
            }
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
