package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;

import java.util.Objects;

/**
 * ScatterPlotter. Provides a plotting algorithm for scatter plot data. Extends Plotter.
 * @author Richard Schmidt
 */
@SuppressWarnings("MagicNumber")
final class ScatterPlotter implements Plotter<ScatterPlot> {

    ScatterPlot mDiagram;
    PlotCanvas mCanvas;
    FloatingPointData mData;

    double mResolution;
    double mPageWidth;
    double mPageHeight;

    // parameters to be identified by trial
    double mStepSize = 0.5;
    int mNumberXTics = 5;
    int mNumberYTics = 10;

    /**
     * Constructor. Create a new plotter for instances of {@link ScatterPlot}.
     */
    ScatterPlotter() {
    }

    /**
     * Plots a {@link ScatterPlot} instance onto a {@link PlotCanvas}.
     * @param diagram An instance of {@link ScatterPlot} representing the scatter plot.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas}, this is
     * to display the given diagram.
     */
    @Override
    public void plot(final ScatterPlot diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException {

        mDiagram = Objects.requireNonNull(diagram);
        mCanvas = Objects.requireNonNull(canvas);
        mData = mCanvas.getCurrentPage();
        mCanvas.readConfig();
        mResolution = mCanvas.getResolution();
        mPageWidth = mCanvas.getPrintableWidth();
        mPageHeight = mCanvas.getPrintableHeight();

        // calculating ranges
        double xRange = mDiagram.getMaxX() - mDiagram.getMinX();
        double yRange = mDiagram.getMaxY() - mDiagram.getMinY();

        // margins for axes
        double leftMargin = 3 * mCanvas.getCellWidth() + 3 * mCanvas.getCellDistHor();
        double bottomMargin = mPageHeight - (2 * mCanvas.getCellHeight() + 2 * mCanvas.getCellDistVer());

        // x-axis:
        double lastValue = leftMargin;
        for (double i = leftMargin; i <= mPageWidth; i += mStepSize) {
            mData.addPoint(new Point2DValued<Double, Boolean>(i, bottomMargin, true));
            lastValue = i;
        }

        // arrows on x-axis
        mData.addPoint((new Point2DValued<Double, Boolean>(lastValue - 0.5, bottomMargin + 0.3, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(lastValue - 1, bottomMargin + 0.6, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(lastValue - 0.5, bottomMargin - 0.3, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(lastValue - 1, bottomMargin - 0.6, true)));

        // tick marks on x-axis
        double xTickStep = (lastValue - leftMargin) / mNumberXTics;
        for (int i = 1; i < mNumberXTics + 1; i++) {
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + i * xTickStep, bottomMargin + 0.5, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + i * xTickStep, bottomMargin + 1, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + i * xTickStep, bottomMargin + 1.5, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + i * xTickStep, bottomMargin - 0.5, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + i * xTickStep, bottomMargin - 1, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + i * xTickStep, bottomMargin - 1.5, true)));
        }

        // y-axis:
        lastValue = bottomMargin;
        for (double i = bottomMargin; i >= 0; i -= mStepSize) {
            mData.addPoint(new Point2DValued<Double, Boolean>(leftMargin, i, true));
            lastValue = i;
        }

        // arrows on y-axis
        mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin - 0.5, lastValue + 0.3, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin - 1, lastValue + 0.6, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + 0.5, lastValue + 0.3, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + 1, lastValue + 0.6, true)));

        // tick marks on y-axis
        double yTickStep = (bottomMargin - lastValue) / mNumberYTics;
        for (int i = 1; i < mNumberYTics + 1; i++) {
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + 0.5, bottomMargin + i * yTickStep, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + 1, bottomMargin + i * yTickStep, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin + 1.5, bottomMargin + i * yTickStep, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin - 0.5, bottomMargin + i * yTickStep, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin - 1, bottomMargin + i * yTickStep, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(leftMargin - 1.5, bottomMargin + i * yTickStep, true)));
        }

    }
}
