package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.LineChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.rendering.Legend;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Provides a plotting algorithm for line plot data.
 * @author Richard Schmidt
 */
public final class LinePlotter extends AbstractPointPlotter<LineChart> implements Plotter<LineChart> {

    /**
     * Plots a {@link LineChart} instance onto a {@link PlotCanvas}. Add new line styles in if statement.
     * @param diagram An instance of {@link LineChart} representing the line plot.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas} or
     * if there are more data series than frames or line styles.
     * @throws NoSuchElementException If the data is corrupt.
     */
    @Override
    public double plot(final LineChart diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException, NoSuchElementException {

        setCanvas(canvas);
        mCanvas.readConfig();
        setData();
        mDiagram = Objects.requireNonNull(diagram);
        mLegend = new Legend();
        mFrames = mCanvas.getFrames();
        mPageHeight = mCanvas.getPrintableHeight() + mCanvas.getMarginTop();
        mPageWidth = mCanvas.getPrintableWidth() + mCanvas.getMarginLeft();
        mResolution = mCanvas.getResolution();
        mStepSize = mCanvas.getDotDiameter() + 1;
        mGrid = mCanvas.getGrid();
        mDotFrame = mCanvas.getDotFrame();

        checkResolution();
        calculateRanges();
        drawAxes();
        mScaleX = scaleAxis("x");
        mScaleY = scaleAxis("y");
        mCanvas.setXScaleFactor((int) mScaleX[mScaleX.length - 1]);
        mCanvas.setYScaleFactor((int) mScaleY[mScaleY.length - 1]);
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
                smallList = smallList.sortXAscend();
                Iterator<Point2DDouble> pointIt = smallList.iterator();
                Point2DDouble currentPoint = pointIt.next();

                double currentX = currentPoint.getX();
                double currentY = currentPoint.getY();
                double nextX;
                double nextY;
                boolean done = false;

                while (!done) {

                    if (pointIt.hasNext()) {
                        Point2DDouble nextPoint = pointIt.next();
                        nextX = nextPoint.getX();
                        nextY = nextPoint.getY();

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

        if (mGrid) {
            drawGrid();
        }
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
        if (Math.abs(slope) == 0) {
            steps = mStepSize;
        } else if (Math.abs(slope) <= 1) {
            steps = mStepSize * SLOPESCALE;
        } else if (Math.abs(slope) <= 2) {
            steps = mStepSize / 2;
        } else if (Math.abs(slope) <= SLOPE3) {
            steps = mStepSize / SLOPE3;
        } else if (Math.abs(slope) <= SLOPE4) {
            steps = mStepSize / SLOPE4;
        } else if (Math.abs(slope) <= SLOPE5) {
            steps = mStepSize / SLOPE5;
        } else if (Math.abs(slope) <= SLOPE6) {
            steps = mStepSize / SLOPE6;
        } else if (Math.abs(slope) <= SLOPE7) {
            steps = mStepSize / SLOPE7;
        } else if (Math.abs(slope) <= SLOPE8) {
            steps = mStepSize / SLOPE8;
        } else if (Math.abs(slope) <= SLOPE9) {
            steps = mStepSize / SLOPE9;
        } else {
            steps = mStepSize / Math.abs(slope);
        }

        // new line styles are added here
        if (i == 0) {
            drawFullLine(currentX, nextX, steps, slope, n);
        } else if (i == 1) {
            drawDashedLine(currentX, nextX, steps, slope, n);
        } else if (i == 2) {
            drawDottedLine(currentX, nextX, steps, slope, n);
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
     * Draws a line consisting of dashes and spaces. The starting and end points are not included.
     * @param currentX Absolute x-coordinate of the starting point.
     * @param nextX Absolute x-coordinate of the end point.
     * @param steps Distance with which the x-coordinate is incremented.
     * @param slope Slope of the line.
     * @param n Y-intercept.
     */
    private void drawDashedLine(final double currentX, final double nextX, final double steps, final double slope, final double n) {
        for (double j = currentX + steps; j < nextX - DASHEDLINESCALE * steps; j += DASHEDLINESCALE * steps) {
            addPoint(j, j * slope + n);
            j += steps;
            addPoint(j, j * slope + n);
            j += steps;
            addPoint(j, j * slope + n);
            j += steps;
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
        double currentY = currentX * slope + n;
        double nextY = nextX * slope + n;

        for (double j = currentX + steps; j < nextX; j += DOTTEDLINESCALE * steps) {
            if (mFrames) {
                // avoid dots inside circle (dotted line is linked to circle frame)
                double diffXCurrent = Math.abs(currentX - j);
                double diffYCurrent = Math.abs(currentY - (j * slope + n));
                double diffXNext = Math.abs(nextX - j);
                double diffYNext = Math.abs(nextY - (j * slope + n));
                double distanceCurrent = Math.sqrt(diffXCurrent * diffXCurrent + diffYCurrent * diffYCurrent);
                double distanceNext = Math.sqrt(diffXNext * diffXNext + diffYNext * diffYNext);
                if ((distanceCurrent <= CIRCLEDIA / 2) || (distanceNext <= CIRCLEDIA / 2)) {
                    continue;
                }
            }
            addPoint(j, j * slope + n);
        }
    }

}
