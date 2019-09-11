package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.diagrams.BarChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.Iterator;
import java.util.Objects;

/**
 * Provides a plotting algorithm for bar chart data.
 * @author Richard Schmidt
 */
public final class BarChartPlotter extends AbstractPlotter<BarChart> implements Plotter<BarChart> {

    private String[] mNamesY;
    private double minWidth;
    private double maxWidth;
    private double minDist;
    private double barWidth;
    private double barDist;
    private double lastXValue;

    /**
     * Plots a {@link BarChart} instance onto a {@link PlotCanvas}.
     * @param diagram An instance of {@link  BarChart} representing the bar chart.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas}, this is
     * to display the given diagram.
     */
    @Override
    public void plot(final BarChart diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException {

        mDiagram = Objects.requireNonNull(diagram);
        CategoricalPointListContainer<PointList> catList = (CategoricalPointListContainer<PointList>) mDiagram.getDataSet();
        mCanvas = Objects.requireNonNull(canvas);
        mData = mCanvas.getCurrentPage();
        mCanvas.readConfig();
        mResolution = mCanvas.getResolution();
        mStepSize = mCanvas.getDotDiameter();
        mPageWidth = mCanvas.getPrintableWidth();
        mPageHeight = mCanvas.getPrintableHeight();

        calculateRanges();
        drawAxes();
        mScaleX = scaleAxis("z");
        mNamesY = new String[catList.getSize()];

        Iterator<PointList> catListIt = catList.iterator();
        for (int i = 0; i < mNamesY.length; i++) {
            if (catListIt.hasNext()) {
                mNamesY[i] = catListIt.next().getName();
            }
        }

        // TODO make this configurable by the user
        int numBar = catList.getSize();
        minWidth = TWENTY; // minimum width of a bar
        maxWidth = FIFTY; // maximum width of a bar
        minDist = TEN; // minimum distance between two bars

        barWidth = (lengthY - (numBar + 1) * minDist) / numBar;
        if (barWidth < minWidth) {
            barWidth = minWidth;
        } else if (barWidth > maxWidth) {
            barWidth = maxWidth;
        }

        barDist = (lengthY - numBar * barWidth) / (numBar + 1);

        Iterator<PointList> bigListIt = catList.iterator();
        for (int i = 0; i < catList.getSize(); i++) {
            if (bigListIt.hasNext()) {
                PointList smallList = bigListIt.next();
                Iterator<Point2DDouble> smallListIt = smallList.iterator();
                lastXValue = 0;
                for (int j = 0; j < smallList.getSize(); j++) {
                    if (smallListIt.hasNext()) {
                        Point2DDouble point = smallListIt.next();
                        double xValue = point.getY();
                        drawRectangle(i, j, xValue);
                    }
                }
            }
        }

    }

    @Override
    void calculateRanges() {
        mXRange = Math.abs(mDiagram.getCumulatedMaxY() - mDiagram.getMinY());
    }

    @Override
    void drawAxes() {
        // margin left of y-axis
        mLeftMargin = WMULT * mCanvas.getCellWidth() + WMULT * mCanvas.getCellDistHor();
        // margin from bottom to x-axis
        mBottomMargin = mPageHeight - (HMULT * mCanvas.getCellHeight() + HMULT * mCanvas.getCellDistVer());
        // margin from top for title
        mTitleMargin = TMULT * mCanvas.getCellHeight() + TMULT * mCanvas.getCellDistVer();

        // x-axis
        double lastValueX = mLeftMargin;
        for (double i = mLeftMargin; i <= mPageWidth; i += mStepSize) {
            addPoint(i, mBottomMargin);
            lastValueX = i;
        }
        lengthX = lastValueX - mLeftMargin;
        mNumberXTics = (int) Math.floor(lengthX / TICKDISTANCE);
        if (mNumberXTics < 2) {
            mNumberXTics = 2;
        } else if (mNumberXTics <= FIVE) {
            mNumberXTics = FIVE;
        } else {
            mNumberXTics = TEN;
        }

        mScaleX = new int[mNumberXTics + 1];

        // arrows on x-axis
        addPoint(lastValueX - ARROWS1, mBottomMargin + ARROWS1);
        addPoint(lastValueX - ARROWS2, mBottomMargin + ARROWS2);
        addPoint(lastValueX - ARROWS3, mBottomMargin + ARROWS3);
        addPoint(lastValueX - ARROWS1, mBottomMargin - ARROWS1);
        addPoint(lastValueX - ARROWS2, mBottomMargin - ARROWS2);
        addPoint(lastValueX - ARROWS3, mBottomMargin - ARROWS3);

        // tick marks on x-axis
        mXTickStep = (lastValueX - MARGIN - mLeftMargin) / mNumberXTics;
        for (int i = 1; i <= mNumberXTics; i++) {
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin + TICK1);
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin + TICK2);
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin + TICK3);
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin - TICK1);
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin - TICK2);
            addPoint(mLeftMargin + i * mXTickStep, mBottomMargin - TICK3);
        }

        // y-axis:
        double lastValueY = mBottomMargin;
        for (double i = mBottomMargin; i > mTitleMargin; i -= mStepSize) {
            addPoint(mLeftMargin, i);
            lastValueY = i;
        }
        lengthY = mBottomMargin - lastValueY;

        // arrows on y-axis
        addPoint(mLeftMargin - ARROWS1, lastValueY + ARROWS1);
        addPoint(mLeftMargin - ARROWS2, lastValueY + ARROWS2);
        addPoint(mLeftMargin - ARROWS3, lastValueY + ARROWS3);
        addPoint(mLeftMargin + ARROWS1, lastValueY + ARROWS1);
        addPoint(mLeftMargin + ARROWS2, lastValueY + ARROWS2);
        addPoint(mLeftMargin + ARROWS3, lastValueY + ARROWS3);

    }

    /**
     * Prepares rectangle drawing.
     * @param i Corresponds to the position on the y-axis.
     * @param j Corresponds to the category and the filling.
     * @param xValue Value on the x-axis.
     */
    private void drawRectangle(final int i, final int j, final double xValue) {
        double startY = mBottomMargin - (barDist + i * (barWidth + barDist));
        double endX = calculateXValue(xValue) + lastXValue;
        lastXValue = endX;
        plotAndFillRectangle(startY, endX, j);

    }

    /**
     * Plots the rectangle and chooses a texture.
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     * @param j Corresponds to the category and the texture.
     */
    private void plotAndFillRectangle(final double startY, final double endX, final int j) {
        for (double i = mLeftMargin + mStepSize; i <= endX; i += mStepSize) {
            addPoint(i, startY);
        }
        for (double i = startY - mStepSize; i >= startY - barWidth; i -= mStepSize) {
            addPoint(endX, i);
        }
        for (double i = mLeftMargin + mStepSize; i < endX; i += mStepSize) {
            addPoint(i, startY - barWidth);
        }

        if (j == 0) {
            fillType1(startY, endX);
        } else if (j == 1) {
            fillType2(startY, endX);
        } else if (j == 2) {
            fillType3(startY, endX);
        } else if (j == THREE) {
            fillType4(startY, endX);
        } else if (j == FOUR) {
            fillType5(startY, endX);
        }
    }

    /**
     * Fills a rectangle with the texture .
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillType1(final double startY, final double endX) {

    }

    /**
     * Fills a rectangle with the texture .
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillType2(final double startY, final double endX) {

    }

    /**
     * Fills a rectangle with the texture .
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillType3(final double startY, final double endX) {

    }

    /**
     * Fills a rectangle with the texture .
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillType4(final double startY, final double endX) {

    }

    /**
     * Fills a rectangle with the texture .
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillType5(final double startY, final double endX) {

    }

}
