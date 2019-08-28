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
        mCanvas.readConfigForResolution();
        mResolution = mCanvas.getResolution();
        mPageWidth = mCanvas.getPrintableWidth();
        mPageHeight = mCanvas.getPrintableHeight();

        // calculating ranges
        double xRange = mDiagram.getMaxX() - mDiagram.getMinX();
        double yRange = mDiagram.getMaxY() - mDiagram.getMinY();

        // margins for axes
        double left_margin = 3 * mCanvas.getCellWidth() + 3 * mCanvas.getCellDistHor();
        double bottom_margin = mPageHeight - (2 * mCanvas.getCellHeight() + 2 * mCanvas.getCellDistVer());

        // x-axis:
        double last_value = left_margin;
        for (double i = left_margin; i <= mPageWidth; i += mStepSize) {
            mData.addPoint(new Point2DValued<Double, Boolean>(i, bottom_margin, true));
            last_value = i;
        }

        // arrows on x-axis
        mData.addPoint((new Point2DValued<Double, Boolean>(last_value - 0.5, bottom_margin + 0.3, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(last_value - 1, bottom_margin + 0.6, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(last_value - 0.5, bottom_margin - 0.3, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(last_value - 1, bottom_margin - 0.6, true)));

        // tick marks on x-axis
        double xTickStep = (last_value - left_margin) / mNumberXTics;
        for (int i = 1; i < mNumberXTics + 1; i++) {
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + i * xTickStep, bottom_margin + 0.5, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + i * xTickStep, bottom_margin + 1, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + i * xTickStep, bottom_margin + 1.5, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + i * xTickStep, bottom_margin - 0.5, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + i * xTickStep, bottom_margin - 1, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + i * xTickStep, bottom_margin - 1.5, true)));
        }

        // y-axis:
        last_value = bottom_margin;
        for (double i = bottom_margin; i >= 0; i -= mStepSize) {
            mData.addPoint(new Point2DValued<Double, Boolean>(left_margin, i, true));
            last_value = i;
        }

        // arrows on y-axis
        mData.addPoint((new Point2DValued<Double, Boolean>(left_margin - 0.5, last_value + 0.3, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(left_margin - 1, last_value + 0.6, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + 0.5, last_value + 0.3, true)));
        mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + 1, last_value + 0.6, true)));

        // tick marks on y-axis
        double yTickStep = (bottom_margin - last_value) / mNumberYTics;
        for (int i = 1; i < mNumberYTics + 1; i++) {
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + 0.5, bottom_margin + i * yTickStep, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + 1, bottom_margin + i * yTickStep, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin + 1.5, bottom_margin + i * yTickStep, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin - 0.5, bottom_margin + i * yTickStep, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin - 1, bottom_margin + i * yTickStep, true)));
            mData.addPoint((new Point2DValued<Double, Boolean>(left_margin - 1.5, bottom_margin + i * yTickStep, true)));
        }

    }
}
