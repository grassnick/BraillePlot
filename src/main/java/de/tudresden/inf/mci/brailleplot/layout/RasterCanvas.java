package de.tudresden.inf.mci.brailleplot.layout;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;

/**
 * Representation of a target onto which an image can be rasterized.
 * It wraps a {@link de.tudresden.inf.mci.brailleplot.printabledata.MatrixData} instance and describes the raster size and its (not necessarily equidistant) layout.
 * @author Leonard Kupper, Georg Graßnick
 * @version 2019.08.26
 */
public class RasterCanvas extends AbstractCanvas<MatrixData<Boolean>> {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

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

    private int mRasterConstraintLeft; // cells
    private int mRasterConstraintTop; // cells

    /**
     * Constructor. Creates a new RasterCanvas, which is a canvas that represents it pages as instances of
     * {@link MatrixData} and holds information about the layout and spacing of the underlying raster grid.
     * The described grid is build from uniform 'cells' consisting of a variable amount of dots.
     * It is used as a target on which can be drawn by a {@link de.tudresden.inf.mci.brailleplot.rendering.Rasterizer}.
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
        mLogger.trace("Cell size set to {}x{}", mCellWidth, mCellHeight);

        readConfig();
        calculateRasterSize();
        calculateSpacing();

        mLogger.info("New RasterCanvas created from config: {}, {}", printer, format);
    }

    public final MatrixData<Boolean> getNewPage() {
        mPageContainer.add(new SimpleMatrixDataImpl<Boolean>(mPrinter, mFormat, mRowCount, mColumnCount, false));
        return getCurrentPage();
    }


    private void readConfig() {

        mLogger.trace("Reading raster specific configuration");

        // What are the dot and cell distances in mm?
        mHorizontalDotDistance = mPrinter.getProperty("raster.dotDistance.horizontal").toDouble();
        mVerticalDotDistance = mPrinter.getProperty("raster.dotDistance.vertical").toDouble();
        mHorizontalCellDistance = mPrinter.getProperty("raster.cellDistance.horizontal").toDouble();
        mVerticalCellDistance = mPrinter.getProperty("raster.cellDistance.vertical").toDouble();

        // Calculate cell size in mm
        mCellHorizontalMM = mHorizontalDotDistance * (mCellWidth - 1) + mHorizontalCellDistance; // Full width of one cell + padding in mm
        mCellVerticalMM = mVerticalDotDistance * (mCellHeight - 1) + mVerticalCellDistance; // Full height of one cell + padding in mm

        mRasterConstraintTop = mPrinter.getProperty("raster.constraint.top").toInt();
        mRasterConstraintLeft = mPrinter.getProperty("raster.constraint.left").toInt();
    }

    private void calculateRasterSize() throws InsufficientRenderingAreaException {

        // New approach using a box model:

        mLogger.trace("Fitting raster into available printing area");
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
        mLogger.trace("Determined cellRasterBox: {}", cellRasterBox);

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
        mLogger.trace("Determined rasterConstraintBox: {}", rasterConstraintBox);

        mPrintingAreaCells = calculatePrintingArea(cellRasterBox, rasterConstraintBox);
        mLogger.trace("Determined printable raster: {}", mPrintingAreaCells);

        // The following values are set to keep track of the 'real' size of the internal data representation, because
        // the margins are created virtually by printing some empty cells at the pages top / left edge.
        // Rasterizers are only presented with a sub-area rectangle, representing the valid printing area.

        // How many rows and columns of full cells fit inside the given page area (ignoring margins and raster constraints)
        mHorizontalCellCount = mPrintingAreaCells.intWrapper().getRight() + 1; // How many full cells fit horizontally?
        mVerticalCellCount = mPrintingAreaCells.intWrapper().getBottom() + 1; // How many full cells fit vertically?

        // To how many dots does this raster size correspond?
        mPrintingAreaDots = toDotRectangle(mPrintingAreaCells);
        // X and Y must be added to the size because the margins are created virtually by leaving these cells empty.
        // They have to be contained in the data representation.
        mColumnCount = mPrintingAreaDots.intWrapper().getX() + mPrintingAreaDots.intWrapper().getWidth();
        mRowCount = mPrintingAreaDots.intWrapper().getY() + mPrintingAreaDots.intWrapper().getHeight();
        mLogger.trace("Determined raster dimensions (dots): {} columns x {} rows", mColumnCount, mRowCount);

    }

    private void calculateSpacing() {

        mLogger.trace("Pre calculating quantified raster positions");
        mXPositions = calculateQuantizedPositions(mHorizontalDotDistance, mHorizontalCellDistance, mCellWidth, mHorizontalCellCount);
        mLogger.trace("X coordinates: {}", mXPositions);
        mYPositions = calculateQuantizedPositions(mVerticalDotDistance, mVerticalCellDistance, mCellHeight, mVerticalCellCount);
        mLogger.trace("Y coordinates: {}", mYPositions);

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

    public final int getRasterConstraintLeft() {
        return mRasterConstraintLeft;
    }

    public final int getRasterConstraintTop() {
        return mRasterConstraintTop;
    }

    /**
     * Returns the full constraint of the printable Area from the left in mm.
     * @return The margin to the left of the paper in mm, where printing is not possible.
     */
    public final double getFullConstraintLeft() {
        return getRasterConstraintLeft() * mCellHorizontalMM + getConstraintLeft();
    }

    /**
     * Returns the full constraint of the printable Area from the top in mm.
     * @return The margin to the top of the paper in mm, where printing is not possible.
     */
    public final double getFullConstraintTop() {
        return getRasterConstraintTop() * mCellVerticalMM + getConstraintTop();
    }

    /**
     * Get the X coordinates of all dots.
     * @return The X coordinates of all dots in mm.
     */
    public final List<Double> getXPositions() {
        return Collections.unmodifiableList(mXPositions);
    }

    /**
     * Get the Y coordinates of all dots.
     * @return The Y coordinates of all dots in mm.
     */
    public final List<Double> getYPositions() {
        return Collections.unmodifiableList(mYPositions);
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
