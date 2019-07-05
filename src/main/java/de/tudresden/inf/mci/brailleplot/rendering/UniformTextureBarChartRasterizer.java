package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

/**
 * UniformTextureBarChartRasterizer. I will write an explanation when finished, this class has changed like over 9000 times...
 * @author Leonard Kupper
 * @version 2019.07.04
 */
final class UniformTextureBarChartRasterizer implements Rasterizer<BarChart> {

    BarChart mDiagram;
    AbstractRasterCanvas mCanvas;
    MatrixData<Boolean> mData;

    // TODO: move some of these into the format config.
    // algorithm specific constants
    private final int mVerticalAxisMinWidth = 2;
    private final int mHorizontalAxisMinHeight = 2;
    private final int mInnerBarThickness = 1;
    private final int mExtraBarPadding = 0;
    private final int mBarBorder = 1;
    private final int mTitleLines = 2;

    // associated rasterizers
    private Rasterizer<Axis> mAxisRasterizer;
    //private Rasterizer<Legend> mLegendRasterizer;

    // with default axis and legend rasterizer
    UniformTextureBarChartRasterizer() {
        mAxisRasterizer = new LinearMappingAxisRasterizer();
        //mLegendRasterizer = new LegendRasterizer();
    }

    UniformTextureBarChartRasterizer(Rasterizer<Axis> axisRasterizer) {
        mAxisRasterizer = axisRasterizer;
        //mLegendRasterizer = legendRasterizer;
    }

    @Override
    public final void rasterize(final BarChart diagram, final AbstractRasterCanvas canvas) throws InsufficientRenderingAreaException {

        mDiagram = diagram;
        mCanvas = canvas;
        mData = mCanvas.getCurrentPage();

        //check raster
        if (!isValidBrailleRaster()) {
            // TODO: Maybe refactor to have different rendering exceptions?
            throw new InsufficientRenderingAreaException("This rasterizer can only work with a 6-dot or 8-dot braille raster.");
        }

        // determine bar orientation and construct bars
        int requiredCells = requiredCells();
        int availableVerticalCells = availableVerticalCells();
        int availableHorizontalCells = availableHorizontalCells();
        if (availableVerticalCells >= requiredCells) {
            constructHorizontalBars(mVerticalAxisMinWidth, mTitleLines, availableHorizontalCells, availableVerticalCells);
        //} else if (availableHorizontalCells >= requiredCells) {
        //    constructHorizontalBars(mVerticalAxisMinWidth, mTitleLines, availableHorizontalCells, availableVerticalCells);
        } else {
            throw new InsufficientRenderingAreaException("Not enough space to rasterize all categories.");
        }


        Axis xAxis = new Axis(Axis.Type.X_AXIS, 0, mCanvas.getCellHeight() * 2, mCanvas.getCellWidth() * 3, false);
        mAxisRasterizer.rasterize(xAxis, mCanvas);
        //mLegendRasterizer.rasterize(new Legend(), canvas);

    }

    private void constructHorizontalBars(final int offsetX, final int offsetY, final int availableX, final int availableY) {
        Rasterizer.rectangle(
                offsetX * mCanvas.getCellWidth(),
                offsetY * mCanvas.getCellHeight(),
                availableX * mCanvas.getCellWidth(),
                availableY * mCanvas.getCellHeight(),
                mData, true
        );
    }

    // Layout help methods

    private int availableHorizontalCells() {
        return mCanvas.getHorizontalCellCount() - mVerticalAxisMinWidth;
    }

    private int availableVerticalCells() {
        return mCanvas.getVerticalCellCount() - mHorizontalAxisMinHeight - mTitleLines;
    }

    private int requiredCells() {
        return mDiagram.getNumberOfCategories() * (mBarBorder + mInnerBarThickness + mExtraBarPadding) + mBarBorder;
    }

    private boolean isValidBrailleRaster() {
        return ((mCanvas.getCellWidth() == 2) && (mCanvas.getCellHeight() >= 3) && (mCanvas.getCellHeight() <= 4));
    }

    /*
    private boolean isEquidistantRaster() {
        return (
                (canvas.getHorizontalCellDistance() == canvas.getVerticalCellDistance())
                && (canvas.getVerticalCellDistance() == canvas.getHorizontalDotDistance())
                && (canvas.getHorizontalDotDistance() == canvas.getVerticalDotDistance())
        );
    }
     */
}