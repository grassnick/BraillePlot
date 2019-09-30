package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.LinePlot;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.rendering.Legend;
import tec.units.ri.unit.MetricPrefix;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import static tec.units.ri.unit.Units.METRE;

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
     * @throws NoSuchElementException If the data is corrupt.
     */
    @Override
    public double plot(final LinePlot diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException, NoSuchElementException {

        setCanvas(canvas);
        mCanvas.readConfig();
        setData();
        mDiagram = Objects.requireNonNull(diagram);
        mLegend = new Legend();
        mFrames = mCanvas.getFrames();
        mPageHeight = mCanvas.getPrintableHeight();
        mPageWidth = mCanvas.getPrintableWidth();
        mResolution = mCanvas.getResolution();
        mStepSize = mCanvas.getDotDiameter() + 1;

        checkResolution();
        calculateRanges();
        drawAxes();
        mScaleX = scaleAxis("x");
        mScaleY = scaleAxis("y");
        mCanvas.setXScaleFactor(mScaleX[mScaleX.length - 1]);
        mCanvas.setYScaleFactor(mScaleY[mScaleY.length - 1]);
        if (mFrames) {
            mLegend.setType(2);
        } else {
            mLegend.setType(1);
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
                if (mFrames) {
                    mLegend.addSymbolExplanation("frames", Integer.toString(i), smallList.getName());
                }
                mLegend.addSymbolExplanation("lines", Integer.toString(i), smallList.getName());
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
                double currentY = smallList.getFirstXOccurence(currentX).getY();
                smallList.removeFirstOccurrence(new Point2DDouble(currentX, currentY));
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

                        nextY = smallList.getFirstXOccurence(nextX).getY();

                        //drawing
                        drawLines(calculateXValue(currentX), calculateXValue(nextX), calculateYValue(currentY), calculateYValue(nextY), i);

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

    /**
     * Calculates line parameters and chooses the line style according to i.
     * @param currentX Absolue x-coordinate of the starting point.
     * @param nextX Absolute x-coordinate of the end point.
     * @param currentY Absolute y-coordinate of the starting point.
     * @param nextY Absolute y-coordinate of the end point.
     * @param i Corresponds to the line style.
     * @throws InsufficientRenderingAreaException If there are more data series than line styles.
     */
    void drawLines(final double currentX, final double nextX, final double currentY, final double nextY, final int i) throws InsufficientRenderingAreaException {

        double slope = (nextY - currentY) / (nextX - currentX);
        double n = currentY - currentX * slope;
        double steps;
        if (Math.abs(slope) <= 1) {
            steps = mStepSize;
        } else if (Math.abs(slope) <= 2) {
            steps = mStepSize / 2;
        } else if (Math.abs(slope) <= THREE) {
            steps = mStepSize / THREE;
        } else if (Math.abs(slope) <= FOUR) {
            steps = mStepSize / FOUR;
        } else if (Math.abs(slope) <= FIVE) {
            steps = mStepSize / FIVE;
        } else if (Math.abs(slope) <= SIX) {
            steps = mStepSize / SIX;
        } else if (Math.abs(slope) <= SEVEN) {
            steps = mStepSize / SEVEN;
        } else if (Math.abs(slope) <= EIGHT) {
            steps = mStepSize / EIGHT;
        } else if (Math.abs(slope) <= NINE) {
            steps = mStepSize / NINE;
        } else {
            steps = mStepSize / TEN;
        }

        // new line styles are added here
        if (i == 0) {
            drawFullLine(currentX, nextX, steps, slope, n);
        } else if (i == 1) {
            drawDottedLine(currentX, nextX, steps, slope, n);
        } else if (i == 2) {
            drawDashedLine(currentX, nextX, steps, slope, n);
        } else {
            throw new InsufficientRenderingAreaException("There are more data series than line types.");
        }

    }

    /**
     * Draws a full line. The starting and end points are not included.
     * @param currentX Absolute x-coordinate of the starting point.
     * @param nextX Absolute x-coordinate of the end point.
     * @param steps Distance with which the x-coordinate is incremented.
     * @param slope Slope of the line.
     * @param n Y-intercept.
     */
    private void drawFullLine(final double currentX, final double nextX, final double steps, final double slope, final double n) {
        for (double j = currentX + steps; j < nextX; j += steps) {
            addPoint(j, j * slope + n);
        }
    }

    /**
     * Draws a dotted line. The starting and end points are not included.
     * @param currentX Absolute x-coordinate of the starting point.
     * @param nextX Absolute x-coordinate of the end point.
     * @param steps Distance with which the x-coordinate is incremented.
     * @param slope Slope of the line.
     * @param n Y-intercept.
     */
    private void drawDottedLine(final double currentX, final double nextX, final double steps, final double slope, final double n) {
        for (double j = currentX + steps; j < nextX; j += FOUR * steps) {
            addPoint(j, j * slope + n);
        }
    }

    /**
     * Draws a line consisting of single dashes and spaces. The starting and end points are not included.
     * @param currentX Absolute x-coordinate of the starting point.
     * @param nextX Absolute x-coordinate of the end point.
     * @param steps Distance with which the x-coordinate is incremented.
     * @param slope Slope of the line.
     * @param n Y-intercept.
     */
    private void drawDashedLine(final double currentX, final double nextX, final double steps, final double slope, final double n) {
        for (double j = currentX + steps; j < nextX - THREE * steps; j += THREE * steps) {
            addPoint(j, j * slope + n);
            j += steps;
            addPoint(j, j * slope + n);
            j += steps;
            addPoint(j, j * slope + n);
            j += steps;
            addPoint(j, j * slope + n);
        }
    }

}
