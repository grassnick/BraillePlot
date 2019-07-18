package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;

import java.util.ArrayList;

import static java.lang.Math.*;

/**
 * Representation of a target onto which an image can be rasterized.
 * It wraps a {@link de.tudresden.inf.mci.brailleplot.printabledata.MatrixData} instance and describes the raster size and its (not necessarily equidistant) layout.
 * @author Leonard Kupper
 * @version 2019.07.12
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
    private int mCellWidth; // dots
    private int mCellHeight; // dots
    private double mCellHorizontalMM; // millimeters
    private double mCellVerticalMM; // millimeters

    // Spacing
    private double mHorizontalDotDistance;
    private double mVerticalDotDistance;
    private double mHorizontalCellDistance;
    private double mVerticalCellDistance;
    private double mDotDiameter;

    // Printing area rectangles
    private Rectangle mPrintingAreaCells;

    AbstractRasterCanvas(final Printer printer, final Format format, final int cellWidth, final int cellHeight)
            throws InsufficientRenderingAreaException {

        super(printer, format);

        // Cell size in dots
        mCellWidth = cellWidth;
        mCellHeight = cellHeight;

        readConfig();
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
        if (mPageContainer.size() < 1) {
            return getNewPage();
        }
        return (MatrixData<Boolean>) mPageContainer.get(mPageContainer.size() - 1);
    }

    private void readConfig() {

        // What are the dot and cell distances in mm?
        mHorizontalDotDistance = mPrinter.getProperty("raster.dotDistance.horizontal").toDouble();
        mVerticalDotDistance = mPrinter.getProperty("raster.dotDistance.vertical").toDouble();
        mHorizontalCellDistance = mPrinter.getProperty("raster.cellDistance.horizontal").toDouble();
        mVerticalCellDistance = mPrinter.getProperty("raster.cellDistance.vertical").toDouble();
        mDotDiameter = mPrinter.getProperty("raster.dotDiameter").toDouble();

        // Calculate cell size in mm
        mCellHorizontalMM = mHorizontalDotDistance * (mCellWidth - 1) + mHorizontalCellDistance; // Full width of one cell + padding in mm
        mCellVerticalMM = mVerticalDotDistance * (mCellHeight - 1) + mVerticalCellDistance; // Full height of one cell + padding in mm

        // Now we are reading properties that are specific to rasterizing.
        // The abstract parent class (AbstractCanvas) already calculated indentations based on millimeters, but it is
        // also possible to set a raster.indentation counted in amount of cells and lines. Those must be removed additionally.
        double indentTop = mPrinter.getProperty("raster.indent.top").toDouble() * mCellVerticalMM;
        double indentLeft = mPrinter.getProperty("raster.indent.left").toDouble() * mCellHorizontalMM;
        double indentBottom = mPrinter.getProperty("raster.indent.bottom").toDouble() * mCellVerticalMM;
        double indentRight = mPrinter.getProperty("raster.indent.right").toDouble() * mCellHorizontalMM;

        // Subtract additional raster indentation from virtual margin.
        mMarginTop = max(mMarginTop - indentTop, 0);
        mMarginLeft = max(mMarginLeft - indentLeft, 0);
        mMarginBottom = max(mMarginBottom - indentBottom, 0);
        mMarginRight = max(mMarginRight - indentRight, 0);

        // Subtract additional raster indentation from page size.
        mMillimeterWidth = max(mMillimeterWidth - (indentLeft + indentRight), 0);
        mMillimeterHeight = max(mMillimeterHeight - (indentTop + indentBottom), 0);

    }

    private void calculateRasterSize() throws InsufficientRenderingAreaException {

        // Calculate how many rows and columns of full cells fit inside the given page area
        mHorizontalCellCount = (int) floor((mMillimeterWidth + mHorizontalCellDistance) / mCellHorizontalMM); // How many full cells fit horizontally?
        mVerticalCellCount = (int) floor((mMillimeterHeight + mVerticalCellDistance) / mCellVerticalMM); // How many full cells fit vertically?

        // To how many dots does this raster size correspond?
        mColumnCount = mHorizontalCellCount * mCellWidth;
        mRowCount = mVerticalCellCount * mCellHeight;

        /*
        // How big is the raster on the page?
        double fullHorizontalMM = (mHorizontalCellCount * cellHorizontalMM) - mHorizontalCellDistance;
        double fullVerticalMM = (mVerticalCellCount * cellVerticalMM) - mVerticalCellDistance;
         */

        // Calculate a 'restricted' printing area rectangle, based on the given virtual margins.
        // By omitting some cells from the printing area rectangle, a real margin is imitated.
        // A rasterizer working with this canvas can still force to write values outside the restricted rectangle
        // so it is expected to at least take a look at the cell rectangle.

        mPrintingAreaCells = new Rectangle(0, 0,mHorizontalCellCount,mVerticalCellCount); // The whole area.
        int omitTop = (int) ceil(mMarginTop / mCellVerticalMM);
        int omitLeft = (int) ceil(mMarginLeft / mCellHorizontalMM);
        int omitBottom = (int) ceil(mMarginBottom / mCellVerticalMM);
        int omitRight = (int) ceil(mMarginRight / mCellHorizontalMM);
        try {
            mPrintingAreaCells.removeFromTop(omitTop);
            mPrintingAreaCells.removeFromLeft(omitLeft);
            mPrintingAreaCells.removeFromBottom(omitBottom);
            mPrintingAreaCells.removeFromRight(omitRight);
        } catch (Rectangle.OutOfSpaceException e) {
            // If an OutOfSpaceException is thrown this early, it basically means that the defined page size is smaller
            // than the sum of its margins, indicating heavy layer 8 error.
            throw new InsufficientRenderingAreaException("The defined page size is smaller than the sum of its margins", e);
        }


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

    public final int getCellWidth() {
        return mCellWidth;
    }
    public final int getCellHeight() {
        return mCellHeight;
    }

    /*
    TODO: Maybe remove these 4 getters, they are currently not used and could mislead to write into the margin. Rasterizers should use the cell and dot rectangle instead.
    public final int getColumnCount() {
        return mColumnCount;
    }
    public final int getRowCount() {
        return mRowCount;
    }
    public final int getHorizontalCellCount() {
        return mHorizontalCellCount;
    }
    public final int getVerticalCellCount() {
        return mVerticalCellCount;
    }
    */
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
    public final double getDotDiameter() {
        return mDotDiameter;
    }
    public final Rectangle getCellRectangle() {
        return new Rectangle(mPrintingAreaCells);
    }
    public final Rectangle getDotRectangle() {
        return toDotRectangle(mPrintingAreaCells);
    }
    public final Rectangle toDotRectangle(Rectangle cellRectangle) {
        return cellRectangle.scaledBy(mCellWidth, mCellHeight);
    }

    @Override
    public double getPrintableWidth() {
        return mXPositions.get(getDotRectangle().intWrapper().getRight()) - mXPositions.get(getDotRectangle().intWrapper().getX());
    }

    @Override
    public double getPrintableHeight() {
        return mYPositions.get(getDotRectangle().intWrapper().getBottom()) - mYPositions.get(getDotRectangle().intWrapper().getY());
    }

    /**
     * Returns the x coordinate (counted in cells) of the cell containing the dot with given x coordinate (counted in dots).
     * @param dotX The dot x coordinate. In other words its columns number.
     * @return The cell x coordinate. In other words the cells columns number.
     */
    public int getCellXFromDotX(final int dotX) {
        return dotX / mCellWidth;
    }
    /**
     * Returns the y coordinate (counted in cells) of the cell containing the dot with given y coordinate (counted in dots).
     * @param dotY The dot y coordinate. In other words its rows number.
     * @return The cell y coordinate. In other words the cells columns number.
     */
    public int getCellYFromDotY(final int dotY) {
        return dotY / mCellHeight;
    }


    public int quantifyX(double unquantifiedMillimeterX) {
        return findClosestValueIndex(unquantifiedMillimeterX, mXPositions);
    }

    public int quantifyY(double unquantifiedMillimeterY) {
        return findClosestValueIndex(unquantifiedMillimeterY, mYPositions);
    }

    private int findClosestValueIndex(Double value, ArrayList<Double> list) {
        double minDistance = Double.POSITIVE_INFINITY;
        for (int index = 0; index < list.size(); index++) {
            double distance = abs(list.get(index) - value);
            if (distance < minDistance) {
                minDistance = distance;
            } else {
                // possible, because we know that the positions are sorted.
                return index - 1;
            }

        }
        // last value is the closest.
        return (list.size() - 1);
    }
}
