package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;
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
    boolean mFrames;
    boolean mDotFrame;
    private boolean mRightAxis;
    private static final double CIRCLEDIA = 12;
    private static final double CIRCLESCALE = 1.5;

    /**
     * Calculates the absolute y-value on the paper.
     * @param y Value as in data.
     * @return Calculated y-value.
     */
    double calculateYValue(final double y) {
        double ratio = mYTickStep / (mScaleY[1] - mScaleY[0]);
        return mBottomMargin - mYTickStep - (y / Math.pow(TEN, mScaleY[mScaleY.length - 1]) - mScaleY[0]) * ratio;
    }

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
                mLeftMargin = (WMULT + MARGINSCALE) * mCanvas.getCellWidth() + (WMULT + 2) * mCanvas.getCellDistHor();
            }
        } else {
            if (mAxesDerivation) {
                mLeftMargin = WMULT * mCanvas.getCellWidth() + WMULT * mCanvas.getCellDistHor();
                mSecondAxis = mLeftMargin;
            } else {
                mLeftMargin = (WMULT + MARGINSCALE) * mCanvas.getCellWidth() + (WMULT + 2) * mCanvas.getCellDistHor();
                mSecondAxis = 2 * mCanvas.getCellWidth() + mCanvas.getCellDistHor();
            }
        }

        // margin from bottom to x-axis
        mBottomMargin = mPageHeight - (HMULT * mCanvas.getCellHeight() + HMULT * mCanvas.getCellDistVer());
        // margin from top for title
        mTitleMargin = TMULT * mCanvas.getCellHeight() + TMULT * mCanvas.getCellDistVer();

        mXTickDistance = mLeftMargin + mCanvas.getCellWidth() / 2;
        if (mXTickDistance < MINXTICKDISTANCE) {
            mXTickDistance = MINXTICKDISTANCE;
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
        } else if (mNumberXTicks <= XTICKS1) {
            mNumberXTicks = XTICKS1;
        } else if (mNumberXTicks <= XTICKS2) {
            mNumberXTicks = XTICKS2;
        } else if (mNumberXTicks <= XTICKS3) {
            mNumberXTicks = XTICKS3;
        } else {
            mNumberXTicks = XTICKSEND;
        }

        mScaleX = new double[mNumberXTicks + 1];

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
        } else if (mNumberYTicks <= XTICKS1) {
            mNumberYTicks = XTICKS1;
        } else if (mNumberYTicks <= XTICKS2) {
            mNumberYTicks = XTICKS2;
        } else if (mNumberYTicks <= XTICKS3) {
            mNumberYTicks = XTICKSEND;
        } else {
            mNumberYTicks = XTICKSEND;
        }

        mScaleY = new double[mNumberYTicks + 1];

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

        double marginLeft = mCanvas.getFloatConstraintLeft();

        // x-axis
        for (double i = 1; i <= 2 * mNumberXTicks; i++) {
            for (double j = mBottomMargin - mStepSize; j > mTitleMargin; j -= mStepSize) {
                double x = mLeftMargin + (i / 2) * mXTickStep;
                // mirroring for grid on the other side of the paper
                double newX = mPageWidth - x + marginLeft;
                Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(newX, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j + 2, MetricPrefix.MILLI(METRE)), true);
                Point2DValued<Quantity<Length>, Boolean> checkPoint = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                if (!mData.pointExists(checkPoint)) {
                    grid.addPointIfNotExisting(point);
                }
            }
        }

        // y-axis
        for (double i = 1; i <= 2 * mNumberYTicks; i++) {
            for (double j = mLeftMargin + mStepSize; j <= mPageWidth - secondAxis; j += mStepSize) {
                // mirroring for grid on the other side of the paper
                double newX = mPageWidth - j + marginLeft;
                Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(newX, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(mBottomMargin - (i / 2) * mYTickStep + 2, MetricPrefix.MILLI(METRE)), true);
                Point2DValued<Quantity<Length>, Boolean> checkPoint = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(mBottomMargin - (i / 2) * mYTickStep, MetricPrefix.MILLI(METRE)), true);
                if (!mData.pointExists(checkPoint)) {
                    grid.addPointIfNotExisting(point);
                }
            }
        }
    }

    @Override
    void nameYAxis() throws InsufficientRenderingAreaException {

        double height = mCanvas.getCellHeight();
        double width = mCanvas.getCellWidth();
        double startX = mLeftMargin - mCanvas.getCellDistHor() - width - DISTYAXISNAMES * mStepSize;
        double secondX = mPageWidth - mSecondAxis + DISTSECAXIS * mStepSize;
        double halfCell = height / 2;

        LiblouisBrailleTextPlotter tplotter = new LiblouisBrailleTextPlotter(mCanvas.getPrinter());

        if (!mRightAxis) {
            if (mAxesDerivation) {
                for (int i = 0; i < mNumberYTicks; i++) {
                    Rectangle rect = new Rectangle(startX, mBottomMargin - mNumberYTicks * mYTickStep - halfCell + i * mYTickStep, width, height);
                    BrailleText text = new BrailleText(Character.toString(mSymbols[i]), rect, BrailleLanguage.Language.DE_BASISSCHRIFT);
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
                        rect = new Rectangle(startX - width - DISTYAXISNAMES2 * mCanvas.getCellDistHor(), mBottomMargin - (i + 1) * mYTickStep - halfCell, width, height);
                    }
                    BrailleText text = new BrailleText(Integer.toString((int) mScaleY[i]), rect, BrailleLanguage.Language.DE_BASISSCHRIFT);
                    tplotter.plot(text, mCanvas);

                }
            }
        } else {
            if (mAxesDerivation) {
                for (int i = 0; i < mNumberYTicks; i++) {
                    Rectangle rect = new Rectangle(startX, mBottomMargin - mNumberYTicks * mYTickStep - halfCell + i * mYTickStep, width, height);
                    BrailleText text = new BrailleText(Character.toString(mSymbols[i]), rect, BrailleLanguage.Language.DE_BASISSCHRIFT);
                    tplotter.plot(text, mCanvas);

                    rect = new Rectangle(secondX, mBottomMargin - mNumberYTicks * mYTickStep - halfCell + i * mYTickStep, width, height);
                    text = new BrailleText(Character.toString(mSymbols[i]), rect, BrailleLanguage.Language.DE_BASISSCHRIFT);
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
                        rect = new Rectangle(startX - width - DISTYAXISNAMES2 * mCanvas.getCellDistHor(), mBottomMargin - (i + 1) * mYTickStep - halfCell, width, height);
                    }

                    BrailleText text = new BrailleText(Integer.toString((int) mScaleY[i]), rect, BrailleLanguage.Language.DE_BASISSCHRIFT);
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
        if (mDotFrame) {
            drawDot(xValue, yValue);
        } else if (mFrames) {
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
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue + mCanvas.getDotDiameter() + 1, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue, MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue - mCanvas.getDotDiameter() - 1, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue, MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue - mCanvas.getDotDiameter() - 1, MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue + mCanvas.getDotDiameter() + 1, MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue + mCanvas.getDotDiameter() + 1, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue + mCanvas.getDotDiameter() + 1, MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue + mCanvas.getDotDiameter() + 1, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue - mCanvas.getDotDiameter() - 1, MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue - mCanvas.getDotDiameter() - 1, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue + mCanvas.getDotDiameter() + 1, MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue - mCanvas.getDotDiameter() - 1, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue - mCanvas.getDotDiameter() - 1, MetricPrefix.MILLI(METRE)), true));
    }

    /**
     * Draws an X with absolute xValue and yValue as center.
     * @param xValue Absolute x-value of center.
     * @param yValue Absolute y-value of center.
     */
    void drawX(final double xValue, final double yValue) {
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue + mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue + mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue + 2 * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue + 2 * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue + THREE * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue + THREE * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));

        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue - mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue - mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue - 2 * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue - 2 * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue - THREE * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue - THREE * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));

        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue + mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue - mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue + 2 * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue - 2 * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue + THREE * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue - THREE * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));

        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue - mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue + mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue - 2 * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue + 2 * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(xValue - THREE * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), Quantities.getQuantity(yValue + THREE * mCanvas.getDotDiameter(), MetricPrefix.MILLI(METRE)), true));
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
            addPoint(xValue + CIRCLEDIA / 2, yValue);
        }

        addPoint(lastX + CIRCLESCALE * mStepSize / CIRCLESCALE2, yValue + mStepSize);
        addPoint(lastX + CIRCLESCALE * mStepSize / CIRCLESCALE2, yValue - mStepSize);
        addPoint(xValue - CIRCLEDIA / 2 + mStepSize / CIRCLESCALE2, yValue + mStepSize);
        addPoint(xValue - CIRCLEDIA / 2 + mStepSize / CIRCLESCALE2, yValue - mStepSize);
    }

}
