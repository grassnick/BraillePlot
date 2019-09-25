package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.rendering.Legend;

import java.util.Iterator;
import java.util.Objects;

/**
 * Provides a plotting algorithm for scatter plot data.
 * @author Richard Schmidt
 */
public final class ScatterPlotter extends AbstractPointPlotter<ScatterPlot> implements Plotter<ScatterPlot> {

    /**
     * Plots a {@link ScatterPlot} instance onto a {@link PlotCanvas}.
     * @param diagram An instance of {@link ScatterPlot} representing the scatter plot.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas} or
     * if there are more data series than frames.
     */
    @Override
    public double plot(final ScatterPlot diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException {

        mCanvas = Objects.requireNonNull(canvas);
        mCanvas.readConfig();
        mData = mCanvas.getCurrentPage();
        mDiagram = Objects.requireNonNull(diagram);
        String title = "";
        mLegend = new Legend(title);
        mPageHeight = mCanvas.getPrintableHeight();
        mPageWidth = mCanvas.getPrintableWidth();
        mResolution = mCanvas.getResolution();
        mStepSize = mCanvas.getDotDiameter();

        checkResolution();
        calculateRanges();
        drawAxes();
        mScaleX = scaleAxis("x");
        mScaleY = scaleAxis("y");
        mCanvas.setXScaleFactor(mScaleX[mScaleX.length - 1]);
        mCanvas.setYScaleFactor(mScaleY[mScaleY.length - 1]);
        mCanvas.setType(0);
        nameXAxis();
        nameYAxis();
        nameTitle();

        // draw points and frames
        PointListContainer<PointList> bigList = mDiagram.getDataSet();
        Iterator<PointList> bigListIt = bigList.iterator();

        for (int i = 0; i < bigList.getSize(); i++) {
            if (bigListIt.hasNext()) {
                PointList smallList = bigListIt.next();
                mLegend.addSymbolExplanation("frames", Integer.toString(i), smallList.getName());
                Iterator<Point2DDouble> smallListIt = smallList.iterator();
                for (int j = 0; j < smallList.getSize(); j++) {
                    if (smallListIt.hasNext()) {
                        Point2DDouble point = smallListIt.next();
                        double xValue = calculateXValue(point.getX());
                        double yValue = calculateYValue(point.getY());
                        drawPoint(xValue, yValue, i);
                    }
                }
            }
        }

        drawGrid();
        plotLegend();

        return 0;

    }

}
