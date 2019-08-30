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

    private static final int AXIS_WIDTH = 4; // Minimum width of an axis, is automatically increased to match a multiple of cell size [dots]
    private static final int TOKEN_AXIS_OFFSET = 1; // Offset of actual plotting area to axis [dots]

    //private PointListContainer<PointList> mContainer;
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

        BrailleTextRasterizer textRasterizer = new BrailleTextRasterizer();
        LinearMappingAxisRasterizer axisRasterizer = new LinearMappingAxisRasterizer();

        Rectangle completeArea = canvas.getCellRectangle();
        completeArea = completeArea.scaledBy(cellWidth, cellHeight);
        Rectangle printableArea = new Rectangle(completeArea);

        Rectangle titleArea;
        BrailleText diagramTitle;
        Rectangle xAxisArea, yAxisArea;
        BrailleText xAxisLabel, yAxisLabel;

        // 1. Reserve space for diagram title
        try {
            printableArea.removeFromRight(2);
            int titleBarHeight = textRasterizer.calculateRequiredHeight(title, 0, 0,
                    printableArea.intWrapper().getWidth() * cellWidth, canvas) + cellHeight;
            titleArea = printableArea.removeFromTop(titleBarHeight);
            diagramTitle = new BrailleText(title, titleArea.scaledBy(cellWidth, cellHeight));
            // TODO check if space is exceeded
        } catch (final Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space to construct title layout", e);
        }

        // 2. Reserve space for axis
        try {
            xAxisArea = printableArea.removeFromBottom(toWholeCells(AXIS_WIDTH, cellHeight));
            yAxisArea = printableArea.removeFromLeft(toWholeCells(AXIS_WIDTH, cellWidth));
            xAxisArea.removeFromLeft(yAxisArea.getWidth());
        } catch (Exception e) {
            throw new InsufficientRenderingAreaException("Not enough space to construct axis label layout", e);
        }

        // 3. Some spacing to distinguish tokens from axis, in whole cells
        try {
            printableArea.removeFromBottom(toWholeCells(TOKEN_AXIS_OFFSET, cellHeight));
            printableArea.removeFromLeft(toWholeCells(TOKEN_AXIS_OFFSET, cellWidth));
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space for plot and axis border space");
        }

        // 4. Print layout if needed
        final boolean printLayout = true;
        if (!printLayout) {
            Rasterizer.fill(xAxisArea.intWrapper().getX(), xAxisArea.intWrapper().getY(), xAxisArea.intWrapper().getWidth() + xAxisArea.intWrapper().getX(), xAxisArea.intWrapper().getY() , mat, true);
            Rasterizer.fill(yAxisArea.intWrapper().getX() + yAxisArea.intWrapper().getWidth() - 1, yAxisArea.intWrapper().getY(), yAxisArea.intWrapper().getX() + yAxisArea.intWrapper().getWidth() - 1 , yAxisArea.intWrapper().getY() + yAxisArea.intWrapper().getHeight(), mat, true);
            textRasterizer.rasterize(diagramTitle, canvas);
        } else {
            Rasterizer.rectangle(titleArea, mat, true);
            Rasterizer.rectangle(xAxisArea, mat, true);
            Rasterizer.rectangle(yAxisArea, mat, true);
            Rasterizer.rectangle(printableArea, mat, true);
        }

        // 5. Actual scatter plot
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
