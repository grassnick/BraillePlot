package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Iterator;

import static tec.units.ri.unit.Units.METRE;

/**
 * Provides a plotting algorithm for stacked bar chart data.
 * @author Richard Schmidt
 */
public final class StackedBarChartPlotter extends AbstractBarChartPlotter implements Plotter<CategoricalBarChart> {

    /**
     * Plots a stacked {@link de.tudresden.inf.mci.brailleplot.diagrams.BarChart} instance onto a {@link PlotCanvas}.
     * @param diagram An instance of {@link  de.tudresden.inf.mci.brailleplot.diagrams.BarChart} representing the bar chart.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas},
     * if there are more data series than textures or if there are negative values in the data.
     */
    @Override
    public double plot(final CategoricalBarChart diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException {

        prereq(diagram, canvas);
        if (mDiagram.getMinY() < 0) {
            throw new InsufficientRenderingAreaException("Negative values are not supported for stacked bar charts.");
        }

        // bar drawing and filling
        mNumBar = mCatList.getSize();
        mGridHelp = new double[mNumBar];
        for (int i = 0; i < mNumBar; i++) {
            mGridHelp[i] = 0;
        }

        mBarWidth = (mLengthY - (mNumBar + 1) * mMinDist) / mNumBar;
        if (mBarWidth > mMaxWidth) {
            mBarWidth = mMaxWidth;
        } else if (mBarWidth < mMinWidth) {
            throw new InsufficientRenderingAreaException("There are too many data series for the size of paper.");
        }

        mBarDist = (mLengthY - mNumBar * mBarWidth) / (mNumBar + 1);

        Iterator<PointList> bigListIt = mCatList.iterator();
        for (int i = 0; i < mCatList.getSize(); i++) {
            if (bigListIt.hasNext()) {
                PointList smallList = bigListIt.next();
                Iterator<Point2DDouble> smallListIt = smallList.iterator();
                for (int j = 0; j < smallList.getSize(); j++) {
                    if (smallListIt.hasNext()) {
                        Point2DDouble point = smallListIt.next();
                        double xValue = point.getY();
                        drawRectangle(i, j, xValue);
                    }
                }
            }
        }

        nameYAxis();
        if (mGrid) {
            drawGrid();
        }
        plotLegend();

        return 0;

    }

    /**
     * Calculates mXRange with cumulated maximum value.
     */
    @Override
    void calculateRanges() {
        mYRange = Math.abs(mDiagram.getCumulatedMaxY() - mDiagram.getMinY());
    }

    /**
     * Draws axes without tick marks on y-axis.
     */
    @Override
    void drawAxes() {
        mAxesDerivation = mCanvas.getAxesDerivation();

        // margin left of y-axis
        mLeftMargin = 2 * mCanvas.getCellWidth() + WMULT * mCanvas.getCellDistHor() + mCanvas.getMarginLeft();
        // margin from bottom to x-axis
        mBottomMargin = mPageHeight - (HMULT * mCanvas.getCellHeight() + HMULT * mCanvas.getCellDistVer());
        // margin from top for title
        mTitleMargin = TMULT * mCanvas.getCellHeight() + (TMULT + 1) * mCanvas.getCellDistVer()  + mCanvas.getMarginTop();

        if (mAxesDerivation) {
            mXTickDistance = mLeftMargin;
            if (mXTickDistance < MINXTICKDISTANCEDER) {
                mXTickDistance = MINXTICKDISTANCEDER;
            }
        } else {
            mXTickDistance = mLeftMargin + 2 * mCanvas.getCellWidth();
            if (mXTickDistance < MINXTICKDISTANCE) {
                mXTickDistance = MINXTICKDISTANCE;
            }
        }

        // x-axis
        double lastValueX = mLeftMargin;
        for (double i = mLeftMargin; i <= mPageWidth - mCanvas.getCellDistHor(); i += mStepSize) {
            addPoint(i, mBottomMargin);
            lastValueX = i;
        }
        mLengthX = lastValueX - mLeftMargin;
        mNumberXTicks = (int) Math.floor(mLengthX / mXTickDistance);
        if (mNumberXTicks < 2) {
            mNumberXTicks = 2;
        } else if (mNumberXTicks <= XTICKS4) {
            mNumberXTicks = XTICKS4;
        } else if (mNumberXTicks <= XTICKS5) {
            mNumberXTicks = XTICKS5;
        } else if (mNumberXTicks <= XTICKS6) {
            mNumberXTicks = XTICKS6;
        } else {
            mNumberXTicks = XTICKSEND2;
        }

        mScaleX = new double[mNumberXTicks + 1];


        // tick marks on x-axis
        mXTickStep = (lastValueX - MARGIN - mLeftMargin) / mNumberXTicks;

        // make tick step a multiple of step size for better texture rendering
        int scale = (int) Math.ceil(mXTickStep / mStepSize);
        double offset = 2 * mCanvas.getCellWidth();
        if (mAxesDerivation) {
            offset = 0;
        }
        if (scale * mStepSize * mNumberXTicks > mLengthX - offset) {
            // if new theoretical length of x-axis is longer than actual length
            scale = (int) Math.floor(mXTickStep / mStepSize);
        }
        mXTickStep = scale * mStepSize;

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

        // y-axis
        double lastValueY = mBottomMargin;
        for (double i = mBottomMargin; i > mTitleMargin; i -= mStepSize) {
            addPoint(mLeftMargin, i);
            lastValueY = i;
        }

        mLengthY = mBottomMargin - lastValueY;

    }

    @Override
    void drawRectangle(final int i, final int j, final double xValue) throws InsufficientRenderingAreaException {
        double startY = mBottomMargin - (mBarDist + i * (mBarWidth + mBarDist));
        double endX;
        double starterX;
        if (j == 0) {
            endX = calculateXValue(xValue);
            starterX = mLeftMargin;
        } else {
            endX = calculateXValue(xValue) + mLastXValue - mLeftMargin;
            starterX = mLastXValue;
        }
        plotAndFillRectangle(startY, starterX, endX, j, false);
        if (mGridHelp[i] < endX) {
            mGridHelp[i] = endX;
        }
        mLastXValue = endX;
    }

    /**
     * Only draws grid on x-axis.
     */
    @Override
    void drawGrid() {
        FloatingPointData<Boolean> grid = mCanvas.getNewPage();

        double marginLeft = mCanvas.getFloatConstraintLeft();
        double marginRight = mCanvas.getMarginRight();

        // x-axis
        for (double i = 1; i <= 2 * mNumberXTicks; i++) {
            loop:
            for (double j = mBottomMargin; j > mTitleMargin; j -= mStepSize) {
                for (int k = 0; k < mNumBar; k++) {
                    // check if j is between bars
                    if (j < mBottomMargin - k * (mBarDist + mBarWidth) && j > mBottomMargin - mBarDist - k * (mBarDist + mBarWidth)) {
                        double x = mLeftMargin + (i / 2) * mXTickStep;
                        // mirroring for grid on the other side of the paper
                        double newX = mPageWidth - x - marginLeft + marginRight;
                        Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(newX, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j + 2, MetricPrefix.MILLI(METRE)), true);
                        Point2DValued<Quantity<Length>, Boolean> checkPoint = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                        if (!mData.pointExists(checkPoint)) {
                            grid.addPointIfNotExisting(point);
                        }
                        continue loop;
                    }

                    // check if j is above highest bar
                    if (j < mBottomMargin - mNumBar * (mBarDist + mBarWidth) && j > mTitleMargin) {
                        double x = mLeftMargin + (i / 2) * mXTickStep;
                        // mirroring for grid on the other side of the paper
                        double newX = mPageWidth - x - marginLeft + marginRight;
                        Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(newX, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j + 2, MetricPrefix.MILLI(METRE)), true);
                        Point2DValued<Quantity<Length>, Boolean> checkPoint = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                        if (!mData.pointExists(checkPoint)) {
                            grid.addPointIfNotExisting(point);
                        }
                        continue loop;
                    }

                    // check if j is inside bar and i/2 outside bar
                    double barStep = mLengthY / mNumBar;
                    if (j < mBottomMargin - k * barStep && j > mBottomMargin - (k + 1) * barStep) {
                        if ((mLeftMargin + (i / 2) * mXTickStep) > mGridHelp[k]) {
                            double x = mLeftMargin + (i / 2) * mXTickStep;
                            // mirroring for grid on the other side of the paper
                            double newX = mPageWidth - x - marginLeft + marginRight;
                            Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(newX, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j + 2, MetricPrefix.MILLI(METRE)), true);
                            Point2DValued<Quantity<Length>, Boolean> checkPoint = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                            if (!mData.pointExists(checkPoint)) {
                                grid.addPointIfNotExisting(point);
                            }
                            continue loop;
                        }
                    }
                }
            }
        }

    }

    @Override
    void nameYAxis() throws InsufficientRenderingAreaException {

        double height = mCanvas.getCellHeight();
        double width = mCanvas.getCellWidth();
        double startX = mLeftMargin - 2 * mCanvas.getCellDistHor() - width;
        double halfCell = (height - mCanvas.getDotDiameter()) / 2;

        LiblouisBrailleTextPlotter tplotter = new LiblouisBrailleTextPlotter(mCanvas.getPrinter());

        for (int i = 0; i < mNumBar; i++) {
            Rectangle rect = new Rectangle(startX, mBottomMargin - mNumBar * (mBarDist + mBarWidth) + mBarWidth / 2 - halfCell + i * (mBarDist + mBarWidth), width, height);
            BrailleText text = new BrailleText(Character.toString(mSymbols[i]), rect, BrailleLanguage.Language.DE_BASISSCHRIFT);
            tplotter.plot(text, mCanvas);
        }
    }

}
