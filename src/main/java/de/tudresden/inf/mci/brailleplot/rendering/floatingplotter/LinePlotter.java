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
 * LinePlotter. Provides a plotting algorithm for line plot data. Extends Plotter.
 * @author Richard Schmidt
 */
public final class LinePlotter extends AbstractPlotter implements Plotter<LinePlot> {

    private LinePlot mDiagram;
    private static final double CIRCLESCALE = 1.3;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int TEN = 10;
    private static final double CIRCLEDIA = 15;

    /**
     * Constructor. Create a new plotter for instances of {@link LinePlot}.
     */
    public LinePlotter() {
        super();
    }

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

        calculateRanges(mDiagram);
        drawAxes();
        scaleX = scaleAxis(xRange, mNumberXTics, mDiagram.getMinX());
        scaleY = scaleAxis(yRange, mNumberYTics, mDiagram.getMinY());
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
                        int k = 1;
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

                        for (double j = calculateXValue(currentX) + steps; j < calculateXValue(nextX); j += steps) {
                            addPoint(j, k * slope * steps  + calculateYValue(currentY));
                            k++;
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

    /**
     * Calculates the absolute x-value on the paper.
     * @param x Value as in data.
     * @return double
     */
    private double calculateXValue(final double x) {
        double ratio = xTickStep / (scaleX[1] - scaleX[0]);
        return (x / Math.pow(TEN, scaleX[scaleX.length - 1]) - scaleX[0]) * ratio + leftMargin + xTickStep;
    }

    /**
     * Calculates the absolute y-value on the paper.
     * @param y Value as in data.
     * @return double
     */
    private double calculateYValue(final double y) {
        double ratio = yTickStep / (scaleY[1] - scaleY[0]);
        return bottomMargin - yTickStep - (y / Math.pow(TEN, scaleY[scaleY.length - 1]) - scaleY[0]) * ratio;
    }

    /**
     * Adds a point by its x- and y-value to the floating point data. Chooses a corresponding frame.
     * @param xValue Must be the absolute x-value on the paper.
     * @param yValue Must be the absolute y-value on the paper.
     * @param i Links to the data series, thus choosing one frame per data series.
     */
    private void drawPoint(final double xValue, final double yValue, final int i) {
        addPoint(xValue, yValue);
        if (i == 0) {
            drawCircle(xValue, yValue);
        } else if (i == 1) {
            drawX(xValue, yValue);
        } else if (i == 2) {
            drawCross(xValue, yValue);
        }
    }

    /**
     * Draws a circle frame with xValue and yValue as center.
     * @param xValue
     * @param yValue
     */
    private void drawCircle(final double xValue, final double yValue) {
        double lastX = 0;

        for (double x = xValue - CIRCLEDIA / 2; x <= xValue + CIRCLEDIA / 2; x += mStepSize) {
            double y1 = yValue + Math.sqrt(Math.pow(CIRCLEDIA / 2, 2) - Math.pow(x - xValue, 2));
            double y2 = yValue - Math.sqrt(Math.pow(CIRCLEDIA / 2, 2) - Math.pow(x - xValue, 2));
            addPoint(x, y1);
            addPoint(x, y2);
            lastX = x;
        }

        addPoint(lastX - mStepSize / THREE, yValue + CIRCLESCALE * mStepSize);
        addPoint(lastX - mStepSize / THREE, yValue - CIRCLESCALE * mStepSize);
        addPoint(xValue - CIRCLEDIA / 2 + mStepSize / THREE, yValue + CIRCLESCALE * mStepSize);
        addPoint(xValue - CIRCLEDIA / 2 + mStepSize / THREE, yValue - CIRCLESCALE * mStepSize);
    }

    /**
     * Draws an X with xValue and yValue as center.
     * @param xValue
     * @param yValue
     */
    private void drawX(final double xValue, final double yValue) {
        addPoint(xValue + mStepSize, yValue + mStepSize);
        addPoint(xValue + 2 * mStepSize, yValue + 2 * mStepSize);
        addPoint(xValue + THREE * mStepSize, yValue + THREE * mStepSize);

        addPoint(xValue - mStepSize, yValue - mStepSize);
        addPoint(xValue - 2 * mStepSize, yValue - 2 * mStepSize);
        addPoint(xValue - THREE * mStepSize, yValue - THREE * mStepSize);

        addPoint(xValue + mStepSize, yValue - mStepSize);
        addPoint(xValue + 2 * mStepSize, yValue - 2 * mStepSize);
        addPoint(xValue + THREE * mStepSize, yValue - THREE * mStepSize);

        addPoint(xValue - mStepSize, yValue + mStepSize);
        addPoint(xValue - 2 * mStepSize, yValue + 2 * mStepSize);
        addPoint(xValue - THREE * mStepSize, yValue + THREE * mStepSize);
    }

    /**
     * Draws a cross with xValue and yValue as center.
     * @param xValue
     * @param yValue
     */
    private void drawCross(final double xValue, final double yValue) {
        addPoint(xValue, yValue + mStepSize);
        addPoint(xValue, yValue + 2 * mStepSize);
        addPoint(xValue, yValue + THREE * mStepSize);

        addPoint(xValue, yValue - mStepSize);
        addPoint(xValue, yValue - 2 * mStepSize);
        addPoint(xValue, yValue - THREE * mStepSize);

        addPoint(xValue + mStepSize, yValue);
        addPoint(xValue + 2 * mStepSize, yValue);
        addPoint(xValue + THREE * mStepSize, yValue);

        addPoint(xValue - mStepSize, yValue);
        addPoint(xValue - 2 * mStepSize, yValue);
        addPoint(xValue - THREE * mStepSize, yValue);
    }
}
