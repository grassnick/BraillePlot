package de.tudresden.inf.mci.brailleplot.rendering;

class Raster {

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

    Raster(int cellsWide, int cellsHigh, int rowsPerCell, int columnsPerCell, double verticalDotDistance,
                  double horizontalDotDistance, double verticalCellDistance, double horizontalCellDistance
    ){
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

    public int getHorizontalCellCount() {
        return mCellsWide;
    }
    public int getVerticalCellCount() {
        return mCellsHigh;
    }
    public int getVerticalCellSize() {
        return mRowsPerCell;
    }
    public int getHorizontalCellSize() {
        return mColumnsPerCell;
    }
    public int getRowCount() {
        return mRowCount;
    }
    public int getColumnCount() {
        return mColumnCount;
    }
    public double getRasterHeight() {
        return mHeight;
    }
    public double getRasterWidth() {
        return mWidth;
    }

}
