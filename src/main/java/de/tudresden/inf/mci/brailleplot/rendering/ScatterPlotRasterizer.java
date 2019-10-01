package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ScatterPlotRasterizer implements Rasterizer<ScatterPlot> {

    private static final int AXIS_WIDTH = 2; // Minimum width of an axis, is automatically increased to match a multiple of cell size [cells]
    private static final int TOKEN_AXIS_OFFSET = 1; // Offset of actual plotting area to axis [dots]
    private static final double AXIS_STEP_WIDTH = 1.0;
    private static final int AXIS_TICK_SIZE = 1; // The size of the ticks on the axis increased to match a multiple of cell size [dots]

    private static final Logger mLogger = LoggerFactory.getLogger(ScatterPlotRasterizer.class);

    public ScatterPlotRasterizer() {
    }

    @Override
    public void rasterize(final ScatterPlot scatterPlot, RasterCanvas canvas) throws InsufficientRenderingAreaException {
        Objects.requireNonNull(scatterPlot);
        Objects.requireNonNull(canvas);

        PointListContainer<PointList> data = scatterPlot.getDataSet();
        MatrixData<Boolean> mat = canvas.getCurrentPage();
        final int cellWidth = canvas.getCellWidth();
        final int cellHeight = canvas.getCellHeight();
        final String title = "I am a Scatter plot beep beep.";


        Rectangle completeArea = canvas.getCellRectangle();
        completeArea = completeArea.scaledBy(cellWidth, cellHeight);
        Rectangle printableArea = new Rectangle(completeArea);

        Rectangle titleArea;
        BrailleText diagramTitle;
        BrailleText xAxisLabel, yAxisLabel;

        // 1.a Reserve space for diagram title
        LiblouisBrailleTextRasterizer textRasterizer = new LiblouisBrailleTextRasterizer(canvas.getPrinter());

        try {
            //printableArea.removeFromRight(cellWidth);
            int titleBarHeight = textRasterizer.calculateRequiredHeight(title, printableArea.intWrapper().getWidth(), canvas);
            titleArea = printableArea.removeFromTop(titleBarHeight * cellHeight);

            // Spacing
            printableArea.removeFromTop(cellHeight);
        } catch (final Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space to construct title layout", e);
        }

        // 1.b Initialize diagram title
        diagramTitle = new BrailleText(title, titleArea.scaledBy(cellWidth, cellHeight));


        // 2.a Reserve space for axis
        Rectangle xAxisArea, yAxisArea;
        Rectangle xAxisBoundary, yAxisBoundary;
        try {
            // Do not remove xAxis area immediately, so that both axis overlap in the bottom left corner
            int xAxisHeight = toWholeCells(AXIS_WIDTH * cellHeight, cellHeight);
            xAxisArea = printableArea.fromBottom(xAxisHeight);
            yAxisArea = printableArea.removeFromLeft(toWholeCells(AXIS_WIDTH * cellWidth, cellWidth));
            printableArea.removeFromBottom(xAxisHeight);
        } catch (Exception e) {
            throw new InsufficientRenderingAreaException("Not enough space to construct axis layout", e);
        }

        // 2.b Some spacing to distinguish tokens from axis, in whole cells
        try {
            printableArea.removeFromBottom(toWholeCells(TOKEN_AXIS_OFFSET, cellHeight));
            printableArea.removeFromLeft(toWholeCells(TOKEN_AXIS_OFFSET, cellWidth));
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space for plot and axis border space");
        }

        // 2.c Initialize axis+
        final Rectangle.IntWrapper printableAreaInt = printableArea.intWrapper();
        Axis xAxis = new Axis(Axis.Type.X_AXIS, printableAreaInt.getX(), xAxisArea.intWrapper().getY(), AXIS_STEP_WIDTH, toWholeCells(AXIS_TICK_SIZE, cellHeight) - 1);
        Axis yAxis = new Axis(Axis.Type.Y_AXIS, yAxisArea.intWrapper().getRight(), printableAreaInt.getBottom(), AXIS_STEP_WIDTH, -1 * (toWholeCells(AXIS_TICK_SIZE, cellWidth) - 1));
        xAxis.setBoundary(xAxisArea);
        yAxis.setBoundary(yAxisArea);

        LinearMappingAxisRasterizer axisRasterizer = new LinearMappingAxisRasterizer();

        // 3. Print layout if needed, else render title and axis
        final boolean printLayout = false;
        if (!printLayout) {
            textRasterizer.rasterize(diagramTitle, canvas);
            axisRasterizer.rasterize(xAxis, canvas);
            axisRasterizer.rasterize(yAxis, canvas);
        } else {
            Rasterizer.rectangle(titleArea, mat, true);
            Rasterizer.rectangle(xAxisArea, mat, true);
            Rasterizer.rectangle(yAxisArea, mat, true);
        }
        Rasterizer.rectangle(printableArea, mat, true);

        // 4. Actual scatter plot
        Rectangle plotArea = printableArea;

        int xDots = plotArea.intWrapper().getWidth();
        int yDots = plotArea.intWrapper().getHeight();

        int xMin = ((int) Math.ceil(data.getMinX()));
        int yMin = ((int) Math.ceil(data.getMinY()));
        int xMax = ((int) Math.ceil(data.getMaxX()));
        int yMax = ((int) Math.ceil(data.getMaxY()));

        int xRange = Math.abs(xMax - xMin);
        int yRange = Math.abs(yMax - yMin);

        double xRatio = Math.floor((double) xDots / (double) xRange);
        double yRatio = Math.floor((double) yDots / (double) yRange);

        int xOrigin = plotArea.intWrapper().getX();
        int yOrigin = plotArea.intWrapper().getY() + plotArea.intWrapper().getHeight();

        mLogger.debug("Complete printable Area: ({},{})", completeArea.intWrapper().getWidth(), completeArea.intWrapper().getHeight());
        mLogger.debug("Printable Area for actual plot: ({},{}) with global offset: ({},{})", plotArea.intWrapper().getWidth(), plotArea.intWrapper().getHeight(), plotArea.intWrapper().getX(), plotArea.intWrapper().getY());
        mLogger.debug("xMin; {}, xMax: {}, xRange: {}, xRatio: {}", xMin, xMax, xRange, xRatio);
        mLogger.debug("yMin; {}, yMax: {}, yRange: {}, yRatio: {}", yMin, yMax, yRange, yRatio);
        mLogger.debug("PlotOrigin: ({},{})", xOrigin, yOrigin);

        for (PointList l : data) {
            for (Point2DDouble p : l) {
                int x = (int) Math.round(Math.abs((p.getX() - xMin) * xRatio));
                int y = (int) Math.round(Math.abs((p.getY() - yMin) * yRatio));
                if (x < 0 || x > xDots || y < 0 || y > yDots) {
                    throw new RuntimeException("Calculated position not in bounds: (" + x + "," + y + "), (" + xDots + "," + yDots + ")");
                }

                final int xGlobal = xOrigin + x;
                final int yGlobal = yOrigin - y;
                mLogger.debug("Placing token at local: ({},{}), global: ({},{}) for data point: ({},{})", x, y, xGlobal, yGlobal, p.getX(), p.getY());
                mat.setValue(yGlobal, xGlobal, true);
            }
        }
    }

    private static int toWholeCells(final int dots, final int cellDots) {
        int diff = dots % cellDots;
        if (diff != 0) {
            diff = cellDots - diff;
        }
        int result = dots + diff;
        mLogger.trace("Stretching {} to {}, cellSize: {}", dots, result, cellDots);
        return result;
    }
}
