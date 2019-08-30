package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.LineChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2D;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import static de.tudresden.inf.mci.brailleplot.rendering.Axis.Type.X_AXIS;
import static java.lang.Math.*;
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
        Rectangle xAxisArea = calculateXAxis();
        try {
            xAxisArea.removeFromLeft(2);
        } catch (Rectangle.OutOfSpaceException e) {
            e.printStackTrace();
        }
        // Space reserving, needs testing.
        addSpaceLeft();
        // Step three: Calculate the range of the x values (example: -5 - 1000)
        double rangeOfXValues = valueRangeOfXAxis();
        double negValueRangeSize = abs(min(mDiagram.getMinY(), 0));
        double posValueRangeSize = max(mDiagram.getMaxY(), 0);
        // Step three.5:  Calculate how many units are available for the x Axis. Units are in dots.
        int xUnitsAvailable = calculateUnitsWidthInCells(xAxisArea);
        int xStepWidth = (int) findXAxisStepWidth(rangeOfXValues, xUnitsAvailable);


        Rectangle xAxisBound = xAxisArea.scaledBy(mCanvas.getCellWidth(), mCanvas.getCellHeight());
        int originY = xAxisBound.intWrapper().getY();
        rasterizeLayout(mDiagramTitle, titleArea, xStepWidth,originY);



        double rangeOfYValues = valueRangeOfYAxis();
    }

    private void rasterizeLayout(String title, Rectangle titleArea, int stepWidthX, int originY) {
        BrailleText diagramTitle = new BrailleText(title, titleArea);
        try {
            mTextRasterizer.rasterize(diagramTitle, mCanvas);
        } catch (InsufficientRenderingAreaException e) {
            e.printStackTrace();
        }
        Axis xAxis = new Axis(Axis.Type.X_AXIS, 0, originY, stepWidthX*2, 3);
        //xAxis.setBoundary(yAxisBound);
        try {
            mAxisRasterizer.rasterize(xAxis, mCanvas);
        } catch (InsufficientRenderingAreaException e) {
            e.printStackTrace();
        }

    }

    // Works Cell based
    private Rectangle calculateTitle() throws InsufficientRenderingAreaException {
        if (mDiagramTitle.isEmpty()) {
            throw new IllegalArgumentException("The title in LineChartRasterizer was empty!");
        }
        int widthOfCompleteArea = mCellLineArea.intWrapper().getWidth();
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

    /**
     * Calculate width, measured in cells.
     * @param rectangle The cell rectangle which you want to know the width.
     * @return Width in cells.
     */
    private int calculateUnitsWidthInCells(final Rectangle rectangle) {
        return (int) rectangle.getWidth();
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
            valueRangeOfXAxis = maxX - minX + 1;
        } else {
            valueRangeOfXAxis = abs(maxX) + abs(minX) + 1;
        }
        return valueRangeOfXAxis;
    }

    private double findXAxisStepWidth(final double rangeOfXValues, final int xUnitsAvailable) throws InsufficientRenderingAreaException {
        int cellsToNextTick = (int) floor(xUnitsAvailable /  rangeOfXValues);
        int numberOfTicks = (int) ceil(xUnitsAvailable / cellsToNextTick) + 1;
        PointListContainer<PointList> data = mDiagram.getData();
        ArrayList listOfFloats = new ArrayList<Float>();
        Iterator<PointList> iter = data.iterator();
        while (iter.hasNext()) {
            PointList list = iter.next();
            Iterator<Point2DDouble> iter2 = list.getListIterator();
            while (iter2.hasNext()) {
                Point2DDouble value = iter2.next();
                listOfFloats.add(value.getX());
            }
            break;
        }
        testIfEquidistant(listOfFloats);

        return cellsToNextTick;



        // Test if every number has the same distance to the next number
        // If yes, try the distance as distance between tickmarks
        // If there are to many to fit in the maximum width, or if they dont have the same distance, then
        // try a mapping either:
        // Try so that the most values are on the tick marks,
        // Try so that atleas the maximum value is present ont the last tickmark.
        // TODO Auskommentieren, Ansatz funktioniert nicht.
       /* double minRange = rangeOfXValues / xUnitsAvailable;
        // Get the Number, represented as String (-5,23 for example) so that we can adjust the minimum space needed
        // between two ticks
        int lengthMax = String.valueOf(mDiagram.getMaxX()).length() + 1;
        int lengthMin = String.valueOf(mDiagram.getMinX()).length() + 1;
        int length = max(lengthMax, lengthMin);
        int maximumDistinguishableTicks = floorDiv(xUnitsAvailable, length);
        // For example: 3: 3   6   9   12 etc
        // or 2: 2   4   6 etc
        int unitJumps = (int) ceil((rangeOfXValues) / maximumDistinguishableTicks);
        int numberOfTicks = (int) ceil(rangeOfXValues / unitJumps);
        int result = floorDiv(xUnitsAvailable, numberOfTicks);
        if(result <length) {
            throw new InsufficientRenderingAreaException("The area does not have enough space. For the biggest number to " +
                    "be written down on the axis, you would need: " + length + ". But the calculated distance was: " + result +".");
        }
        //LinearMappingAxisRasterizer rast = new LinearMappingAxisRasterizer(new Axis(X_AXIS,0,0,unitJumps,);
        return result;
        */
    }

    private void testIfEquidistant(ArrayList<Double> listOfFloats) {
        float distance;
        Double[] list = new Double[listOfFloats.size()];
        listOfFloats.<Double>toArray(list);
        for (int i = 0; i < listOfFloats.size()-1 ; i++) {
            double temp = list[i];

        }
    }
}
