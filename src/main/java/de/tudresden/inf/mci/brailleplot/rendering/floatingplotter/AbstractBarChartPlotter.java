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
            if (starterY > mCanvas.getPageHeight() - (mCanvas.getCellHeight() + RECTSCALE * mCanvas.getCellDistVer())) {
                mCanvas.getNewPage();
                setData();
                starterY = SCALE2;
                newPage = true;
            }

            for (int i = 0; i < 2; i++) {
                for (double k = starterY - mStepSize; k > starterY - BAR; k -= mStepSize) {
                    addPoint(startX + i * (endX - startX), k);
                }
            }
            for (int i = 0; i < 2; i++) {
                for (double k = endX - SCALE1; k <= endX; k += mStepSize) {
                    addPoint(k, starterY - i * BAR);
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
        } else if (j == COMPARE3) {
            fillGridPattern(starterY, endX, legend);
        } else if (j == COMPARE4) {
            fillDottedPattern(starterY, endX, legend);
        } else if (j == COMPARE5) {
            if (legend) {
                fillStairPatternL(starterY, endX);
            } else {
                fillStairPatternD(starterY, endX);
            }
        } else if (j == COMPARE6) {
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
            for (double i = FULLSCALE + mStepSize; i < endX; i += mStepSize) {
                for (double j = startY - mStepSize; j > startY - BAR; j -= mStepSize) {
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
            for (double i = VERTSCALE + VERTSCALE2 * mStepSize; i < endX - mStepSize; i += VERTSCALE2 * mStepSize) {
                for (double j = startY - VERTSCALE2 * mStepSize; j > startY - BAR + VERTSCALE2 * mStepSize; j -= mStepSize) {
                    addPoint(i, j);
                }
            }
        } else {
            if (endX < mLastXValue + mNegative * mXTickStep) {
                for (double i = mLastXValue + mNegative * mXTickStep - VERTSCALE2 * mStepSize; i > endX + mStepSize; i -= VERTSCALE2 * mStepSize) {
                    for (double j = startY - VERTSCALE2 * mStepSize; j > startY - mBarWidth + VERTSCALE2 * mStepSize; j -= mStepSize) {
                        addPoint(i, j);
                    }
                }
            } else {
                for (double i = mLastXValue + VERTSCALE2 * mStepSize + mNegative * mXTickStep; i < endX - mStepSize; i += VERTSCALE2 * mStepSize) {
                    for (double j = startY - VERTSCALE2 * mStepSize; j > startY - mBarWidth + VERTSCALE2 * mStepSize; j -= mStepSize) {
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
            for (double j = startY - BAR + mStepSize; j < startY; j += DIAGSCALE * mStepSize) {
                double y = j;
                for (double i = DIAGSCALE + mStepSize; y < startY && i < endX; i += mStepSize) {
                    addPoint(i, y);
                    y += mStepSize;
                }
            }

            for (double i = DIAGSCALE + DISTDIAGONALS * mStepSize; i < endX; i += DIAGSCALE * mStepSize) {
                double y = startY - BAR + mStepSize;
                for (double k = i; y < startY && k < endX; k += mStepSize) {
                    addPoint(k, y);
                    y += mStepSize;
                }
            }
        } else {
            if (endX < mLastXValue + mNegative * mXTickStep) {
                for (double j = startY - mBarWidth + mStepSize; j < startY; j += DIAGSCALE * mStepSize) {
                    double y = j;
                    for (double i = mLastXValue + mNegative * mXTickStep - mStepSize; y < startY && i > endX; i -= mStepSize) {
                        addPoint(i, y);
                        y += mStepSize;
                    }
                }

                for (double i = mLastXValue + mNegative * mXTickStep - DISTDIAGONALS * mStepSize; i > endX; i -= DIAGSCALE * mStepSize) {
                    double y = startY - mBarWidth + mStepSize;
                    for (double k = i; y < startY && k > endX; k -= mStepSize) {
                        addPoint(k, y);
                        y += mStepSize;
                    }
                }
            } else {
                for (double j = startY - mBarWidth + mStepSize; j < startY; j += DIAGSCALE * mStepSize) {
                    double y = j;
                    for (double i = mLastXValue + mStepSize + mNegative * mXTickStep; y < startY && i < endX; i += mStepSize) {
                        addPoint(i, y);
                        y += mStepSize;
                    }
                }

                for (double i = mLastXValue + DISTDIAGONALS * mStepSize + mNegative * mXTickStep; i < endX; i += DIAGSCALE * mStepSize) {
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
            for (double i = GRIDSCALE + GRIDSCALE2 * mStepSize; i < endX - 2 * mStepSize; i += GRIDSCALE2 * mStepSize) {
                for (double j = startY - mStepSize; j > startY - BAR; j -= mStepSize) {
                    addPoint(i, j);
                }
            }

            for (double j = startY - GRIDSCALE2 * mStepSize; j > startY - BAR + mStepSize; j -= GRIDSCALE2 * mStepSize) {
                for (double i = GRIDSCALE + mStepSize; i < endX; i += mStepSize) {
                    addPoint(i, j);
                }
            }
        } else {
            if (endX < mLastXValue + mNegative * mXTickStep) {
                for (double i = mLastXValue + mNegative * mXTickStep - GRIDSCALE2 * mStepSize; i > endX + 2 * mStepSize; i -= GRIDSCALE2 * mStepSize) {
                    for (double j = startY - mStepSize; j > startY - mBarWidth; j -= mStepSize) {
                        addPoint(i, j);
                    }
                }

                for (double j = startY - GRIDSCALE2 * mStepSize; j > startY - mBarWidth + mStepSize; j -= GRIDSCALE2 * mStepSize) {
                    for (double i = mLastXValue - mStepSize + mNegative * mXTickStep; i > endX; i -= mStepSize) {
                        addPoint(i, j);
                    }
                }
            } else {
                for (double i = mLastXValue + GRIDSCALE2 * mStepSize + mNegative * mXTickStep; i < endX - 2 * mStepSize; i += GRIDSCALE2 * mStepSize) {
                    for (double j = startY - mStepSize; j > startY - mBarWidth; j -= mStepSize) {
                        addPoint(i, j);
                    }
                }

                for (double j = startY - GRIDSCALE2 * mStepSize; j > startY - mBarWidth + mStepSize; j -= GRIDSCALE2 * mStepSize) {
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
            for (double i = DOTSCALE + DOTSCALE2 * mStepSize; i < endX - mStepSize; i += DOTSCALE2 * mStepSize) {
                for (double j = startY - DOTSCALE2 * mStepSize; j > startY - BAR + mStepSize; j -= DOTSCALE2 * mStepSize) {
                    addPoint(i, j);
                }
            }
        } else {
            if (endX < mLastXValue + mNegative * mXTickStep) {
                for (double i = mLastXValue - DOTSCALE2 * mStepSize + mNegative * mXTickStep; i > endX + mStepSize; i -= DOTSCALE2 * mStepSize) {
                    for (double j = startY - DOTSCALE2 * mStepSize; j > startY - mBarWidth + mStepSize; j -= DOTSCALE2 * mStepSize) {
                        addPoint(i, j);
                    }
                }
            } else {
                for (double i = mLastXValue + DOTSCALE2 * mStepSize + mNegative * mXTickStep; i < endX - mStepSize; i += DOTSCALE2 * mStepSize) {
                    for (double j = startY - DOTSCALE2 * mStepSize; j > startY - mBarWidth + mStepSize; j -= DOTSCALE2 * mStepSize) {
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
            for (double j = startY - STAIRDIST; j > startY - mBarWidth; j -= STAIRSCALE2 * STAIRDIST) {
                double lastX;
                double lastY;
                for (double i = mLastXValue - mStepSize + mNegative * mXTickStep; i >= mLastXValue - STAIRSCALE3 * mStepSize + mNegative * mXTickStep; i -= mStepSize) {
                    if (i > endX) {
                        addPoint(i, j);
                        last = i;
                    } else {
                        continue outerloop;
                    }
                }
                lastX = last;
                for (double k = j - mStepSize; k >= j - STAIRSCALE3 * mStepSize; k -= mStepSize) {
                    if (k > startY - mBarWidth) {
                        addPoint(lastX, k);
                        last = k;
                    } else {
                        continue outerloop;
                    }
                }
                lastY = last;
                while (true) {
                    for (double i = lastX - mStepSize; i >= lastX - STAIRSCALE3 * mStepSize; i -= mStepSize) {
                        if (i > endX) {
                            addPoint(i, lastY);
                            last = i;
                        } else {
                            continue outerloop;
                        }
                    }
                    lastX = last;
                    for (double k = lastY - mStepSize; k >= lastY - STAIRSCALE3 * mStepSize; k -= mStepSize) {
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
            for (double i = mLastXValue - 2 - 2 * STAIRDIST + mNegative * mXTickStep; i > endX; i -= STAIRSCALE2 * STAIRDIST) {
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
                for (double k = i - mStepSize; k >= i - STAIRSCALE3 * mStepSize; k -= mStepSize) {
                    if (k > endX) {
                        addPoint(k, lastY);
                        last = k;
                    } else {
                        continue anotherloop;
                    }
                }
                lastX = last;
                while (true) {
                    for (double j = lastY - mStepSize; j >= lastY - STAIRSCALE3 * mStepSize; j -= mStepSize) {
                        if (j > startY - mBarWidth) {
                            addPoint(lastX, j);
                            last = j;
                        } else {
                            continue anotherloop;
                        }
                    }
                    lastY = last;
                    for (double k = lastX - mStepSize; k >= lastX - STAIRSCALE3 * mStepSize; k -= mStepSize) {
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
            for (double j = startY - STAIRDIST; j > startY - mBarWidth; j -= STAIRSCALE2 * STAIRDIST) {
                double lastX;
                double lastY;
                for (double i = mLastXValue + mStepSize + mNegative * mXTickStep; i <= mLastXValue + STAIRSCALE3 * mStepSize + mNegative * mXTickStep; i += mStepSize) {
                    if (i < endX) {
                        addPoint(i, j);
                        last = i;
                    } else {
                        continue outerloop;
                    }
                }
                lastX = last;
                for (double k = j - mStepSize; k >= j - STAIRSCALE3 * mStepSize; k -= mStepSize) {
                    if (k > startY - mBarWidth) {
                        addPoint(lastX, k);
                        last = k;
                    } else {
                        continue outerloop;
                    }
                }
                lastY = last;
                while (true) {
                    for (double i = lastX + mStepSize; i <= lastX + STAIRSCALE3 * mStepSize; i += mStepSize) {
                        if (i < endX) {
                            addPoint(i, lastY);
                            last = i;
                        } else {
                            continue outerloop;
                        }
                    }
                    lastX = last;
                    for (double k = lastY - mStepSize; k >= lastY - STAIRSCALE3 * mStepSize; k -= mStepSize) {
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
            for (double i = mLastXValue + 2 + 2 * STAIRDIST + mNegative * mXTickStep; i < endX; i += STAIRSCALE2 * STAIRDIST) {
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
                for (double k = i + mStepSize; k <= i + STAIRSCALE3 * mStepSize; k += mStepSize) {
                    if (k < endX) {
                        addPoint(k, lastY);
                        last = k;
                    } else {
                        continue anotherloop;
                    }
                }
                lastX = last;
                while (true) {
                    for (double j = lastY - mStepSize; j >= lastY - STAIRSCALE3 * mStepSize; j -= mStepSize) {
                        if (j > startY - mBarWidth) {
                            addPoint(lastX, j);
                            last = j;
                        } else {
                            continue anotherloop;
                        }
                    }
                    lastY = last;
                    for (double k = lastX + mStepSize; k <= lastX + STAIRSCALE3 * mStepSize; k += mStepSize) {
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
        for (double j = startY - STAIRDIST; j > startY - BAR; j -= STAIRSCALE2 * STAIRDIST) {
            double lastX;
            double lastY;
            for (double i = STAIRSCALE + mStepSize; i <= STAIRSCALE + STAIRSCALE3 * mStepSize; i += mStepSize) {
                if (i < endX) {
                    addPoint(i, j);
                    last = i;
                } else {
                    continue outerloop;
                }
            }
            lastX = last;
            for (double k = j - mStepSize; k >= j - STAIRSCALE3 * mStepSize; k -= mStepSize) {
                if (k > startY - BAR) {
                    addPoint(lastX, k);
                    last = k;
                } else {
                    continue outerloop;
                }
            }
            lastY = last;
            while (true) {
                for (double i = lastX + mStepSize; i <= lastX + STAIRSCALE3 * mStepSize; i += mStepSize) {
                    if (i < endX) {
                        addPoint(i, lastY);
                        last = i;
                    } else {
                        continue outerloop;
                    }
                }
                lastX = last;
                for (double k = lastY - mStepSize; k >= lastY - STAIRSCALE3 * mStepSize; k -= mStepSize) {
                    if (k > startY - BAR) {
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
        for (double i = SCALESTAIRS + 2 * STAIRDIST; i < endX; i += STAIRSCALE2 * STAIRDIST) {
            double lastX;
            double lastY;
            for (double j = startY - mStepSize; j >= startY - 2 * mStepSize; j -= mStepSize) {
                if (j > startY - BAR) {
                    addPoint(i, j);
                    last = j;
                } else {
                    continue anotherloop;
                }
            }
            lastY = last;
            for (double k = i + mStepSize; k <= i + STAIRSCALE3 * mStepSize; k += mStepSize) {
                if (k < endX) {
                    addPoint(k, lastY);
                    last = k;
                } else {
                    continue anotherloop;
                }
            }
            lastX = last;
            while (true) {
                for (double j = lastY - mStepSize; j >= lastY - STAIRSCALE3 * mStepSize; j -= mStepSize) {
                    if (j > startY - BAR) {
                        addPoint(lastX, j);
                        last = j;
                    } else {
                        continue anotherloop;
                    }
                }
                lastY = last;
                for (double k = lastX + mStepSize; k <= lastX + STAIRSCALE3 * mStepSize; k += mStepSize) {
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
            for (double j = startY - mStepSize; j > startY - BAR; j -= DIAGSCALE * mStepSize) {
                double y = j;
                for (double i = DIAGSCALE + mStepSize; y > startY - BAR && i < endX; i += mStepSize) {
                    addPoint(i, y);
                    y -= mStepSize;
                }
            }

            for (double i = DIAGSCALE + DISTDIAGONALS * mStepSize; i < endX; i += DIAGSCALE * mStepSize) {
                double y = startY - mStepSize;
                for (double k = i; y > startY - BAR && k < endX; k += mStepSize) {
                    addPoint(k, y);
                    y -= mStepSize;
                }
            }
        } else {
            if (endX < mLastXValue + mNegative * mXTickStep) {
                for (double j = startY - mStepSize; j > startY - mBarWidth; j -= DIAGSCALE * mStepSize) {
                    double y = j;
                    for (double i = mLastXValue - mStepSize + mNegative * mXTickStep; y > startY - mBarWidth && i > endX; i -= mStepSize) {
                        addPoint(i, y);
                        y -= mStepSize;
                    }
                }

                for (double i = mLastXValue - DISTDIAGONALS * mStepSize + mNegative * mXTickStep; i > endX; i -= DIAGSCALE * mStepSize) {
                    double y = startY - mStepSize;
                    for (double k = i; y > startY - mBarWidth && k > endX; k -= mStepSize) {
                        addPoint(k, y);
                        y -= mStepSize;
                    }
                }
            } else {
                for (double j = startY - mStepSize; j > startY - mBarWidth; j -= DIAGSCALE * mStepSize) {
                    double y = j;
                    for (double i = mLastXValue + mStepSize + mNegative * mXTickStep; y > startY - mBarWidth && i < endX; i += mStepSize) {
                        addPoint(i, y);
                        y -= mStepSize;
                    }
                }

                for (double i = mLastXValue + DISTDIAGONALS * mStepSize + mNegative * mXTickStep; i < endX; i += DIAGSCALE * mStepSize) {
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
