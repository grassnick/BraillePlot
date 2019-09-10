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
public final class LinePlotter extends AbstractPlotter<LinePlot> implements Plotter<LinePlot> {

    /**
     * Plots a {@link LinePlot} instance onto a {@link PlotCanvas}.
     * @param diagram An instance of {@link LinePlot} representing the line plot.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas}, this is
     * to display the given diagram.
     */
    @Override
    public void plot(final LinePlot diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException {

        mDiagram = Objects.requireNonNull(diagram);
        mCanvas = Objects.requireNonNull(canvas);
        mData = mCanvas.getCurrentPage();
        mCanvas.readConfig();
        mResolution = mCanvas.getResolution();
        mStepSize = mCanvas.getDotDiameter();
        mPageWidth = mCanvas.getPrintableWidth();
        mPageHeight = mCanvas.getPrintableHeight();

        calculateRanges();
        drawAxes();
        mScaleX = scaleAxis("x");
        mScaleY = scaleAxis("y");
        drawGrid();

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

                        if (i == 0) {
                            for (double j = calculateXValue(currentX) + steps; j < calculateXValue(nextX); j += steps) {
                                addPoint(j, j * slope + n);
                            }
                        } else if (i == 1) {
                            for (double j = calculateXValue(currentX) + steps; j < calculateXValue(nextX); j += 4 * steps) {
                                addPoint(j, j * slope + n);
                            }
                        } else if (i == 2) {
                            for (double j = calculateXValue(currentX) + steps; j < calculateXValue(nextX) - 3 * steps; j += 3 * steps) {
                                addPoint(j, j * slope + n);
                                j += steps;
                                addPoint(j, j * slope + n);
                                j += steps;
                                addPoint(j, j * slope + n);
                                j += steps;
                                addPoint(j, j * slope + n);
                            }
                        }

                        currentX = nextX;
                        currentY = nextY;
                    } else {
                        done = true;
                    }
                }

            }
        }

    }
}
