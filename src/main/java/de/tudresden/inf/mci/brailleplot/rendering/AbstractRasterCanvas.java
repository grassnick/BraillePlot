package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;

import java.util.ArrayList;

import static java.lang.Math.floor;

/**
 * Representation of a target onto which an image can be rasterized.
 * It wraps a {@link de.tudresden.inf.mci.brailleplot.printabledata.MatrixData} instance and describes the raster size and its (not necessarily equidistant) layout.
 */
public abstract class AbstractRasterCanvas extends AbstractCanvas {

    private ArrayList<Double> mXPositions;
    private ArrayList<Double> mYPositions;

    // Raster size
    private int mHorizontalCellCount;
    private int mVerticalCellCount;
    private int mColumnCount;
    private int mRowCount;

    // Cell size
    private int mCellWidth;
    private int mCellHeight;

    // Spacing
    private double mHorizontalDotDistance;
    private double mVerticalDotDistance;
    private double mHorizontalCellDistance;
    private double mVerticalCellDistance;

    AbstractRasterCanvas(final Printer printer, final Format format, final int cellWidth, final int cellHeight) {

        super(printer, format);

        mCellWidth = cellWidth;
        mCellHeight = cellHeight;

        readRasterConfig();
        calculateRasterSize();
        calculateSpacing();

        System.out.println(mXPositions.toString());
        System.out.println(mYPositions.toString());

    }

    public final MatrixData<Boolean> getNewPage() {
        mPageContainer.add(new SimpleMatrixDataImpl<Boolean>(mPrinter, mFormat, mRowCount, mColumnCount, false));
        return getCurrentPage();
    }

    @SuppressWarnings("unchecked")
    // This is allowed because the mPageContainer fields are always initialized with the correct type by the page getters,
    // cannot be accessed from the outside and are never changed anywhere else.
    public final MatrixData<Boolean> getCurrentPage() {
        if (mPageContainer.size() < 1)
            return getNewPage();
        return (MatrixData<Boolean>) mPageContainer.get(mPageContainer.size() - 1);
    }

    private void readRasterConfig() {

        // how big is the full printable area in mm?
        mMillimeterWidth = mFormat.getProperty("page.width").toInt() - (mFormat.getProperty("margin.left").toInt() + mFormat.getProperty("margin.right").toInt());
        mMillimeterHeight = mFormat.getProperty("page.height").toInt() - (mFormat.getProperty("margin.top").toInt() + mFormat.getProperty("margin.bottom").toInt());

        // what are the dot and cell distances in mm?
        mHorizontalDotDistance = mFormat.getProperty("raster.dotDistance.horizontal").toDouble();
        mVerticalDotDistance = mFormat.getProperty("raster.dotDistance.vertical").toDouble();
        mHorizontalCellDistance = mFormat.getProperty("raster.cellDistance.horizontal").toDouble();
        mVerticalCellDistance = mFormat.getProperty("raster.cellDistance.vertical").toDouble();

    }

    private void calculateRasterSize() {

        // Calculate cell sizes in mm
        double cellHorizontalMM = mHorizontalDotDistance * (mCellWidth - 1) + mHorizontalCellDistance; // Full width of one cell + padding in mm
        double cellVerticalMM = mVerticalDotDistance * (mCellHeight - 1) + mVerticalCellDistance; // Full height of one cell + padding in mm

        // Calculate how many rows and columns of full cells fit inside the given printing area
        mHorizontalCellCount = (int) floor((mMillimeterWidth + mHorizontalCellDistance) / cellHorizontalMM); // How many full cells fit horizontally?
        mVerticalCellCount = (int) floor((mMillimeterHeight + mVerticalCellDistance) / cellVerticalMM); // How many full cells fit vertically?

        // To how many dots does this raster size correspond?
        mColumnCount = mHorizontalCellCount * mCellWidth;
        mRowCount = mVerticalCellCount * mCellHeight;
    }

    private void calculateSpacing() {

        mXPositions = calculateQuantizedPositions(mHorizontalDotDistance, mHorizontalCellDistance, mCellWidth, mHorizontalCellCount);
        mYPositions = calculateQuantizedPositions(mVerticalDotDistance, mVerticalCellDistance, mCellHeight, mVerticalCellCount);

    }

    private ArrayList<Double> calculateQuantizedPositions(
            final double dotSpacing,
            final double cellSpacing,
            final int cellSize,
            final int cellCount
    ) {
        ArrayList<Double> positions = new ArrayList<>();
        double position = 0;
        for (int i = 0; i < cellCount; i++) {
            for (int j = 0; j < cellSize; j++) {
                positions.add(position);
                if (j < (cellSize - 1)) {
                    position += dotSpacing;
                }
            }
            position += cellSpacing;
        }
        return positions;
    }

    // TODO: much more getters
    public final int getColumnCount() {
        return mColumnCount;
    }
    public final int getRowCount() {
        return mRowCount;
    }
    public final int getCellWidth() {
        return mCellWidth;
    }
    public final int getCellHeight() {
        return mCellHeight;
    }
    public final int getHorizontalCellCount() {
        return mHorizontalCellCount;
    }
    public final int getVerticalCellCount() {
        return mVerticalCellCount;
    }
    public final double getHorizontalDotDistance() {
        return mHorizontalDotDistance;
    }
    public final double getVerticalDotDistance() {
        return mVerticalDotDistance;
    }
    public final double getHorizontalCellDistance() {
        return mHorizontalCellDistance;
    }
    public final double getVerticalCellDistance() {
        return mVerticalCellDistance;
    }
    public final Rectangle getCellRectangle() {
        return new Rectangle(0,0,getHorizontalCellCount(),getVerticalCellCount());
    }
    public final Rectangle getDotRectangle() {
        return new Rectangle(0,0,getColumnCount(),getRowCount());
    }

    @Override
    public double getAbsoluteWidth() {
        return mXPositions.get(mColumnCount - 1);
    }

    @Override
    public double getAbsoluteHeight() {
        return mYPositions.get(mRowCount - 1);
    }

    public int getCellXFromDotX(final int dotX) {
        return dotX / mCellWidth;
    }

    public int getCellYFromDotY(final int dotY) {
        return dotY / mCellHeight;
    }

}
