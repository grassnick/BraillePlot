package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;

/**
 * Representation of a target onto which an image can be rasterized.
 * It wraps a {@link de.tudresden.inf.mci.brailleplot.printabledata.MatrixData} instance and describes the raster size and its (not necessarily equidistant) layout.
 * @author Leonard Kupper
 * @version 2019.07.22
 */
public class RasterCanvas extends AbstractCanvas {

    private ArrayList<Double> mXPositions;
    private ArrayList<Double> mYPositions;

    // Raster size
    private int mHorizontalCellCount;
    private int mVerticalCellCount;
    private int mColumnCount;
    private int mRowCount;

    // Printing area rectangle
    private Rectangle mPrintingAreaCells;
    private Rectangle mPrintingAreaDots;

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

    /**
     * Constructor. Creates a new RasterCanvas, which is a canvas that represents it pages as instances of
     * {@link MatrixData} and holds information about the layout and spacing of the underlying raster grid.
     * The described grid is build from uniform 'cells' consisting of a variable amount of dots.
     * It is used as a target on which can be drawn by a {@link Rasterizer}.
     * @param printer The {@link Printer} configuration to be used.
     * @param format The {@link Format} configuration to be used.
     * @param cellWidth The horizontal count of dots in a cell.
     * @param cellHeight The vertical count of dots in a cell.
     * @throws InsufficientRenderingAreaException If the given configuration leads to an printable area of negative
     * size or zero size, e.g. if the sum of defined margins and constraints adds up to be greater than the original page size.
     */
    RasterCanvas(final Printer printer, final Format format, final int cellWidth, final int cellHeight)
            throws InsufficientRenderingAreaException {

        super(printer, format);

        // Cell size in dots
        mCellWidth = cellWidth;
        mCellHeight = cellHeight;

        readConfig();
        calculateRasterSize();
        calculateSpacing();

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

    }

    private void calculateRasterSize() throws InsufficientRenderingAreaException {

        // New approach using a box model:

        // Dividing the printable area into cells to create a cell raster box.
        int cellRasterX = (int) ceil(mPrintableArea.getX() / mCellHorizontalMM);
        int cellRasterY = (int) ceil(mPrintableArea.getY() / mCellVerticalMM);
        int cellRasterR = (int) floor((mPrintableArea.getRight() + mHorizontalCellDistance) / mCellHorizontalMM);
        int cellRasterB = (int) floor((mPrintableArea.getBottom() + mVerticalCellDistance) / mCellVerticalMM);
        Rectangle cellRasterBox = new Rectangle(
                cellRasterX, cellRasterY,
                cellRasterR - cellRasterX,
                cellRasterB - cellRasterY
        );

        // The following properties impact the printing area, but are specific to rasterizing. (That's why they weren't read before in the AbstractCanvas)
        // The abstract parent class (AbstractCanvas) already calculated indentations based on millimeters, but it is
        // also possible to set a raster.indentation counted in amount of cells and lines. Those must be removed additionally.

        // Create a raster constraint box
        int rasterConstraintTop = mPrinter.getProperty("raster.constraint.top").toInt();
        int rasterConstraintLeft = mPrinter.getProperty("raster.constraint.left").toInt();
        int rasterConstraintHeight, rasterConstraintWidth;
        if (mPrinter.getPropertyNames().contains("raster.constraint.height")) {
            rasterConstraintHeight = mPrinter.getProperty("raster.constraint.height").toInt();
        } else {
            rasterConstraintHeight = Integer.MAX_VALUE;
        }
        if (mPrinter.getPropertyNames().contains("raster.constraint.width")) {
            rasterConstraintWidth = mPrinter.getProperty("raster.constraint.width").toInt();
        } else {
            rasterConstraintWidth = Integer.MAX_VALUE;
        }
        Rectangle rasterConstraintBox = new Rectangle(rasterConstraintLeft, rasterConstraintTop,
                rasterConstraintWidth, rasterConstraintHeight);

        mPrintingAreaCells = calculatePrintingArea(cellRasterBox, rasterConstraintBox);

        // The following values are set to keep track of the 'real' size of the internal data representation, because
        // the margins are created virtually by printing some empty cells at the pages top / left edge.
        // Rasterizers are only presented with a sub-area rectangle, representing the valid printing area.

        // How many rows and columns of full cells fit inside the given page area (ignoring margins and raster constraints)
        mHorizontalCellCount = mPrintingAreaCells.intWrapper().getRight() + 1; // How many full cells fit horizontally?
        mVerticalCellCount = mPrintingAreaCells.intWrapper().getBottom() + 1; // How many full cells fit vertically?

        // To how many dots does this raster size correspond?
        mPrintingAreaDots = toDotRectangle(mPrintingAreaCells);
        mColumnCount = mPrintingAreaDots.intWrapper().getWidth();
        mRowCount = mPrintingAreaDots.intWrapper().getHeight();


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
        return mPrintingAreaDots;
    }
    public final Rectangle toDotRectangle(final Rectangle cellRectangle) {
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


    public final int quantifyX(final double unquantifiedMillimeterX) {
        return findClosestValueIndex(unquantifiedMillimeterX, mXPositions);
    }

    public final int quantifyY(final double unquantifiedMillimeterY) {
        return findClosestValueIndex(unquantifiedMillimeterY, mYPositions);
    }

    private int findClosestValueIndex(final Double value, final ArrayList<Double> list) {
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
