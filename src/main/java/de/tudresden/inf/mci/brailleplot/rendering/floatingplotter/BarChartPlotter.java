package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.diagrams.BarChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Iterator;
import java.util.Objects;

import static tec.units.ri.unit.Units.METRE;

/**
 * Provides a plotting algorithm for bar chart data.
 * @author Richard Schmidt
 */
public final class BarChartPlotter extends AbstractPlotter<BarChart> implements Plotter<BarChart> {

    private String[] mNamesY;
    private double[] mGridHelp;
    private int mNumBar;
    private double mBarWidth;
    private double mBarDist;
    private double mLastXValue;
    private static final double STAIRDIST = 6;

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
        double mMinWidth = mCanvas.getMinBarWidth();
        double mMaxWidth = mCanvas.getMaxBarWidth();
        double mMinDist = mCanvas.getMinBarDist();

        if (mStepSize < mResolution) {
            mStepSize = mResolution;
        }

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

        // bar drawing and filling
        mNumBar = catList.getSize();
        mGridHelp = new double[mNumBar];
        mBarWidth = (lengthY - (mNumBar + 1) * mMinDist) / mNumBar;
        if (mBarWidth < mMinWidth) {
            mBarWidth = mMinWidth;
        } else if (mBarWidth > mMaxWidth) {
            mBarWidth = mMaxWidth;
        }

        mBarDist = (lengthY - mNumBar * mBarWidth) / (mNumBar + 1);

        Iterator<PointList> bigListIt = catList.iterator();
        for (int i = 0; i < catList.getSize(); i++) {
            if (bigListIt.hasNext()) {
                PointList smallList = bigListIt.next();
                Iterator<Point2DDouble> smallListIt = smallList.iterator();
                for (int j = 0; j < smallList.getSize(); j++) {
                    if (smallListIt.hasNext()) {
                        Point2DDouble point = smallListIt.next();
                        double xValue = point.getY();
                        drawRectangle(i, j, xValue);
                    }
                }
            }
        }

        drawGrid();

    }

    /**
     * Calculates mXRange with cumulated maximum value.
     */
    @Override
    void calculateRanges() {
        mXRange = Math.abs(mDiagram.getCumulatedMaxY() - mDiagram.getMinY());
    }

    /**
     * Draws axes without tick marks on the y-axis.
     */
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
        for (double i = 1; i <= 2 * mNumberXTics; i++) {
            if (i % 2 == 0) {
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK2);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK3);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK4);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK2);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK3);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK4);
            } else {
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK2);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin - TICK2);
            }
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
        double startY = mBottomMargin - (mBarDist + i * (mBarWidth + mBarDist));
        double endX;
        if (j == 0) {
            endX = calculateXValue(xValue);
        } else {
            endX = calculateXValue(xValue) + mLastXValue - mLeftMargin;
        }
        plotAndFillRectangle(startY, endX, j);
        mGridHelp[i] = endX;
        mLastXValue = endX;
    }

    /**
     * Plots the rectangle and chooses a texture.
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     * @param j Corresponds to the category and the texture.
     */
    private void plotAndFillRectangle(final double startY, final double endX, final int j) {
        // plot rectangle
        for (double i = mLeftMargin + mStepSize; i <= endX; i += mStepSize) {
            addPoint(i, startY);
        }
        for (double i = startY - mStepSize; i >= startY - mBarWidth; i -= mStepSize) {
            addPoint(endX, i);
        }
        for (double i = mLeftMargin + mStepSize; i < endX; i += mStepSize) {
            addPoint(i, startY - mBarWidth);
        }

        // choose texture
        if (j == 0) {
            fillFullPattern(startY, endX);
        } else if (j == 1) {
            fillVerticalLine(startY, endX);
        } else if (j == 2) {
            fillDiagonalRight(startY, endX);
        } else if (j == THREE) {
            fillGridPattern(startY, endX);
        } else if (j == FOUR) {
            fillDottedPattern(startY, endX);
        } else if (j == FIVE) {
            fillStairPattern(startY, endX);
        } else if (j == SIX) {
            fillDiagonalLeft(startY, endX);
        }
    }

    /**
     * Fills a rectangle with the texture full_pattern.
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillFullPattern(final double startY, final double endX) {
        for (double i = mLeftMargin + mStepSize; i < endX - mStepSize / 2; i += mStepSize) {
            for (double j = startY - mStepSize; j > startY - mBarWidth; j -= mStepSize) {
                addPoint(i, j);
            }
        }
    }

    /**
     * Fills a rectangle with the texture vertical_line.
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillVerticalLine(final double startY, final double endX) {
        for (double i = mLastXValue + FIVE * mStepSize; i < endX - mStepSize; i += FIVE * mStepSize) {
            for (double j = startY - THREE * mStepSize; j > startY - mBarWidth + THREE * mStepSize; j -= mStepSize) {
                addPoint(i, j);
            }
        }
    }

    /**
     * Fills a rectangle with the texture grid_pattern.
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillGridPattern(final double startY, final double endX) {
        for (double i = mLastXValue + FIVE * mStepSize; i < endX - 2 * mStepSize; i += FIVE * mStepSize) {
             for (double j = startY - mStepSize; j > startY - mBarWidth; j -= mStepSize) {
                addPoint(i, j);
            }
        }

        for (double j = startY - FIVE * mStepSize; j > startY - mBarWidth + mStepSize; j -= FIVE * mStepSize) {
            for (double i = mLastXValue + mStepSize; i < endX; i += mStepSize) {
                addPoint(i, j);
            }
        }
    }

    /**
     * Fills a rectangle with the texture dotted_pattern.
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillDottedPattern(final double startY, final double endX) {
        for (double i = mLastXValue + FOUR * mStepSize; i < endX - mStepSize; i += FOUR * mStepSize) {
            for (double j = startY - FOUR * mStepSize; j > startY - mBarWidth + mStepSize; j -= FOUR * mStepSize) {
                addPoint(i, j);
            }
        }
    }

    /**
     * Fills a rectangle with the texture stair_pattern.
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillStairPattern(final double startY, final double endX) {
        double last = 0;
        outerloop:
        for (double j = startY - STAIRDIST; j > startY - mBarWidth; j -= 2 * STAIRDIST) {
            double lastX;
            double lastY;
            for (double i = mLastXValue + mStepSize; i <= mLastXValue + FOUR * mStepSize; i += mStepSize) {
                if (i < endX) {
                    addPoint(i, j);
                    last = i;
                } else {
                    continue outerloop;
                }
            }
            lastX = last;

            for (double k = j - mStepSize; k >= j - FOUR * mStepSize; k -= mStepSize) {
                if (k > startY - mBarWidth) {
                    addPoint(lastX, k);
                    last = k;
                } else {
                    continue outerloop;
                }
            }

            lastY = last;

            while (true) {
                for (double i = lastX + mStepSize; i <= lastX + FOUR * mStepSize; i += mStepSize) {
                    if (i < endX) {
                        addPoint(i, lastY);
                        last = i;
                    } else {
                        continue outerloop;
                    }
                }

                lastX = last;

                for (double k = lastY - mStepSize; k >= lastY - FOUR * mStepSize; k -= mStepSize) {
                    if (k > startY - mBarWidth) {
                        addPoint(lastX, k);
                        last = k;
                    } else {
                        continue outerloop;
                    }
                }

                lastY = last;
            }

        }

        anotherloop:
        for (double i = mLastXValue + 2 * STAIRDIST; i < endX; i += 2 * STAIRDIST) {
            double lastX;
            double lastY;
            for (double j = startY - mStepSize; j >= startY - FOUR * mStepSize; j -= mStepSize) {
                if (j > startY - mBarWidth) {
                    addPoint(i, j);
                    last = j;
                } else {
                    continue anotherloop;
                }
            }

            lastY = last;

            for (double k = i + mStepSize; k <= i + FOUR * mStepSize; k += mStepSize) {
                if (k < endX) {
                    addPoint(k, lastY);
                    last = k;
                } else {
                    continue anotherloop;
                }
            }

            lastX = last;

            while (true) {
                for (double j = lastY - mStepSize; j >= lastY - FOUR * mStepSize; j -= mStepSize) {
                    if (j > startY - mBarWidth) {
                        addPoint(lastX, j);
                        last = j;
                    } else {
                        continue anotherloop;
                    }
                }
                lastY = last;

                for (double k = lastX + mStepSize; k <= lastX + FOUR * mStepSize; k += mStepSize) {
                    if (k < endX) {
                        addPoint(k, lastY);
                        last = k;
                    } else {
                        continue anotherloop;
                    }
                }

                lastX = last;
            }
        }
    }

    /**
     * Fills a rectangle with the texture diagonal_left.
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillDiagonalLeft(final double startY, final double endX) {
        for (double j = startY - mStepSize; j > startY - mBarWidth; j -= SIX * mStepSize) {
            double y = j;
            for (double i = mLastXValue + mStepSize; y > startY - mBarWidth && i < endX; i += mStepSize) {
                addPoint(i, y);
                y -= mStepSize;
            }
        }

        for (double i = mLastXValue + SEVEN * mStepSize; i < endX; i += SIX * mStepSize) {
            double y = startY - mStepSize;
            for (double k = i; y > startY - mBarWidth && k < endX; k += mStepSize) {
                addPoint(k, y);
                y -= mStepSize;
            }
        }
    }

    /**
     * Fills a rectangle with the texture diagonal_right.
     * @param startY Starting y-coordinate.
     * @param endX Starting y-coordinate.
     */
    private void fillDiagonalRight(final double startY, final double endX) {
        for (double j = startY - mBarWidth + mStepSize; j < startY; j += SIX * mStepSize) {
            double y = j;
            for (double i = mLastXValue + mStepSize; y < startY && i < endX; i += mStepSize) {
                addPoint(i, y);
                y += mStepSize;
            }
        }

        for (double i = mLastXValue + SEVEN * mStepSize; i < endX; i += SIX * mStepSize) {
            double y = startY - mBarWidth + mStepSize;
            for (double k = i; y < startY && k < endX; k += mStepSize) {
                addPoint(k, y);
                y += mStepSize;
            }
        }
    }

    /**
     * Only draws grid on x-axis.
     */
    @Override
    void drawGrid() {
        FloatingPointData<Boolean> grid = mCanvas.getNewPage();

        // x-axis
        for (double i = 1; i <= 2 * mNumberXTics; i++) {
            loop:
            for (double j = mBottomMargin; j > mTitleMargin; j -= mStepSize) {
                for (int k = 0; k < mNumBar; k++) {
                    if (j < mBottomMargin - k * (mBarDist + mBarWidth) && j > mBottomMargin - mBarDist - k * (mBarDist + mBarWidth)) {
                        Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                        if (!mData.checkPoint(point)) {
                            grid.addPoint(point);
                        }
                        continue loop;
                    }

                    if (j < mBottomMargin - mNumBar * (mBarDist + mBarWidth) && j > mTitleMargin) {
                        Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                        if (!mData.checkPoint(point)) {
                            grid.addPoint(point);
                        }
                        continue loop;
                    }

                    if ((mLeftMargin + (i / 2) * mXTickStep) > mGridHelp[k]) {
                        Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(mLeftMargin + (i / 2) * mXTickStep, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(j, MetricPrefix.MILLI(METRE)), true);
                        if (!mData.checkPoint(point)) {
                            grid.addPoint(point);
                        }
                        continue loop;
                    }
                }
            }
        }

    }

}
