package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;

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
    String[] mNamesY;

    // constants
    private static final double STAIRDIST = 6;

    /**
     * Prepares bar chart plot using the defined methods.
     * @param diagram {@link de.tudresden.inf.mci.brailleplot.diagrams.BarChart} with the data.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     */
    void prereq(final CategoricalBarChart diagram, final PlotCanvas canvas) {

        mCanvas = Objects.requireNonNull(canvas);
        mCanvas.readConfig();
        mData = mCanvas.getCurrentPage();
        mDiagram = Objects.requireNonNull(diagram);
        mCatList = (CategoricalPointListContainer<PointList>) mDiagram.getDataSet();
        mMaxWidth = mCanvas.getMaxBarWidth();
        mMinDist = mCanvas.getMinBarDist();
        mMinWidth = mCanvas.getMinBarWidth();
        mPageHeight = mCanvas.getPrintableHeight();
        mPageWidth = mCanvas.getPrintableWidth();
        mResolution = mCanvas.getResolution();
        mStepSize = mCanvas.getDotDiameter();

        checkResolution();
        calculateRanges();
        drawAxes();
        mScaleX = scaleAxis("z");
        mNamesY = new String[mCatList.getSize()];

        Iterator<PointList> catListIt = mCatList.iterator();
        for (int i = 0; i < mNamesY.length; i++) {
            if (catListIt.hasNext()) {
                mNamesY[i] = catListIt.next().getName();
            }
        }
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

        mTickDistance = mLeftMargin;
        if (mTickDistance < THIRTY) {
            mTickDistance = THIRTY;
        }

        // x-axis
        double lastValueX = mLeftMargin;
        for (double i = mLeftMargin; i <= mPageWidth; i += mStepSize) {
            addPoint(i, mBottomMargin);
            lastValueX = i;
        }
        mLengthX = lastValueX - mLeftMargin;
        mNumberXTicks = (int) Math.floor(mLengthX / mTickDistance);
        if (mNumberXTicks < 2) {
            mNumberXTicks = 2;
        } else if (mNumberXTicks <= FIVE) {
            mNumberXTicks = FIVE;
        } else if (mNumberYTicks <= TEN) {
            mNumberYTicks = TEN;
        } else if (mNumberYTicks <= FIFTEEN) {
            mNumberYTicks = FIFTEEN;
        } else {
            mNumberYTicks = TWENTY;
        }

        mScaleX = new int[mNumberXTicks + 1];


        // tick marks on x-axis
        mXTickStep = (lastValueX - MARGIN - mLeftMargin) / mNumberXTicks;
        for (double i = 1; i <= 2 * mNumberXTicks; i++) {
            if (i % 2 == 0) {
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK2);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK3);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK4);
            } else {
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK1);
                addPoint(mLeftMargin + (i / 2) * mXTickStep, mBottomMargin + TICK2);
            }
        }

        // y-axis
        double lastValueY = mBottomMargin;
        for (double i = mBottomMargin; i > mTitleMargin; i -= mStepSize) {
            addPoint(mLeftMargin, i);
            lastValueY = i;
        }

        mLengthY = mBottomMargin - lastValueY;

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
     * @param startY Absolute starting y-coordinate.
     * @param endX Absolute ending x-coordinate.
     * @param j Corresponds to the category and the texture.
     * @throws InsufficientRenderingAreaException If there are more data series than textures.
     */
    void plotAndFillRectangle(final double startY, final double endX, final int j) throws InsufficientRenderingAreaException {
        // plot rectangle
        for (double i = mLeftMargin; i <= endX; i += mStepSize) {
            addPoint(i, startY);
        }
        for (double i = startY - mStepSize; i >= startY - mBarWidth; i -= mStepSize) {
            addPoint(endX, i);
        }
        for (double i = mLeftMargin; i <= endX; i += mStepSize) {
            addPoint(i, startY - mBarWidth);
        }

        // choose texture; new textures are added here
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
        } else {
            throw new InsufficientRenderingAreaException("There are more data series than textures.");
        }
    }

    /**
     * Fills a rectangle with the texture full_pattern.
     * @param startY Absolute starting y-coordinate.
     * @param endX Absolute starting y-coordinate.
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
     * @param startY Absolute starting y-coordinate.
     * @param endX Absolute starting y-coordinate.
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
     * @param startY Absolute starting y-coordinate.
     * @param endX Absolute starting y-coordinate.
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
     * @param startY Absolute starting y-coordinate.
     * @param endX Absolute starting y-coordinate.
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
     * @param startY Absolute starting y-coordinate.
     * @param endX Absolute starting y-coordinate.
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
     * @param startY Absolute starting y-coordinate.
     * @param endX Absolute starting y-coordinate.
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
     * @param startY Absolute starting y-coordinate.
     * @param endX Absolute starting y-coordinate.
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

}
