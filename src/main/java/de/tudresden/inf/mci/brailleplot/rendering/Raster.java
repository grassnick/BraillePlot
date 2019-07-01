package de.tudresden.inf.mci.brailleplot.rendering;

/**
 * Raster. Represents a raster for rasterizing.
 * @author Leonard Kupper
 * @version 2019.06.28
 */
public abstract class Raster {

    private int mCellsWide;
    private int mCellsHigh;
    private int mRowsPerCell;
    private int mColumnsPerCell;
    private double mVerticalDotDistance;
    private double mHorizontalDotDistance;
    private double mVerticalCellDistance;
    private double mHorizontalCellDistance;

    private int mRowCount;
    private int mColumnCount;
    private double mWidth;
    private double mHeight;

    Raster(final int cellsWide, final int cellsHigh, final int rowsPerCell, final int columnsPerCell, final double verticalDotDistance,
                  final double horizontalDotDistance, final double verticalCellDistance, final double horizontalCellDistance
    ) {
        mCellsWide = cellsWide;
        mCellsHigh = cellsHigh;
        mRowsPerCell = rowsPerCell;
        mColumnsPerCell = columnsPerCell;
        mVerticalDotDistance = verticalDotDistance;
        mHorizontalDotDistance = horizontalDotDistance;
        mVerticalCellDistance = verticalCellDistance;
        mHorizontalCellDistance = horizontalCellDistance;

        mRowCount = mCellsWide * mRowsPerCell;
        mColumnCount = mCellsHigh * mRowsPerCell;
        mHeight =  (mRowsPerCell - 1) * mVerticalDotDistance * mCellsHigh + (mCellsHigh - 1) * mVerticalCellDistance;
        mWidth =  (mColumnsPerCell - 1) * mHorizontalDotDistance * mCellsWide + (mCellsWide - 1) * mHorizontalCellDistance;
    }

    public final int getHorizontalCellCount() {
        return mCellsWide;
    }
    public final int getVerticalCellCount() {
        return mCellsHigh;
    }
    public final int getVerticalCellSize() {
        return mRowsPerCell;
    }
    public final int getHorizontalCellSize() {
        return mColumnsPerCell;
    }
    public final int getRowCount() {
        return mRowCount;
    }
    public final int getColumnCount() {
        return mColumnCount;
    }
    public final double getRasterHeight() {
        return mHeight;
    }
    public final double getRasterWidth() {
        return mWidth;
    }

}
