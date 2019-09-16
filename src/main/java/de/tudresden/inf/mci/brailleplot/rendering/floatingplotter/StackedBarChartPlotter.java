package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.diagrams.BarChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
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
public final class StackedBarChartPlotter extends AbstractBarChartPlotter implements Plotter<BarChart> {
    
    /**
     * Plots a stacked {@link BarChart} instance onto a {@link PlotCanvas}.
     * @param diagram An instance of {@link  BarChart} representing the bar chart.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas}, this is
     * to display the given diagram.
     */
    @Override
    public void plot(final BarChart diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException {

        prereq(diagram, canvas);

        // bar drawing and filling
        mNumBar = mCatList.getSize();
        mGridHelp = new double[mNumBar];
        for (int i = 0; i < mNumBar; i++) {
            mGridHelp[i] = 0;
        }

        mBarWidth = (lengthY - (mNumBar + 1) * mMinDist) / mNumBar;
        if (mBarWidth > mMaxWidth) {
            mBarWidth = mMaxWidth;
        }

        mBarDist = (lengthY - mNumBar * mBarWidth) / (mNumBar + 1);

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

        drawGrid();

    }

    /**
     * Calculates mXRange with cumulated maximum value.
     */
    @Override
    void calculateRanges() {
        mXRange = Math.abs(mDiagram.getCumulatedMaxY() - mDiagram.getMinY());
    }

    @Override
    void drawRectangle(final int i, final int j, final double xValue) {
        double startY = mBottomMargin - (mBarDist + i * (mBarWidth + mBarDist));
        double endX;
        if (j == 0) {
            endX = calculateXValue(xValue);
        } else {
            endX = calculateXValue(xValue) + mLastXValue - mLeftMargin;
        }
        plotAndFillRectangle(startY, endX, j);
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

        // x-axis
        for (double i = 1; i <= 2 * mNumberXTics; i++) {
            loop:
            for (double j = mBottomMargin; j > mTitleMargin; j -= mStepSize) {
                for (int k = 0; k < mNumBar; k++) {
                    // check if j is between bars
                    if (j < mBottomMargin - k * (mBarDist + mBarWidth) && j > mBottomMargin - mBarDist - k * (mBarDist + mBarWidth)) {
                        Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                        if (!mData.checkPoint(point)) {
                            grid.addPoint(point);
                        }
                        continue loop;
                    }

                    // check if j is above highest bar
                    if (j < mBottomMargin - mNumBar * (mBarDist + mBarWidth) && j > mTitleMargin) {
                        Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                        if (!mData.checkPoint(point)) {
                            grid.addPoint(point);
                        }
                        continue loop;
                    }

                    // check if j is inside bar and i/2 outside bar
                    double barStep = lengthY / mNumBar;
                    if (j < mBottomMargin - k * barStep && j > mBottomMargin - (k + 1) * barStep) {
                        if ((mLeftMargin + (i / 2) * mXTickStep) > mGridHelp[k]) {
                            Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                            if (!mData.checkPoint(point)) {
                                grid.addPoint(point);
                            }
                            continue loop;
                        }
                    }
                }
            }
        }

    }

}
