package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
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
 * Abstract class to provide methods for dot plotting. {@link LinePlotter} and {@link ScatterPlotter} extend this class.
 * @param <T> Type of diagram the plotter can plot. Needs to extend {@link Diagram}.
 * Can be {@link de.tudresden.inf.mci.brailleplot.diagrams.LinePlot} or {@link de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot}.
 * @author Richard Schmidt
 */
abstract class AbstractPointPlotter<T extends Diagram> extends AbstractPlotter<T> {

    private double mSecondAxis;
    boolean mFrames = true;
    private boolean mRightAxis;
    private static final double CIRCLEDIA = 12;
    private static final double CIRCLESCALE = 1.5;

    @Override
    void drawAxes() {

        mAxesDerivation = mCanvas.getAxesDerivation();
        mRightAxis = mCanvas.getSecondAxis();

        // margin left of y-axis
        if (!mRightAxis) {
            mSecondAxis = 0;

            if (mAxesDerivation) {
                mLeftMargin = WMULT * mCanvas.getCellWidth() + WMULT * mCanvas.getCellDistHor();
            } else {
                mLeftMargin = (WMULT + 2) * mCanvas.getCellWidth() + (WMULT + 2) * mCanvas.getCellDistHor();
            }
        } else {
            if (mAxesDerivation) {
                mLeftMargin = WMULT * mCanvas.getCellWidth() + WMULT * mCanvas.getCellDistHor();
                mSecondAxis = mLeftMargin;
            } else {
                mLeftMargin = WMULT * mCanvas.getCellWidth() + WMULT * mCanvas.getCellDistHor();
                mSecondAxis = (WMULT + 1) * mCanvas.getCellWidth() + (WMULT + 1) * mCanvas.getCellDistHor();
            }
        }

        // margin from bottom to x-axis
        mBottomMargin = mPageHeight - (HMULT * mCanvas.getCellHeight() + HMULT * mCanvas.getCellDistVer());
        // margin from top for title
        mTitleMargin = TMULT * mCanvas.getCellHeight() + TMULT * mCanvas.getCellDistVer();

        mXTickDistance = mLeftMargin + mCanvas.getCellWidth() / 2;
        if (mXTickDistance < THIRTY) {
            mXTickDistance = THIRTY;
        }

        // x-axis
        double lastValueX = mLeftMargin;
        for (double i = mLeftMargin; i <= mPageWidth - mSecondAxis; i += mStepSize) {
            addPoint(i, mBottomMargin);
            lastValueX = i;
        }
        mLengthX = lastValueX - mLeftMargin;
        mNumberXTicks = (int) Math.floor(mLengthX / mXTickDistance);
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
        mNumberYTicks = (int) Math.floor(mLengthY / YTICKDISTANCE);
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
                if (!mData.pointExists(point)) {
                    grid.addPointIfNotExisting(point);
                }
            }
        }

        // y-axis
        for (double i = 1; i <= 2 * mNumberYTicks; i++) {
            for (double j = mLeftMargin + mStepSize; j <= mPageWidth - secondAxis; j += mStepSize) {
                Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(mBottomMargin - (i / 2) * mYTickStep, MetricPrefix.MILLI(METRE)), true);
                if (!mData.pointExists(point)) {
                    grid.addPointIfNotExisting(point);
                }
            }
        }
    }

    @Override
    void nameYAxis() {

        double height = mCanvas.getCellHeight();
        double width = mCanvas.getCellWidth();
        double startX = mLeftMargin - mCanvas.getCellDistHor() - width - FIVE * mStepSize;
        double secondX = mPageWidth - mSecondAxis + SIX * mStepSize;
        double halfCell = height / 2;

        LiblouisBrailleTextPlotter tplotter = new LiblouisBrailleTextPlotter(mCanvas.getPrinter());

        if (!mRightAxis) {
            if (mAxesDerivation) {
                for (int i = 0; i < mNumberYTicks; i++) {
                    Rectangle rect = new Rectangle(startX, mBottomMargin - mNumberYTicks * mYTickStep - halfCell + i * mYTickStep, width, height);
                    BrailleText text = new BrailleText(Character.toString(mSymbols[i]), rect);
                    tplotter.plot(text, mCanvas);
                }
            } else {
                for (int i = 0; i < mNumberYTicks; i++) {
                    Rectangle rect;

                    if (mScaleY[i] < TEN) {
                        // two digits
                        rect = new Rectangle(startX - 2 * mCanvas.getCellDistHor(), mBottomMargin - (i + 1) * mYTickStep - halfCell, width, height);
                    } else {
                        // three digits
                        rect = new Rectangle(startX - width - THREE * mCanvas.getCellDistHor(), mBottomMargin - (i + 1) * mYTickStep - halfCell, width, height);
                    }
                    BrailleText text = new BrailleText(Integer.toString(mScaleY[i]), rect);
                    tplotter.plot(text, mCanvas);

                }
            }
        } else {
            if (mAxesDerivation) {
                for (int i = 0; i < mNumberYTicks; i++) {
                    Rectangle rect = new Rectangle(startX, mBottomMargin - mNumberYTicks * mYTickStep - halfCell + i * mYTickStep, width, height);
                    BrailleText text = new BrailleText(Character.toString(mSymbols[i]), rect);
                    tplotter.plot(text, mCanvas);

                    rect = new Rectangle(secondX, mBottomMargin - mNumberYTicks * mYTickStep - halfCell + i * mYTickStep, width, height);
                    text = new BrailleText(Character.toString(mSymbols[i]), rect);
                    tplotter.plot(text, mCanvas);
                }
            } else {
                for (int i = 0; i < mNumberYTicks; i++) {
                    Rectangle rect = new Rectangle(secondX, mBottomMargin - (i + 1) * mYTickStep - halfCell, width, height);
                    BrailleText text = new BrailleText(Integer.toString(mScaleY[i]), rect);
                    tplotter.plot(text, mCanvas);
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
                drawDot(xValue, yValue);
            } else if (i == 1) {
                drawX(xValue, yValue);
            } else if (i == 2) {
                drawCircle(xValue, yValue);
            } else {
                throw new InsufficientRenderingAreaException("There are more data series than frames.");
            }
        }
    }

    /**
     * Draws a cross with absolute xValue and yValue as center.
     * @param xValue Absolute x-value of center.
     * @param yValue Absolute y-value of center.
     */
    void drawDot(final double xValue, final double yValue) {
        addPoint(xValue + mCanvas.getDotDiameter(), yValue);
        addPoint(xValue - mCanvas.getDotDiameter(), yValue);
        addPoint(xValue, yValue + mCanvas.getDotDiameter());
        addPoint(xValue, yValue - mCanvas.getDotDiameter());
        addPoint(xValue + mCanvas.getDotDiameter(), yValue + mCanvas.getDotDiameter());
        addPoint(xValue + mCanvas.getDotDiameter(), yValue - mCanvas.getDotDiameter());
        addPoint(xValue - mCanvas.getDotDiameter(), yValue + mCanvas.getDotDiameter());
        addPoint(xValue - mCanvas.getDotDiameter(), yValue - mCanvas.getDotDiameter());
    }

    /**
     * Draws an X with absolute xValue and yValue as center.
     * @param xValue Absolute x-value of center.
     * @param yValue Absolute y-value of center.
     */
    void drawX(final double xValue, final double yValue) {
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
     * Draws a circle frame with absolute xValue and yValue as center.
     * @param xValue Absolute x-value of center.
     * @param yValue Absolute y-value of center.
     */
    void drawCircle(final double xValue, final double yValue) {
        double lastX = 0;

        for (double x = xValue - CIRCLEDIA / 2; x <= xValue + CIRCLEDIA / 2; x += mStepSize) {
            double root = Math.sqrt(Math.pow(CIRCLEDIA / 2, 2) - Math.pow(x - xValue, 2));
            double y1 = yValue + root;
            double y2 = yValue - root;
            addPoint(x, y1);
            addPoint(x, y2);
            lastX = x;
        }

        if (lastX < xValue + CIRCLEDIA / 2) {
            double x = xValue + CIRCLEDIA / 2;
            double root = Math.sqrt(Math.pow(CIRCLEDIA / 2, 2) - Math.pow(x - xValue, 2));
            double y1 = yValue + root;
            double y2 = yValue - root;
            addPoint(x, y1);
            addPoint(x, y2);
        }

        addPoint(lastX + CIRCLESCALE * mStepSize / THREE, yValue + mStepSize);
        addPoint(lastX + CIRCLESCALE * mStepSize / THREE, yValue - mStepSize);
        addPoint(xValue - CIRCLEDIA / 2 + mStepSize / THREE, yValue + mStepSize);
        addPoint(xValue - CIRCLEDIA / 2 + mStepSize / THREE, yValue - mStepSize);
    }

}
