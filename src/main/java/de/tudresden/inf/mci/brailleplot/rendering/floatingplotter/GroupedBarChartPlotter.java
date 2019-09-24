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
 * Provides a plotting algorithm for grouped bar chart data.
 * @author Richard Schmidt
 */
public final class GroupedBarChartPlotter extends AbstractBarChartPlotter implements Plotter<BarChart> {

    private double mNumBarGroup;
    private double mBarGroupWidth;

    /**
     * Plots a grouped {@link BarChart} instance onto a {@link PlotCanvas}.
     * @param diagram An instance of {@link  BarChart} representing the bar chart.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas}, this is
     * to display the given diagram.
     */
    @Override
    public void plot(final BarChart diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException {

        prereq(diagram, canvas);

        // bar drawing and filling
        mNumBar = mCatList.getSize() * mCatList.getNumberOfCategories();
        mNumBarGroup = mCatList.getSize();
        mGridHelp = new double[mNumBar];
        for (int i = 0; i < mNumBar; i++) {
            mGridHelp[i] = 0;
        }

        mBarGroupWidth = (lengthY - (mNumBarGroup + 1) * mMinDist) / mNumBarGroup;
        mBarWidth = mBarGroupWidth / mCatList.getNumberOfCategories();
        if (mBarWidth > mMaxWidth) {
            mBarWidth = mMaxWidth;
        } else if (mBarWidth < mMinWidth) {
            throw new InsufficientRenderingAreaException();
        }

        mBarDist = (lengthY - mNumBarGroup * mBarGroupWidth) / (mNumBarGroup + 1);

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

    @Override
    void drawRectangle(final int i, final int j, final double xValue) throws InsufficientRenderingAreaException {
        double startY = mBottomMargin - mBarDist - i * (mBarGroupWidth + mBarDist) - j * mBarWidth;
        double endX = calculateXValue(xValue);
        plotAndFillRectangle(startY, endX, j);
        int k = mCatList.getNumberOfCategories() * i + j;
        if (mGridHelp[k] < endX) {
            mGridHelp[k] = endX;
        }
        mLastXValue = mLeftMargin;
    }

    /**
     * Only draws grid on x-axis.
     */
    @Override
    void drawGrid() {
        FloatingPointData<Boolean> grid = mCanvas.getNewPage();

        // x-axis
        for (double i = 1; i <= 2 * mNumberXTicks; i++) {
            loop:
            for (double j = mBottomMargin; j > mTitleMargin; j -= mStepSize) {
                for (int k = 0; k < mNumBarGroup; k++) {
                    // check if j is between bars
                    if (j < mBottomMargin - k * (mBarDist + mBarGroupWidth) && j > mBottomMargin - mBarDist - k * (mBarDist + mBarGroupWidth)) {
                        Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                        if (!mData.checkPoint(point)) {
                            grid.addPoint(point);
                        }
                        continue loop;
                    }

                    // check if j is above highest bar
                    if (j < mBottomMargin - mNumBarGroup * (mBarDist + mBarGroupWidth) && j > mTitleMargin) {
                        Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                        if (!mData.checkPoint(point)) {
                            grid.addPoint(point);
                        }
                        continue loop;
                    }
                }


                // check if j is inside bar and i/2 outside bar
                double barGroupStep = mCatList.getNumberOfCategories() * mBarWidth + mBarDist;
                for (int l = 0; l < mNumBarGroup; l++) {
                    if (j <= mBottomMargin - l * barGroupStep && j >= mBottomMargin - (l + 1) * barGroupStep) {
                        for (int m = 0; m < mCatList.getNumberOfCategories(); m++) {
                            if (j <= mBottomMargin - l * barGroupStep - mBarDist - m * mBarWidth && j >= mBottomMargin - l * barGroupStep - mBarDist - (m + 1) * mBarWidth) {
                                int n = mCatList.getNumberOfCategories() * l + m;
                                if ((mLeftMargin + (i / 2) * mXTickStep) > mGridHelp[n]) {
                                    Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                                    if (!mData.checkPoint(point)) {
                                        grid.addPoint(point);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}
