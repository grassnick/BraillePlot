package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.Iterator;
import java.util.Objects;

/**
 * ScatterPlotter. Provides a plotting algorithm for scatter plot data. Extends Plotter.
 * @author Richard Schmidt
 */
public final class ScatterPlotter extends AbstractPlotter implements Plotter<ScatterPlot> {

    ScatterPlot mDiagram;
    private static final double CIRCLEDIA = 10;

    /**
     * Constructor. Create a new plotter for instances of {@link ScatterPlot}.
     */
    public ScatterPlotter() {
        super();
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
        mStepSize = mCanvas.getDotDiameter();
        mPageWidth = mCanvas.getPrintableWidth();
        mPageHeight = mCanvas.getPrintableHeight();

        calculateRanges(mDiagram);
        drawAxes();
        scaleX = scaleAxes(xRange, mNumberXTics, mDiagram.getMinX());
        scaleY = scaleAxes(yRange, mNumberYTics, mDiagram.getMinY());
        drawGrid();

        PointListContainer<PointList> bigList = mDiagram.getDataSet();
        Iterator<PointList> bigListIt = bigList.iterator();

        for (int i = 0; i < bigList.getSize(); i++) {
            if (bigListIt.hasNext()) {
                PointList smallList = bigListIt.next();
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

    }

    double calculateXValue(final double x) {
        double ratio = xTickStep / (scaleX[1] - scaleX[0]);
        return (x - scaleX[0]) * ratio + leftMargin + xTickStep;
    }

    double calculateYValue(final double y) {
        double ratio = yTickStep / (scaleY[1] - scaleY[0]);
        return bottomMargin - yTickStep - (y - scaleY[0]) * ratio;
    }

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

    void drawCircle(final double xValue, final double yValue) {

    }

    void drawX(final double xValue, final double yValue) {

    }

    void drawCross(final double xValue, final double yValue) {

    }
}
