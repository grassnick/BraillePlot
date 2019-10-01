package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.diagrams.LineChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static java.lang.StrictMath.floor;
import static java.lang.StrictMath.round;

/**
 * Class representing a line chart rasterizer.
 * @author Andrey Ruzhanskiy
 * @version 2019.09.24
 */
public class LineChartRasterizer implements Rasterizer<LineChart> {
    private LineChart mDiagram;
    private RasterCanvas mCanvas;

    private LiblouisBrailleTextRasterizer mTextRasterizer;
    private LinearMappingAxisRasterizer mAxisRasterizer;
    private Legend mLegend;
    private final double tenth = 0.1, fifth = 0.2, quarter = 0.25, half = 0.5;
    private final double[] mUnitScalings = new double[]{tenth, fifth, quarter, half, 1.0};

    private int mXStepWidth;
    private int mYStepWidth;
    private double mDpiX;
    private double mDpiY;
    private final int  mPaddingBetweenAxisTextAndDiagram = 3;
    private final int mPaddingXandYText = 1; 
    private Rectangle mCellLineArea;
    private boolean mPrintOnSamePaper = false; // If you want to print on the same paper, change this variable to true.



    LineChartRasterizer() {
        mAxisRasterizer = new LinearMappingAxisRasterizer();
    }

    /**
     * Method for rasterizing a {@link LineChart}-diagram.
     * This approach of an algorithm is minimaly adjustable by design, the algorithm for itself tries to find the best fitting
     * for the given dataset.
     * @param data The renderable representation.
     * @param canvas An instance of {@link RasterCanvas} representing the target for the rasterizer output.
     * @throws InsufficientRenderingAreaException If the data can not be rasterized due to shortcomings of the algorithm
     *                                              or because the data was too big.
     */
    @Override
    public void rasterize(final LineChart data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        if (data.equals(null)) {
            throw new NullPointerException("The given data for the LineChartRasterizer was null!");
        }
        if (canvas.equals(null)) {
            throw new NullPointerException("The given canvas for the LineChartRasterizer was null!");
        }
        mTextRasterizer = new LiblouisBrailleTextRasterizer(canvas.getPrinter());
        mLegend = new Legend(data.getTitle());

        mCanvas = canvas;
        mDiagram = data;
        // Important: Its a cell rectangle, not a dot rectangle.
        mCellLineArea = mCanvas.getCellRectangle();

        // ITS CALCULATION TIME //

        // Step one: Calculate area needed for the title.
        Rectangle titleArea = calculateTitle();
        Rectangle yAxisText;
        Rectangle xAxisText;
        try {
            yAxisText = canvas.toDotRectangle(mCellLineArea.removeFromTop(mPaddingXandYText));
            xAxisText = canvas.toDotRectangle(mCellLineArea.removeFromBottom(mPaddingXandYText));
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("The axis text cant fit to the layout.", e);
        }

        // Step two: Calculate area needed for the x/y axis.
        Rectangle xAxisArea = calculateXAxis();
        Rectangle yAxisArea = calculateYAxis();

        // Step three: Calculate various things needed for computing the most simple approach for the x axis.
        double rangeOfXValues = valueRangeOfXAxis();
        int xUnitsAvailable = calculateUnitsWidthInCells(xAxisArea);
        mDpiX = calculateDPI(rangeOfXValues, xUnitsAvailable);
        mXStepWidth = (int) findXAxisStepWidth(rangeOfXValues, xUnitsAvailable);
        int xNumberOfTicks = (int) getNumberOfTicks(xUnitsAvailable);
        Rectangle xAxisBound = xAxisArea.scaledBy(mCanvas.getCellWidth(), mCanvas.getCellHeight());  // Change to canvas convert toDotRectangle
        int originY = xAxisBound.intWrapper().getY();
        int originX = xAxisBound.intWrapper().getX();

        // Step four: Same thing for the y axis.
        Rectangle yAxisBound = yAxisArea.scaledBy(mCanvas.getCellWidth(), mCanvas.getCellHeight());
        int yOriginY = originY - 1; // Drawing the diagram so that the y = 0 lies not on the x axis
        int yOriginX = yAxisBound.intWrapper().getRight();
        double rangeOfYValues = valueRangeOfYAxis();
        int yUnitsAvailable = calculateUnitsHeightInCells(yAxisArea);
        mYStepWidth =  findYAxisStepWidth(rangeOfYValues, yUnitsAvailable);
        mDpiY = calculateDPI(rangeOfYValues, yUnitsAvailable);
        int yNumberOfTicks = (int) getNumberOfTicks(yUnitsAvailable);

        // Step five: Setting correct labels for x and y axis.
        Map<String, String> xLabelsForLegend = new TreeMap<>();
        Map<String, String> yLabelsForLegend = new TreeMap<>();
        Map<Integer, String> xLabels = setCorrectLabelsforX(rangeOfXValues, xNumberOfTicks, mDpiX, xLabelsForLegend);
        Map<Integer, String> yLabels = setCorrectLabelsforY(rangeOfYValues, yNumberOfTicks, mDpiY, yLabelsForLegend);

        // Step six: Filling the legend.
        mLegend.addSymbolExplanation("Achsenskalierung:", "X-Achse", "Faktor " + mDpiX);
        mLegend.addSymbolExplanation("Achsenskalierung:", "Y-Achse", "Faktor " + mDpiY);
        mLegend.setColumnViewTitle("Werte der Tickmarks");
        setLabelsXForLegend(xLabelsForLegend);
        // Currently commented out because the legendrasterizer cant handle that much legend.
        setLabelsYForLegend(yLabelsForLegend);

        // Step seven: Iterate through the lines, rasterize the axis for each paper.
        LegendRasterizer mLegendRasterizer = new LegendRasterizer();
        Iterator<PointList> iter  = mDiagram.getData().iterator();
        while (iter.hasNext()) {
            rasterizeTitle(data.getTitle(), titleArea);
            rasterizeXAxis(originY, originX, mXStepWidth, xAxisBound, xLabels);
            rasterizeYAxis(yOriginY, yOriginX, mYStepWidth, yAxisBound, yLabels);
            mTextRasterizer.rasterize(new BrailleText(data.getYAxisName(), yAxisText, BrailleLanguage.Language.DE_KURZSCHRIFT), mCanvas);
            mTextRasterizer.rasterize(new BrailleText(data.getXAxisName(), xAxisText, BrailleLanguage.Language.DE_KURZSCHRIFT), mCanvas);
            rasterizeData(mDiagram.getMinX(), mDiagram.getMinY(), iter.next());
            if (iter.hasNext() && !mPrintOnSamePaper) {
                mCanvas.getNewPage();
            }
        }
        // Last Step eight: Rasterize the legend (only needed one time).
        mLegendRasterizer.rasterize(mLegend, mCanvas);
    }

    // Various helper methods //

    /**
     * Method for setting the correct x-labels to the {@link Legend}.
     * Places first the value of the map and then the corresponding key.
     * @param labelsForLegend A map containing the values and the letters which will be put on the legend.
     */
    private void setLabelsXForLegend(final Map<String, String> labelsForLegend) {
        mLegend.addColumn("X-Achse", labelsForLegend);
    }

    /**
     * Method for setting the correct y-labels to the {@link Legend}.
     * Places first the value of the map and then the corresponding key.
     * @param labelsForLegend A map containing the values and the letters which will be put on the legend.
     */
    private void setLabelsYForLegend(final Map<String, String> labelsForLegend) {
        mLegend.addColumn("Y-Achse", labelsForLegend);
    }

    /**
     * Method for rasterizing the data inside a {@link LineChart}.
     * @param globalMinX The global minimum of the x values in the {@link LineChart}.
     * @param globalMinY The global minimum of the y values in the {@link LineChart}.
     * @param next The {@link PointList} containing the data for rasterization.
     */
    private void rasterizeData(final double globalMinX, final double globalMinY, final PointList next) {
        PointList sorted = next.sortXAscend();
        SimplePointListImpl points = rasterizePoints(sorted, globalMinX, globalMinY);
        Iterator<Point2DDouble> iter = points.getListIterator();
        Point2DDouble previous = null;
        while (iter.hasNext()) {
            Point2DDouble current = iter.next();
            if (previous == null) {
                previous = current;
                continue;
            }
            // Here you can swap bresenham to a new linerasterizing algorithm
            bresenham(previous.getX(), previous.getY(), current.getX(), current.getY());
            previous = current;
        }
    }

    /**
     * Bresenham algorithm for rasterizing lines.
     * Important: It translates the y coordinates to a normal coordinate-system. Currently, the Y-coordinate of the
     * {@link RasterCanvas} lies on the left upper corner, representing 0. But Bresenham assumes the Y-coordinate lies
     * in the left buttom corner. The difference is that the Y-Coordinate grows in a normal coordinate system as it lies
     * further and further above, but in a {@link RasterCanvas} it actually decreases as it goes further up.
     * Before setting the point on to the {@link RasterCanvas} it translates it back to the {@link RasterCanvas}-coordinate
     * system.
     * @param xStart X-coordinate of the startpoint.
     * @param yStart Y-coordinate of the startpoint.
     * @param xEnd X-coordinate of the endpoint.
     * @param yEnd Y-coordinate of the endpoint.
     */
    @SuppressWarnings("avoidinlineconditionals")
    private void bresenham(final Double xStart, final Double yStart, final Double xEnd, final Double yEnd) {
        int y0 = (int) (mCanvas.toDotRectangle(mCellLineArea).intWrapper().getHeight() - yStart);
        int y1 = (int) (mCanvas.toDotRectangle(mCellLineArea).intWrapper().getHeight() - yEnd);
        int x0 = (int) (xStart.doubleValue());
        int x1 = (int) (xEnd.doubleValue());
        int dx =  abs(x1 - x0);
        int dy = -abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;
        int e2;
        while (true) {
            mCanvas.getCurrentPage().setValue((int) (mCanvas.toDotRectangle(mCellLineArea).getHeight() - y0), (int) x0, true);
            if (x0 == x1 && y0 == y1) {
                break;
            }
            e2 = 2 * err;
            if (e2 > dy) {
                err += dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    /**
     * Method for rasterizing the points.
     * @param list A {@link PointList} containing points which will be rasterized.
     * @param globalMinX The global minimum of the x values in the {@link LineChart}.
     * @param globalMinY The global minimum of the y values in the {@link LineChart}.
     * @return The {@link SimplePointListImpl} containing the converted coordinates of the points.
     */
    private SimplePointListImpl rasterizePoints(final PointList list, final double globalMinX, final double globalMinY) {
        Objects.requireNonNull(list, "The given PointList for the rasterization of points was null!");
        double xMin = globalMinX;
        double yMin = globalMinY;
        Iterator<Point2DDouble> iter = list.getListIterator();
        Rectangle canvas = mCanvas.toDotRectangle(mCellLineArea);
        double canvasStartX = canvas.intWrapper().getX();
        double canvasStartY = canvas.intWrapper().getBottom();
        SimplePointListImpl result = new SimplePointListImpl();
        while (iter.hasNext()) {
            Point2DDouble current = iter.next();
            double currentValueX = current.getX() - xMin;
            double currentValueY = current.getY() - yMin;
            double stepX = currentValueX / mDpiX;
            double stepY = currentValueY / mDpiY;
            result.pushBack(new Point2DDouble(round(canvasStartX + mXStepWidth * mCanvas.getCellWidth() * stepX), round(canvasStartY - mYStepWidth * mCanvas.getCellHeight() * stepY)));
            mCanvas.getCurrentPage().setValue((int) round(canvasStartY - mYStepWidth * mCanvas.getCellHeight() * stepY), (int) round(canvasStartX + mXStepWidth * mCanvas.getCellWidth() * stepX), true);
        }
        result.calculateExtrema();
        return result;
    }


    /**
     * Method for creating a map containing the key-value pair for the datapoints for the y-axis. The key represents an integer, which has
     * no special meaning (but is needed for the {@link LinearMappingAxisRasterizer}, the value is a letter which will be
     * drawn on to the diagram. This same letter will appear on the legend with its representational value
     * (for example: 'a' -> 0.5 ).
     * @param rangeOfYValues The value range for the y datapoints.
     * @param numberOfTicks The number of ticks wich will be drawn on to the diagram.
     * @param dpi The resolution, or in other words, what one step for the tickmark along the axis means for the datapoints (for example 0.5 means
     *            for each tickmark the coordinatesystem where the datapoints lies is increased by 0.5)
     * @param yLabelsForLegend The map (can be empty, but must be initialized) in which the representation of the letters will be stored.
     *                         For example: 2.5 -> a, 3.0 -> b and so on.
     * @return A map containing the correct number of labels which will be needed to address all datapoints in {@link LineChart}.
     */
    @SuppressWarnings({"finalparameters", "magicnumber"})
    private Map<Integer, String> setCorrectLabelsforY(final double rangeOfYValues, final int numberOfTicks, double dpi, Map<String, String> yLabelsForLegend) {
        Objects.requireNonNull(yLabelsForLegend, "The given map for setting the correct labels for the y-axis was null!");
        double min = mDiagram.getData().getMinY();
        Map<Integer, String> result = new HashMap<>();
        double tmpDpi = dpi;

        // According to a not representative study the y axis should start with 'a' on the highest value, not the lowest.
        // So we need to calculate an offset and decrement the letter
        // Works currently only with letters represented in ASCII
        int datapoints = (int) ceil(rangeOfYValues / dpi);
        int range = 25; // Number of letters in the ASCII alphabet
        int offset = range - datapoints;
        byte z = 0x7A;
        byte letterAsByte = (byte) (z - offset);
        char letter = (char) letterAsByte;


        for (int i = 0; i < numberOfTicks; i++) {
            result.put(i, String.valueOf(letter));
            if (i == 0) {
                yLabelsForLegend.put(String.valueOf(letter), String.valueOf(min));
            } else {
                yLabelsForLegend.put(String.valueOf(letter), String.valueOf((dpi + min)));
                dpi = dpi + tmpDpi;
            }
            letter--;
            if (i >= datapoints) {
                break;
            }
        }
        return result;
    }

    /**
     * Calculates the resolution (meaning how much in the datapoint we go if we do one tickmark-step).
     * @param rangeOfValues The range of values in the {@link LineChart}.
     * @param unitsAvailable How many units (Braillecells) are available on the axis.
     * @return Double representing the resolution.
     */
    @SuppressWarnings("magicnumber")
    private double calculateDPI(final double rangeOfValues, final int unitsAvailable) {
        if (unitsAvailable < 0) {
            throw new RuntimeException("The units available were less then zero!");
        }
        double minRangePerUnit = rangeOfValues / unitsAvailable; // this range must fit into one 'axis step'
        double orderOfMagnitude = pow(10, ceil(log10(minRangePerUnit)));
        double scaledRangePerUnit = 0;
        for (double scaling : mUnitScalings) {
            scaledRangePerUnit = (scaling * orderOfMagnitude);
            if (scaledRangePerUnit >= minRangePerUnit) {
                break;
            }
        }
        return scaledRangePerUnit;
    }

    /**
     * Method for creating a map containing the key-value pair for the datapoints for the x-axis. The key represents an integer, which has
     * no special meaning (but is needed for the {@link LinearMappingAxisRasterizer}, the value is a letter which will be
     * drawn on to the diagram. This same letter will appear on the legend with its representational value
     * (for example: 'a' -> 0.5 ).
     * @param rangeOfXValues The value range for the y datapoints.
     * @param numberOfTicks The number of ticks wich will be drawn on to the diagram.
     * @param dpi The resolution, or in other words, what one step for the tickmark along the axis means for the datapoints (for example 0.5 means
     *            for each tickmark the coordinatesystem where the datapoints lies is increased by 0.5)
     * @param xLabelsForLegend The map (can be empty, but must be initialized) in which the representation of the letters will be stored.
     *                         For example: 2.5 -> a, 3.0 -> b and so on.
     * @return A map containing the correct number of labels which will be needed to address all datapoints in {@link LineChart}.
     */
    @SuppressWarnings("finalparameters")
    private Map<Integer, String> setCorrectLabelsforX(final double rangeOfXValues, final int numberOfTicks, double dpi, Map<String, String> xLabelsForLegend) {
        Objects.requireNonNull(xLabelsForLegend, "The given map to set the correct labels for the x-axis was null!");
        double min = mDiagram.getMinX();
        Map<Integer, String> result = new HashMap<>();
        double tmpDpi = dpi;
        char letter = 'a';
        double datapoints = rangeOfXValues / dpi;
        for (int i = 0; i < numberOfTicks; i++) {
            result.put(i, String.valueOf(letter));
            if (i == 0) {
                xLabelsForLegend.put(String.valueOf(letter), String.valueOf(min));
            } else {
                xLabelsForLegend.put(String.valueOf(letter), String.valueOf((dpi + min)));
                dpi = dpi + tmpDpi;
            }
            letter++;
            if (i >= datapoints) {
                break;
            }
        }
        return result;
    }

    /**
     * Method for the calculation of the stepwidth for the y-axis on the canvas.
     * Important: Not meant in the datapoints, but on the canvas. Currently returning 1.
     * @param rangeOfYValues The range of values along the y-axis.
     * @param yUnitsAvailable The number of available units along the y-axis, measured in braillecells.
     * @return Integer representing the number of braillecells between two tickmarks.
     */
    private int findYAxisStepWidth(final double rangeOfYValues, final int yUnitsAvailable) {
        // You can change the following step width to cater your needs.
        // The minimum int taken by the y-axis rasterizer is 1
        return 1;
    }

    /**
     * Method for calculating the height for a given {@link Rectangle} in braillecells.
     * @param rectangle The rectangle for which the height is computed.
     * @return Integer, representing the height in braillecells.
     */
    private int calculateUnitsHeightInCells(final Rectangle rectangle) {
        // Needed because one can get a height that encapsulates a fraction of a braillecell, so we need to ensure that
        // we work on whole cells.
        return (int) floor((rectangle.getHeight() * mCanvas.getCellHeight()) / mCanvas.getCellHeight());
    }

    /**
     * Method for rasterizing the title of the diagram.
     * @param title String which contains the title of the diagram.
     * @param titleArea The {@link Rectangle} on which the the text will be rasterized.
     */
    private void rasterizeTitle(final String title, final Rectangle titleArea) {
        BrailleText diagramTitle = new BrailleText(title, titleArea);
        try {
            mTextRasterizer.rasterize(diagramTitle, mCanvas);
        } catch (InsufficientRenderingAreaException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wrapper method for creating an X-{@link Axis} and rasterize it. Delegates to the {@link LinearMappingAxisRasterizer} for rasterizing.
     * @param originY The y coordinate of the position where the axis line and the tickmark and label corresponding to the value '0' is placed.
     * @param originX The x coordinate of the position where the axis line and the tickmark and label corresponding to the value '0' is placed.
     * @param stepWidthX The distance between two tickmarks on the axis in cells. This will be automatically converted in dots for the {@link Axis}.
     * @param xAxisBound The x-axis bound so that the borders are considered.
     * @param labels Map containing the labels (letters).
     */
    private void rasterizeXAxis(final int originY, final int originX, final int stepWidthX, final Rectangle xAxisBound, final Map<Integer, String> labels) {
        Axis xAxis = new Axis(Axis.Type.X_AXIS, originX, originY, stepWidthX * mCanvas.getCellWidth(), 2);
        xAxis.setBoundary(xAxisBound);
        xAxis.setLabels(labels);
        Rectangle test = xAxis.getBoundary();
        try {
            mAxisRasterizer.rasterize(xAxis, mCanvas);
        } catch (InsufficientRenderingAreaException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wrapper method for creating an Y-{@link Axis} and rasterize it. Delegates to the {@link LinearMappingAxisRasterizer} for rasterizing.
     * @param originY The y coordinate of the position where the axis line and the tickmark and label corresponding to the value '0' is placed.
     * @param originX The x coordinate of the position where the axis line and the tickmark and label corresponding to the value '0' is placed.
     * @param stepWidthY The distance between two tickmarks on the axis in cells. This will be automatically converted in dots for the {@link Axis}.
     * @param yAxisBound The x-axis bound so that the borders are considered.
     * @param labels {@link Map} containing the labels (letters).
     */
    @SuppressWarnings("magicnumber")
    private void rasterizeYAxis(final int originY, final int originX, final int stepWidthY, final Rectangle yAxisBound, final Map<Integer, String> labels) {
        Axis yAxis = new Axis(Axis.Type.Y_AXIS, originX, originY, stepWidthY * mCanvas.getCellHeight(), -2);
        yAxis.setBoundary(yAxisBound);
        yAxis.setLabels(labels);
        try {
            mAxisRasterizer.rasterize(yAxis, mCanvas);
        } catch (InsufficientRenderingAreaException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for cutting off the right {@link Rectangle} from the whole {@link RasterCanvas}.
     * Internally it uses the mDiagram variable.
     * @return {@link Rectangle} with the correct length and width so that the diagramtitle can be rasterized on it.
     * @throws InsufficientRenderingAreaException If the text is too big to fit on the {@link RasterCanvas}.
     */
    private Rectangle calculateTitle() throws InsufficientRenderingAreaException {
        if (mDiagram.getTitle().isEmpty()) {
            throw new IllegalArgumentException("The title in LineChartRasterizer was empty!");
        }
        int widthOfCompleteArea = mCellLineArea.intWrapper().getWidth();
        int titleBarHeight = mTextRasterizer.calculateRequiredHeight(mDiagram.getTitle(), widthOfCompleteArea, mCanvas, BrailleLanguage.Language.DE_KURZSCHRIFT);
        try {
            return  mCellLineArea.removeFromTop(mCanvas.getCellYFromDotY(titleBarHeight) + 1);
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space to build the title area for the line chart!");
        }
    }

    /**
     * Method for cutting off the right {@link Rectangle} from the whole {@link RasterCanvas}.
     * Currently it cuts of from the bottom and left by the amount of the offset variable
     * @return {@link Rectangle} for the x-axis.
     * @throws InsufficientRenderingAreaException If the offset amount cant be cut off the mCellLineArea.
     */
    private Rectangle calculateXAxis() throws InsufficientRenderingAreaException {
        Objects.requireNonNull(mCellLineArea, "The given Rectangle for the x axis to be removed from was null!");
        try {
            Rectangle result = mCellLineArea.removeFromBottom(mPaddingBetweenAxisTextAndDiagram);
            result.removeFromLeft(mPaddingBetweenAxisTextAndDiagram);
            return result;
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space to build the X-Axis for the line chart!");
        }
    }

    /**
     * Method for cutting off the right {@link Rectangle} from the whole {@link RasterCanvas}.
     * Currently it cuts of from the left by the amount of the offset variable
     * @return {@link Rectangle} for the y-axis.
     * @throws InsufficientRenderingAreaException If the offset amount cant be cut off the mCellLineArea.
     */
    private Rectangle calculateYAxis() throws InsufficientRenderingAreaException {
        Objects.requireNonNull(mCellLineArea, "The given Rectangle for the y axis to be removed from was null!");
        try {
            return mCellLineArea.removeFromLeft(mPaddingBetweenAxisTextAndDiagram);
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space to build the Y-Axis for the line chart!");
        }
    }

    /**
     * Method for calculating the valuerange of the x-axis.
     * @return {@link Double} representing the value range of the x-axis.
     */
    private double valueRangeOfYAxis() {
        Objects.requireNonNull(mDiagram, "The given linechart for the calculation of the value range of the y-axis was null!");
        double minY = mDiagram.getMinY();
        double maxY = mDiagram.getMaxY();
        double valueRangeOfYAxis;
        if (minY >= 0) {
            valueRangeOfYAxis = maxY - minY;
        } else {
            valueRangeOfYAxis = abs(maxY) + abs(minY);
        }
        return valueRangeOfYAxis;
    }

    /**
     * Calculate width, measured in cells. Important: it divides by two and floors the result. The current axis rasterizer
     * does not support a width of 1.
     * @param rectangle The cell rectangle which you want to know the width.
     * @return Width in cells divided by two and floored.
     */
    private int calculateUnitsWidthInCells(final Rectangle rectangle) {
        Objects.requireNonNull(rectangle, "The given rectangle for the calculation of its width was null!");
        return (int) floor((rectangle.getWidth() - 1) / 2);
    }

    /**
     * Method for calculating the value range of the x axis.
     * @return {@link Double} representing the value range.
     */
    private double valueRangeOfXAxis() {
        Objects.requireNonNull(mDiagram, "The given linechart for the calculation of the value range of the x-axis was null!");
        double minX = mDiagram.getMinX();
        double maxX = mDiagram.getMaxX();
        return maxX - minX;
    }

    /**
     * Returns the number of ticks. Currently, it adds one to the given parameter.
     * @param unitsAvailable How many units are available on the x axis.
     * @return {@link Double} representing ticks available.
     */
    private double getNumberOfTicks(final int unitsAvailable) {
        if (unitsAvailable < 0) {
            throw new RuntimeException("The units available was less then zero!");
        }
        return unitsAvailable + 1;
    }

    /**
     * Method for finding the x axis step width.
     * @param rangeOfXValues Representing the range of values.
     * @param xUnitsAvailable Representing the availabe units on the xAxis.
     * @return Currently always 2;
     */
    private double findXAxisStepWidth(final double rangeOfXValues, final int xUnitsAvailable) {
        // Most simple approach: always take the minimum stepwidth, which the x-axis rasterizer can handle
        // The signature is not adjusted so that someone can change the calculation if he needs it
        return 2;
    }
}
