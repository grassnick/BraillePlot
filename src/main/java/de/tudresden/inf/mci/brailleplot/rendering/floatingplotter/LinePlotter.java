package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.LinePlot;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.Iterator;
import java.util.Objects;

/**
 * Provides a plotting algorithm for line plot data.
 * @author Richard Schmidt
 */
public final class LinePlotter extends AbstractPointPlotter<LinePlot> implements Plotter<LinePlot> {

    /**
     * Plots a {@link LinePlot} instance onto a {@link PlotCanvas}. Add new line styles in if statement.
     * @param diagram An instance of {@link LinePlot} representing the line plot.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas} or
     * if there are more data series than frames or line styles.
     */
    @Override
    public double plot(final LinePlot diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException {

        mCanvas = Objects.requireNonNull(canvas);
        mCanvas.readConfig();
        mData = mCanvas.getCurrentPage();
        mDiagram = Objects.requireNonNull(diagram);
        mFrames = mCanvas.getFrames();
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
        if (mFrames) {
            //mCanvas.setType(2);
        } else {
            //mCanvas.setType(1);
        }
        nameXAxis();
        nameYAxis();
        nameTitle();

        // draw points, frames and lines
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

                // draw lines
                double currentX = smallList.getMinX();
                double currentY = smallList.getCorrespondingYValue(currentX);
                double nextX;
                double nextY;
                boolean done = false;

                while (!done) {

                    smallList.removeFirstOccurrence(new Point2DDouble(currentX, currentY));

                    // find next point
                    Iterator<Point2DDouble> pointIt = smallList.iterator();
                    if (pointIt.hasNext()) {
                        Point2DDouble point = pointIt.next();
                        nextX = point.getX();


                        for (int j = 0; j < smallList.getSize(); j++) {
                            if (pointIt.hasNext()) {
                                point = pointIt.next();
                                if (point.getX() < nextX) {
                                    nextX = point.getX();
                                }
                            }
                        }

                        // drawing
                        nextY = smallList.getCorrespondingYValue(nextX);
                        double slope = (calculateYValue(nextY) - calculateYValue(currentY)) / (calculateXValue(nextX) - calculateXValue(currentX));
                        double n = calculateYValue(currentY) - calculateXValue(currentX) * slope;
                        double steps;
                        if (Math.abs(slope) <= 1) {
                            steps = mStepSize;
                        } else if (Math.abs(slope) <= 2) {
                            steps = mStepSize / 2;
                        } else if (Math.abs(slope) <= THREE) {
                            steps = mStepSize / THREE;
                        } else if (Math.abs(slope) <= FOUR) {
                            steps = mStepSize / FOUR;
                        } else {
                            steps = mStepSize / FIVE;
                        }

                        // new line styles are added here
                        if (i == 0) {
                            for (double j = calculateXValue(currentX) + steps; j < calculateXValue(nextX); j += steps) {
                                addPoint(j, j * slope + n);
                            }
                        } else if (i == 1) {
                            for (double j = calculateXValue(currentX) + steps; j < calculateXValue(nextX); j += FOUR * steps) {
                                addPoint(j, j * slope + n);
                            }
                        } else if (i == 2) {
                            for (double j = calculateXValue(currentX) + steps; j < calculateXValue(nextX) - THREE * steps; j += THREE * steps) {
                                addPoint(j, j * slope + n);
                                j += steps;
                                addPoint(j, j * slope + n);
                                j += steps;
                                addPoint(j, j * slope + n);
                                j += steps;
                                addPoint(j, j * slope + n);
                            }
                        } else {
                            throw new InsufficientRenderingAreaException("There are more data series than line types.");
                        }

                        currentX = nextX;
                        currentY = nextY;
                    } else {
                        done = true;
                    }
                }

            }
        }

        drawGrid();
        plotLegend();

        return 0;

    }
}
