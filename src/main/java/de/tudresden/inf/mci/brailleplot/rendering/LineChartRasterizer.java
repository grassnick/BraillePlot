package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.LineChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import static java.lang.Math.abs;
import static java.lang.StrictMath.floor;

/**
 * Class representing a line chart rasterizer.
 * @author Andrey Ruzhanskiy
 * @version 2019.08.17
 */
public class LineChartRasterizer implements Rasterizer<LineChart> {
    private MatrixData mData;
    private LineChart mDiagram;
    private RasterCanvas mCanvas;
    private BrailleTextRasterizer mTextRasterizer;
    private LinearMappingAxisRasterizer mAxisRasterizer;


    /*
        Parameters which should be read somewhere, not be hardcoded.
        For the time being, these constants will be used.
     */
    private String mDiagramTitle =  "I am a line chart";
    private String mXAxisUnit = "Units per Memes";
    private String mYAxisUnit = "Pepes per Wojacks";


    // Layout Variables

    private Rectangle mCellLineArea;

    public LineChartRasterizer() {
        mTextRasterizer = new BrailleTextRasterizer();
        mAxisRasterizer = new LinearMappingAxisRasterizer();
    }

    @Override
    public void rasterize(final LineChart data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        if (data.equals(null)) {
            throw new NullPointerException("The given data for the LineChartRasterizer was null!");
        }
        if (canvas.equals(null)) {
            throw new NullPointerException("The given canvas for the LineChartRasterizer was null!");
        }

        PointListContainer list =  data.getData();
        mCanvas = canvas;
        mDiagram = data;
        mData = mCanvas.getCurrentPage();
        // Important: Its a cell rectangle, not a dot rectangle.
        mCellLineArea = mCanvas.getCellRectangle();

        // Step one: Calculate area needed for Title.
        Rectangle titleArea = calculateTitle();
        // Step two: Calculate area needed for the x axis.
        Rectangle xAxisAreas = calculateXAxis();
        // Space reserving, needs testing.
        addSpaceLeft();
        // Step three: Calculate the range of the x values (example: -5 - 1000)
        double rangeOfXValues = valueRangeOfXAxis();
        // Step three.5:  Calculate how many units are available for the x Axis. Units are in dots.
        int xUnitsAvailable = calculateUnitsWidthInDots(xAxisAreas);
        findXAxisScaling(rangeOfXValues, xUnitsAvailable);




        double rangeOfYValues = valueRangeOfYAxis();
    }

    private Rectangle calculateTitle() throws InsufficientRenderingAreaException {
        if (mDiagramTitle.isEmpty()) {
            throw new IllegalArgumentException("The title in LineChartRasterizer was empty!");
        }
        int widthOfCompleteArea = mCellLineArea.intWrapper().getWidth() * mCanvas.getCellWidth();
        int titleBarHeight = mTextRasterizer.calculateRequiredHeight(mDiagramTitle, 0, 0, widthOfCompleteArea, mCanvas);
        try {
            return  mCellLineArea.removeFromTop(mCanvas.getCellYFromDotY(titleBarHeight));
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space to build the title area for the line chart!");
        }
    }

    private Rectangle calculateXAxis() throws InsufficientRenderingAreaException {
        try {
            return mCellLineArea.removeFromBottom(2);
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space to build the X-Axis for the line chart!");
        }
    }

    // Please shut up checkstyle
    @SuppressWarnings("checkstyle:MagicNumber")
    private void addSpaceLeft() throws InsufficientRenderingAreaException {
        try {
            mCellLineArea.removeFromLeft(6);
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough enough space to the left for the line chart!");
        }
    }

    private double valueRangeOfYAxis() {
        double minY = mDiagram.getMinY();
        double maxY = mDiagram.getMaxY();

        double valueRangeOfYAxis;


        //Needs testing
        if (minY >= 0) {
            valueRangeOfYAxis = maxY - minY;
        } else {
            valueRangeOfYAxis = abs(maxY) + abs(minY);
        }
        return valueRangeOfYAxis;
    }

    /**
     * Calculate the width, measured in dots.
     * Important: Because the rectangle width is measured in doubles, we need to round off the product of
     *            the width and the cellwidth.
     * Important: Only use it with CellRectangles
     * @param rectangle The cell rectangle which you want to know the width.
     * @return Width in dots.
     */
    private int calculateUnitsWidthInDots(final Rectangle rectangle) {
        return (int) floor(rectangle.getWidth() * mCanvas.getCellWidth());
    }

    private int calculateUnitsWidhtInCells(final Rectangle rectangle) {
        return 0;
    }

    private double valueRangeOfXAxis() {
        double minX = mDiagram.getMinX();
        double maxX = mDiagram.getMaxX();

        double valueRangeOfXAxis;
        //Needs testing
        if (minX >= 0) {
            valueRangeOfXAxis = maxX - minX;
        } else {
            valueRangeOfXAxis = abs(maxX) + abs(minX);
        }
        return valueRangeOfXAxis;
    }

    private double findXAxisScaling(final double rangeOfXValues, final int xUnitsAvailable) {
        // Divide range by units available, so that we can get a resolution
        double minRange = rangeOfXValues / xUnitsAvailable;
        // Get the Number, represented as String (-5,23 for example) so that we can adjust the minimum space needed
        // between two ticks
        int length = String.valueOf(mDiagram.getMaxX()).length();
        return 0;
    }
}
