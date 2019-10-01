package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.rendering.Legend;

import java.util.Iterator;
import java.util.Objects;

/**
 * Abstract class to provide methods for bar chart plotting.
 * @author Richard Schmidt
 */
abstract class AbstractBarChartPlotter extends AbstractPlotter<CategoricalBarChart> {

    CategoricalPointListContainer<PointList> mCatList;

    int mNumBar;
    double mBarDist;
    double mBarWidth;
    double mLastXValue;
    double mMaxWidth;
    double mMinDist;
    double mMinWidth;
    double[] mGridHelp;

    // value for scaling mXTickStep for y-axis on x-axis, if there are negative values
    double mNegative;

    // constants
    private static final double STAIRDIST = 6;

    /**
     * Prepares bar chart plot using the defined methods.
     * @param diagram {@link de.tudresden.inf.mci.brailleplot.diagrams.BarChart} with the data.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     */
    void prereq(final CategoricalBarChart diagram, final PlotCanvas canvas) {

        setCanvas(canvas);
        mCanvas.readConfig();
        setData();
        mDiagram = Objects.requireNonNull(diagram);
        mLegend = new Legend();
        mCatList = mDiagram.getDataSet();
        mMaxWidth = mCanvas.getMaxBarWidth();
        mMinDist = mCanvas.getMinBarDist();
        mMinWidth = mCanvas.getMinBarWidth();
        mPageHeight = mCanvas.getPrintableHeight();
        mPageWidth = mCanvas.getPrintableWidth();
        mResolution = mCanvas.getResolution();
        mStepSize = mCanvas.getDotDiameter() + 1;
        mGrid = mCanvas.getGrid();

        checkResolution();
        calculateRanges();
        drawAxes();
        mScaleX = scaleAxis("z");
        mCanvas.setXScaleFactor((int) mScaleX[mScaleX.length - 1]);
        mLegend.setType(THREE);
        nameXAxis();
        nameTitle();

        Iterator<PointList> catListIt = mCatList.iterator();
        for (int i = 0; i < mCatList.getSize(); i++) {
            if (catListIt.hasNext()) {
                mLegend.addSymbolExplanation("y-axis", Integer.toString(i), catListIt.next().getName());
            }
        }

        for (int i = 0; i < mCatList.getNumberOfCategories(); i++) {
            mLegend.addSymbolExplanation("textures", Integer.toString(i), mCatList.getCategory(i));
        }
    }

    /**
     * Prepares rectangle drawing.
     * @param i Corresponds to the relative position on the y-axis.
     * @param j Corresponds to the category and the filling.
     * @param xValue Value on the x-axis.
     * @throws InsufficientRenderingAreaException If there are more data series than textures.
     */
    abstract void drawRectangle(int i, int j, double xValue) throws InsufficientRenderingAreaException;

    /**
     * Plots the rectangle and chooses a texture. Add new textures in if statement.
     * @param startY Absolute starting y-coordinate. Y-coordinate must be decreasing.
     * @param starterX Absolute starting x-coordinate.
     * @param enderX Absolute ending x-coordinate.
     * @param j Corresponds to the category and the texture.
     * @param legend True if texture is for the legend, false if texture is for the diagram.
     * @return Boolean is true, if a new page was set on the Canvas.
     * @throws InsufficientRenderingAreaException If there are more data series than textures.
     */
    boolean plotAndFillRectangle(final double startY, final double starterX, final double enderX, final int j, final boolean legend) throws InsufficientRenderingAreaException {
        boolean newPage = false;
        double startX = starterX;
        double endX = enderX;
        double starterY = startY;

        // plot rectangle
        if (legend) {
            if (starterY > mCanvas.getPageHeight() - (mCanvas.getCellHeight() + ONE * mCanvas.getCellDistVer())) {
                mCanvas.getNewPage();
                setData();
                starterY = THIRTYFIVE;
                newPage = true;
            }

            for (int i = 0; i < 2; i++) {
                for (double k = starterY - mStepSize; k > starterY - THIRTY; k -= mStepSize) {
                    addPoint(startX + i * (endX - startX), k);
                }
            }
            for (int i = 0; i < 2; i++) {
                for (double k = endX - SIXTY; k <= endX; k += mStepSize) {
                    addPoint(k, starterY - i * THIRTY);
                }
            }

        } else if (endX > startX) {
            for (double i = startX; i <= endX; i += mStepSize) {
                addPoint(i, starterY);
            }
            for (double i = starterY - mStepSize; i >= starterY - mBarWidth; i -= mStepSize) {
                addPoint(endX, i);
            }
            for (double i = startX; i <= endX; i += mStepSize) {
                addPoint(i, starterY - mBarWidth);
            }
        } else {
            for (double i = endX; i <= startX; i += mStepSize) {
                addPoint(i, starterY);
            }
            for (double i = starterY - mStepSize; i >= starterY - mBarWidth; i -= mStepSize) {
                addPoint(endX, i);
            }
            for (double i = endX; i <= startX; i += mStepSize) {
                addPoint(i, starterY - mBarWidth);
            }
        }


        // choose texture; new textures are added here
        if (j == 0) {
            fillFullPattern(starterY, endX, legend);
        } else if (j == 1) {
            fillVerticalLine(starterY, endX, legend);
        } else if (j == 2) {
            fillDiagonalRight(starterY, endX, legend);
        } else if (j == THREE) {
            fillGridPattern(starterY, endX, legend);
        } else if (j == FOUR) {
            fillDottedPattern(starterY, endX, legend);
        } else if (j == FIVE) {
            if (legend) {
                fillStairPatternL(starterY, endX);
            } else {
                fillStairPatternD(starterY, endX);
            }
        } else if (j == SIX) {
            fillDiagonalLeft(starterY, endX, legend);
        } else {
            throw new InsufficientRenderingAreaException("There are more data series than textures.");
        }

        return newPage;
    }

    /**
     * Fills a rectangle with the texture full_pattern.
     * @param startY Absolute starting y-coordinate. Y-coordinate must be decreasing.
     * @param endX Absolute ending x-coordinate.
     * @param legend True if texture is for the legend, false if texture is for the diagram.
     */
    private void fillFullPattern(final double startY, final double endX, final boolean legend) {

        if (legend) {
            for (double i = FIVE + mStepSize; i < endX; i += mStepSize) {
                for (double j = startY - mStepSize; j > startY - THIRTY; j -= mStepSize) {
                    addPoint(i, j);
                }
            }
        } else {
            if (endX <  mLeftMargin + mNegative * mXTickStep) {
                for (double i = mLeftMargin + mNegative * mXTickStep - mStepSize; i > endX + mStepSize / 2; i -= mStepSize) {
                    for (double j = startY - mStepSize; j > startY - mBarWidth; j -= mStepSize) {
                        addPoint(i, j);
                    }
                }
            } else {
                for (double i = mLeftMargin + mNegative * mXTickStep + mStepSize; i < endX - mStepSize / 2; i += mStepSize) {
                    for (double j = startY - mStepSize; j > startY - mBarWidth; j -= mStepSize) {
                        addPoint(i, j);
                    }
                }
            }
        }
    }


    /**
     * Fills a rectangle with the texture vertical_line.
     * @param startY Absolute starting y-coordinate. Y-coordinate must be decreasing.
     * @param endX Absolute ending x-coordinate.
     * @param legend True if texture is for the legend, false if texture is for the diagram.
     */
    private void fillVerticalLine(final double startY, final double endX, final boolean legend) {

        if (legend) {
            for (double i = FIVE + THREE * mStepSize; i < endX - mStepSize; i += THREE * mStepSize) {
                for (double j = startY - THREE * mStepSize; j > startY - THIRTY + THREE * mStepSize; j -= mStepSize) {
                    addPoint(i, j);
                }
            }
        } else {
            if (endX < mLastXValue + mNegative * mXTickStep) {
                for (double i = mLastXValue + mNegative * mXTickStep - THREE * mStepSize; i > endX + mStepSize; i -= THREE * mStepSize) {
                    for (double j = startY - THREE * mStepSize; j > startY - mBarWidth + THREE * mStepSize; j -= mStepSize) {
                        addPoint(i, j);
                    }
                }
            } else {
                for (double i = mLastXValue + THREE * mStepSize + mNegative * mXTickStep; i < endX - mStepSize; i += THREE * mStepSize) {
                    for (double j = startY - THREE * mStepSize; j > startY - mBarWidth + THREE * mStepSize; j -= mStepSize) {
                        addPoint(i, j);
                    }
                }
            }
        }
    }

    /**
     * Fills a rectangle with the texture diagonal_right.
     * @param startY Absolute starting y-coordinate. Y-coordinate must be decreasing.
     * @param endX Absolute ending x-coordinate.
     * @param legend True if texture is for the legend, false if texture is for the diagram.
     */
    private void fillDiagonalRight(final double startY, final double endX, final boolean legend) {

        if (legend) {
            for (double j = startY - THIRTY + mStepSize; j < startY; j += FIVE * mStepSize) {
                double y = j;
                for (double i = FIVE + mStepSize; y < startY && i < endX; i += mStepSize) {
                    addPoint(i, y);
                    y += mStepSize;
                }
            }

            for (double i = FIVE + SIX * mStepSize; i < endX; i += FIVE * mStepSize) {
                double y = startY - THIRTY + mStepSize;
                for (double k = i; y < startY && k < endX; k += mStepSize) {
                    addPoint(k, y);
                    y += mStepSize;
                }
            }
        } else {
            if (endX < mLastXValue + mNegative * mXTickStep) {
                for (double j = startY - mBarWidth + mStepSize; j < startY; j += FIVE * mStepSize) {
                    double y = j;
                    for (double i = mLastXValue + mNegative * mXTickStep - mStepSize; y < startY && i > endX; i -= mStepSize) {
                        addPoint(i, y);
                        y += mStepSize;
                    }
                }

                for (double i = mLastXValue + mNegative * mXTickStep - SIX * mStepSize; i > endX; i -= FIVE * mStepSize) {
                    double y = startY - mBarWidth + mStepSize;
                    for (double k = i; y < startY && k > endX; k -= mStepSize) {
                        addPoint(k, y);
                        y += mStepSize;
                    }
                }
            } else {
                for (double j = startY - mBarWidth + mStepSize; j < startY; j += FIVE * mStepSize) {
                    double y = j;
                    for (double i = mLastXValue + mStepSize + mNegative * mXTickStep; y < startY && i < endX; i += mStepSize) {
                        addPoint(i, y);
                        y += mStepSize;
                    }
                }

                for (double i = mLastXValue + SIX * mStepSize + mNegative * mXTickStep; i < endX; i += FIVE * mStepSize) {
                    double y = startY - mBarWidth + mStepSize;
                    for (double k = i; y < startY && k < endX; k += mStepSize) {
                        addPoint(k, y);
                        y += mStepSize;
                    }
                }
            }
        }
    }

    /**
     * Fills a rectangle with the texture grid_pattern.
     * @param startY Absolute starting y-coordinate. Y-coordinate must be decreasing.
     * @param endX Absolute ending x-coordinate.
     * @param legend True if texture is for the legend, false if texture is for the diagram.
     */
    private void fillGridPattern(final double startY, final double endX, final boolean legend) {

        if (legend) {
            for (double i = FIVE + THREE * mStepSize; i < endX - 2 * mStepSize; i += THREE * mStepSize) {
                for (double j = startY - mStepSize; j > startY - THIRTY; j -= mStepSize) {
                    addPoint(i, j);
                }
            }

            for (double j = startY - THREE * mStepSize; j > startY - THIRTY + mStepSize; j -= THREE * mStepSize) {
                for (double i = FIVE + mStepSize; i < endX; i += mStepSize) {
                    addPoint(i, j);
                }
            }
        } else {
            if (endX < mLastXValue + mNegative * mXTickStep) {
                for (double i = mLastXValue + mNegative * mXTickStep - THREE * mStepSize; i > endX + 2 * mStepSize; i -= THREE * mStepSize) {
                    for (double j = startY - mStepSize; j > startY - mBarWidth; j -= mStepSize) {
                        addPoint(i, j);
                    }
                }

                for (double j = startY - THREE * mStepSize; j > startY - mBarWidth + mStepSize; j -= THREE * mStepSize) {
                    for (double i = mLastXValue - mStepSize + mNegative * mXTickStep; i > endX; i -= mStepSize) {
                        addPoint(i, j);
                    }
                }
            } else {
                for (double i = mLastXValue + THREE * mStepSize + mNegative * mXTickStep; i < endX - 2 * mStepSize; i += THREE * mStepSize) {
                    for (double j = startY - mStepSize; j > startY - mBarWidth; j -= mStepSize) {
                        addPoint(i, j);
                    }
                }

                for (double j = startY - THREE * mStepSize; j > startY - mBarWidth + mStepSize; j -= THREE * mStepSize) {
                    for (double i = mLastXValue + mStepSize + mNegative * mXTickStep; i < endX; i += mStepSize) {
                        addPoint(i, j);
                    }
                }
            }
        }
    }

    /**
     * Fills a rectangle with the texture dotted_pattern.
     * @param startY Absolute starting y-coordinate. Y-coordinate must be decreasing.
     * @param endX Absolute ending x-coordinate.
     * @param legend True if texture is for the legend, false if texture is for the diagram.
     */
    private void fillDottedPattern(final double startY, final double endX, final boolean legend) {
        if (legend) {
            for (double i = FIVE + THREE * mStepSize; i < endX - mStepSize; i += THREE * mStepSize) {
                for (double j = startY - THREE * mStepSize; j > startY - THIRTY + mStepSize; j -= THREE * mStepSize) {
                    addPoint(i, j);
                }
            }
        } else {
            if (endX < mLastXValue + mNegative * mXTickStep) {
                for (double i = mLastXValue - THREE * mStepSize + mNegative * mXTickStep; i > endX + mStepSize; i -= THREE * mStepSize) {
                    for (double j = startY - THREE * mStepSize; j > startY - mBarWidth + mStepSize; j -= THREE * mStepSize) {
                        addPoint(i, j);
                    }
                }
            } else {
                for (double i = mLastXValue + THREE * mStepSize + mNegative * mXTickStep; i < endX - mStepSize; i += THREE * mStepSize) {
                    for (double j = startY - THREE * mStepSize; j > startY - mBarWidth + mStepSize; j -= THREE * mStepSize) {
                        addPoint(i, j);
                    }
                }
            }
        }
    }

    /**
     * Fills a rectangle with the texture stair_pattern on the diagram.
     * @param startY Absolute starting y-coordinate. Y-coordinate must be decreasing.
     * @param endX Absolute ending x-coordinate.
     */
    private void fillStairPatternD(final double startY, final double endX) {
        double last = mLeftMargin + mNegative * mXTickStep;

        if (endX < mLastXValue + mNegative * mXTickStep) {
            outerloop:
            for (double j = startY - STAIRDIST; j > startY - mBarWidth; j -= TWOFIVE * STAIRDIST) {
                double lastX;
                double lastY;
                for (double i = mLastXValue - mStepSize + mNegative * mXTickStep; i >= mLastXValue - THREE * mStepSize + mNegative * mXTickStep; i -= mStepSize) {
                    if (i > endX) {
                        addPoint(i, j);
                        last = i;
                    } else {
                        continue outerloop;
                    }
                }
                lastX = last;
                for (double k = j - mStepSize; k >= j - THREE * mStepSize; k -= mStepSize) {
                    if (k > startY - mBarWidth) {
                        addPoint(lastX, k);
                        last = k;
                    } else {
                        continue outerloop;
                    }
                }
                lastY = last;
                while (true) {
                    for (double i = lastX - mStepSize; i >= lastX - THREE * mStepSize; i -= mStepSize) {
                        if (i > endX) {
                            addPoint(i, lastY);
                            last = i;
                        } else {
                            continue outerloop;
                        }
                    }
                    lastX = last;
                    for (double k = lastY - mStepSize; k >= lastY - THREE * mStepSize; k -= mStepSize) {
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
            for (double i = mLastXValue - 2 - 2 * STAIRDIST + mNegative * mXTickStep; i > endX; i -= TWOFIVE * STAIRDIST) {
                double lastX;
                double lastY;
                for (double j = startY - mStepSize; j >= startY - 2 * mStepSize; j -= mStepSize) {
                    if (j > startY - mBarWidth) {
                        addPoint(i, j);
                        last = j;
                    } else {
                        continue anotherloop;
                    }
                }
                lastY = last;
                for (double k = i - mStepSize; k >= i - THREE * mStepSize; k -= mStepSize) {
                    if (k > endX) {
                        addPoint(k, lastY);
                        last = k;
                    } else {
                        continue anotherloop;
                    }
                }
                lastX = last;
                while (true) {
                    for (double j = lastY - mStepSize; j >= lastY - THREE * mStepSize; j -= mStepSize) {
                        if (j > startY - mBarWidth) {
                            addPoint(lastX, j);
                            last = j;
                        } else {
                            continue anotherloop;
                        }
                    }
                    lastY = last;
                    for (double k = lastX - mStepSize; k >= lastX - THREE * mStepSize; k -= mStepSize) {
                        if (k > endX) {
                            addPoint(k, lastY);
                            last = k;
                        } else {
                            continue anotherloop;
                        }
                    }
                    lastX = last;
                }
            }
        } else {
            outerloop:
            for (double j = startY - STAIRDIST; j > startY - mBarWidth; j -= TWOFIVE * STAIRDIST) {
                double lastX;
                double lastY;
                for (double i = mLastXValue + mStepSize + mNegative * mXTickStep; i <= mLastXValue + THREE * mStepSize + mNegative * mXTickStep; i += mStepSize) {
                    if (i < endX) {
                        addPoint(i, j);
                        last = i;
                    } else {
                        continue outerloop;
                    }
                }
                lastX = last;
                for (double k = j - mStepSize; k >= j - THREE * mStepSize; k -= mStepSize) {
                    if (k > startY - mBarWidth) {
                        addPoint(lastX, k);
                        last = k;
                    } else {
                        continue outerloop;
                    }
                }
                lastY = last;
                while (true) {
                    for (double i = lastX + mStepSize; i <= lastX + THREE * mStepSize; i += mStepSize) {
                        if (i < endX) {
                            addPoint(i, lastY);
                            last = i;
                        } else {
                            continue outerloop;
                        }
                    }
                    lastX = last;
                    for (double k = lastY - mStepSize; k >= lastY - THREE * mStepSize; k -= mStepSize) {
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
            for (double i = mLastXValue + 2 + 2 * STAIRDIST + mNegative * mXTickStep; i < endX; i += TWOFIVE * STAIRDIST) {
                double lastX;
                double lastY;
                for (double j = startY - mStepSize; j >= startY - 2 * mStepSize; j -= mStepSize) {
                    if (j > startY - mBarWidth) {
                        addPoint(i, j);
                        last = j;
                    } else {
                        continue anotherloop;
                    }
                }
                lastY = last;
                for (double k = i + mStepSize; k <= i + THREE * mStepSize; k += mStepSize) {
                    if (k < endX) {
                        addPoint(k, lastY);
                        last = k;
                    } else {
                        continue anotherloop;
                    }
                }
                lastX = last;
                while (true) {
                    for (double j = lastY - mStepSize; j >= lastY - THREE * mStepSize; j -= mStepSize) {
                        if (j > startY - mBarWidth) {
                            addPoint(lastX, j);
                            last = j;
                        } else {
                            continue anotherloop;
                        }
                    }
                    lastY = last;
                    for (double k = lastX + mStepSize; k <= lastX + THREE * mStepSize; k += mStepSize) {
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

    }

    /**
     * Fills a rectangle with the texture stair_pattern on the legend.
     * @param startY Absolute starting y-coordinate. Y-coordinate must be decreasing.
     * @param endX Absolute ending x-coordinate.
     */
    private void fillStairPatternL(final double startY, final double endX) {
        double last = 0;
        outerloop:
        for (double j = startY - STAIRDIST; j > startY - THIRTY; j -= TWOFIVE * STAIRDIST) {
            double lastX;
            double lastY;
            for (double i = FIVE + mStepSize; i <= FIVE + THREE * mStepSize; i += mStepSize) {
                if (i < endX) {
                    addPoint(i, j);
                    last = i;
                } else {
                    continue outerloop;
                }
            }
            lastX = last;
            for (double k = j - mStepSize; k >= j - THREE * mStepSize; k -= mStepSize) {
                if (k > startY - THIRTY) {
                    addPoint(lastX, k);
                    last = k;
                } else {
                    continue outerloop;
                }
            }
            lastY = last;
            while (true) {
                for (double i = lastX + mStepSize; i <= lastX + THREE * mStepSize; i += mStepSize) {
                    if (i < endX) {
                        addPoint(i, lastY);
                        last = i;
                    } else {
                        continue outerloop;
                    }
                }
                lastX = last;
                for (double k = lastY - mStepSize; k >= lastY - THREE * mStepSize; k -= mStepSize) {
                    if (k > startY - THIRTY) {
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
        for (double i = SEVEN + 2 * STAIRDIST; i < endX; i += TWOFIVE * STAIRDIST) {
            double lastX;
            double lastY;
            for (double j = startY - mStepSize; j >= startY - 2 * mStepSize; j -= mStepSize) {
                if (j > startY - THIRTY) {
                    addPoint(i, j);
                    last = j;
                } else {
                    continue anotherloop;
                }
            }
            lastY = last;
            for (double k = i + mStepSize; k <= i + THREE * mStepSize; k += mStepSize) {
                if (k < endX) {
                    addPoint(k, lastY);
                    last = k;
                } else {
                    continue anotherloop;
                }
            }
            lastX = last;
            while (true) {
                for (double j = lastY - mStepSize; j >= lastY - THREE * mStepSize; j -= mStepSize) {
                    if (j > startY - THIRTY) {
                        addPoint(lastX, j);
                        last = j;
                    } else {
                        continue anotherloop;
                    }
                }
                lastY = last;
                for (double k = lastX + mStepSize; k <= lastX + THREE * mStepSize; k += mStepSize) {
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
     * @param startY Absolute starting y-coordinate. Y-coordinate must be decreasing.
     * @param endX Absolute ending x-coordinate.
     * @param legend True if texture is for the legend, false if texture is for the diagram.
     */
    private void fillDiagonalLeft(final double startY, final double endX, final boolean legend) {

        if (legend) {
            for (double j = startY - mStepSize; j > startY - THIRTY; j -= FIVE * mStepSize) {
                double y = j;
                for (double i = FIVE + mStepSize; y > startY - THIRTY && i < endX; i += mStepSize) {
                    addPoint(i, y);
                    y -= mStepSize;
                }
            }

            for (double i = FIVE + SIX * mStepSize; i < endX; i += FIVE * mStepSize) {
                double y = startY - mStepSize;
                for (double k = i; y > startY - THIRTY && k < endX; k += mStepSize) {
                    addPoint(k, y);
                    y -= mStepSize;
                }
            }
        } else {
            if (endX < mLastXValue + mNegative * mXTickStep) {
                for (double j = startY - mStepSize; j > startY - mBarWidth; j -= FIVE * mStepSize) {
                    double y = j;
                    for (double i = mLastXValue - mStepSize + mNegative * mXTickStep; y > startY - mBarWidth && i > endX; i -= mStepSize) {
                        addPoint(i, y);
                        y -= mStepSize;
                    }
                }

                for (double i = mLastXValue - SIX * mStepSize + mNegative * mXTickStep; i > endX; i -= FIVE * mStepSize) {
                    double y = startY - mStepSize;
                    for (double k = i; y > startY - mBarWidth && k > endX; k -= mStepSize) {
                        addPoint(k, y);
                        y -= mStepSize;
                    }
                }
            } else {
                for (double j = startY - mStepSize; j > startY - mBarWidth; j -= FIVE * mStepSize) {
                    double y = j;
                    for (double i = mLastXValue + mStepSize + mNegative * mXTickStep; y > startY - mBarWidth && i < endX; i += mStepSize) {
                        addPoint(i, y);
                        y -= mStepSize;
                    }
                }

                for (double i = mLastXValue + SIX * mStepSize + mNegative * mXTickStep; i < endX; i += FIVE * mStepSize) {
                    double y = startY - mStepSize;
                    for (double k = i; y > startY - mBarWidth && k < endX; k += mStepSize) {
                        addPoint(k, y);
                        y -= mStepSize;
                    }
                }
            }
        }
    }

}
