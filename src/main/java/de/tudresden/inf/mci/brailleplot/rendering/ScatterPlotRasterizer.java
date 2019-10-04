package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A rasterizer for Scatterplots.
 */
public class ScatterPlotRasterizer implements Rasterizer<ScatterPlot> {

    private static final int X_AXIS_WIDTH = 2; // Minimum width of the x axis [cells]
    private static final int Y_AXIS_WIDTH = 3; // Minimum width of the y axis [cells]
    private static final int TOKEN_AXIS_OFFSET = 1; // Offset of actual plotting area to axis, increased to match cell size [dots]
    private static final int X_AXIS_STEP_WIDTH = 3; // The distance between two tick marks on the x axis [cells]
    private static final int Y_AXIS_STEP_WIDTH = 3; // The distance between two tick marks on the y axis [cells]
    private static final int AXIS_TICK_SIZE = 1; // The length of the ticks on the axis [dots]
    private static final Locale NUMBER_LOCALE = new Locale("en", "US");

    private static final Logger LOG = LoggerFactory.getLogger(ScatterPlotRasterizer.class);

    public ScatterPlotRasterizer() {
    }

    @Override
    @SuppressWarnings("checkstyle:MethodLength")
    public void rasterize(final ScatterPlot scatterPlot, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        Objects.requireNonNull(scatterPlot);
        Objects.requireNonNull(canvas);

        PointListContainer<PointList> data = scatterPlot.getDataSet();

        final int cellWidth = canvas.getCellWidth();
        final int cellHeight = canvas.getCellHeight();
        final int xAxisStepWidth = cellWidth * X_AXIS_STEP_WIDTH;
        final int yAxisStepWidth = cellHeight * Y_AXIS_STEP_WIDTH;
        final BrailleLanguage.Language language = BrailleLanguage.Language.DE_BASISSCHRIFT;

        final String title = scatterPlot.getTitle();
        final String titleToDataSetSeparator = " - ";
        final String legendTitle = title;
        final String axisExplanationGroupName = "Achsenbeschriftungen";
        final String xAxisLegendGroupName = "x-Achse Werte";
        final String yAxisLegendGroupName = "y-Achse Werte";
        final String xAxisLabel = "x-Achse";
        final String xAxisLabelValue = scatterPlot.getXAxisName();
        final String yAxisLabel = "y-Achse";
        final String yAxisLabelValue = scatterPlot.getYAxisName();


        Rectangle completeArea = canvas.getCellRectangle();
        completeArea = completeArea.scaledBy(cellWidth, cellHeight);
        Rectangle printableArea = new Rectangle(completeArea);

        // --------------------------------------------------------------------
        // Layout shared for all data sets
        // --------------------------------------------------------------------

        // 1.a Reserve space for diagram title
        LiblouisBrailleTextRasterizer textRasterizer = new LiblouisBrailleTextRasterizer(canvas.getPrinter());

        Rectangle titleArea;
        try {
            // Calculate the minimum Height for the title out of all data sets
            int titleBarHeight = calcTitleHeight(title.concat(titleToDataSetSeparator), data, textRasterizer, printableArea.intWrapper().getWidth(), canvas, language);
            titleArea = printableArea.removeFromTop(titleBarHeight * cellHeight);

            // Spacing
            printableArea.removeFromTop(cellHeight);
        } catch (final Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space to construct title layout", e);
        }

        // 2.a Reserve space for axis
        Rectangle xAxisArea, yAxisArea;
        try {
            int xAxisHeight = X_AXIS_WIDTH * cellHeight;
            int yAxisWidth = Y_AXIS_WIDTH * cellWidth;
            xAxisArea = printableArea.removeFromBottom(xAxisHeight);
            yAxisArea = printableArea.removeFromLeft(yAxisWidth);
            xAxisArea.removeFromLeft(yAxisWidth);
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
        Axis xAxis = new Axis(Axis.Type.X_AXIS, printableAreaInt.getX(), xAxisArea.intWrapper().getY(), xAxisStepWidth, AXIS_TICK_SIZE);
        Axis yAxis = new Axis(Axis.Type.Y_AXIS, yAxisArea.intWrapper().getRight(), printableAreaInt.getBottom(), yAxisStepWidth, -AXIS_TICK_SIZE);
        xAxis.setBoundary(xAxisArea);
        yAxis.setBoundary(yAxisArea);

        // 3. Calculate scaling
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

        LOG.debug("Complete printable Area: ({},{})", completeArea.intWrapper().getWidth(), completeArea.intWrapper().getHeight());
        LOG.debug("Printable Area for actual plot: ({},{}) with global offset: ({},{})", plotArea.intWrapper().getWidth(), plotArea.intWrapper().getHeight(), plotArea.intWrapper().getX(), plotArea.intWrapper().getY());
        LOG.debug("xMin; {}, xMax: {}, xRange: {}, xRatio: {}", xMin, xMax, xRange, xRatio);
        LOG.debug("yMin; {}, yMax: {}, yRange: {}, yRatio: {}", yMin, yMax, yRange, yRatio);
        LOG.debug("PlotOrigin: ({},{})", xOrigin, yOrigin);

        // 4. Add tick mark labels to axis and to legend
        LinearMappingAxisRasterizer axisRasterizer = new LinearMappingAxisRasterizer();
        Legend legend = new Legend(legendTitle);

        Map<String, String> axisLabels = new HashMap<>();
        axisLabels.put(xAxisLabel, xAxisLabelValue);
        axisLabels.put(yAxisLabel, yAxisLabelValue);
        legend.addSymbolExplanationGroup(axisExplanationGroupName, axisLabels);

        char label = 'a';

        // X axis
        Map<String, String> xAxisLegendSymbols = new HashMap<>();
        legend.addSymbolExplanationGroup(xAxisLegendGroupName, xAxisLegendSymbols);

        // TODO Transfer to separate method
        Map<Integer, String> xAxisLabels = new HashMap<>();
        xAxis.setLabels(xAxisLabels);
        final int xAxisTickCount = xDots / xAxisStepWidth;
        for (int x = 0; x < xAxisTickCount; x++) {
            xAxisLabels.put(x, String.valueOf(label));
            int tickPos = x * xAxisStepWidth;
            double val = tickPos / xRatio;
            LOG.trace("Adding x axis label {{},{}} for tick #{}", label, val, x);
            xAxisLegendSymbols.put(String.valueOf(label), formatDouble(val));
            label++;
        }

        // Y axis
        Map<String, String> yAxisLegendSymbols = new HashMap<>();
        legend.addSymbolExplanationGroup(yAxisLegendGroupName, yAxisLegendSymbols);

        Map<Integer, String> yAxisLabels = new HashMap<>();
        yAxis.setLabels(yAxisLabels);
        final int yAxisTickCount = yDots / yAxisStepWidth;
        for (int y = 0; y < yAxisTickCount; y++) {
            yAxisLabels.put(y, String.valueOf(label));
            int tickPos = y * yAxisStepWidth;
            double val = tickPos / yRatio;
            LOG.trace("Adding y axis label {{},{}} for tick #{}", label, val, y);
            yAxisLegendSymbols.put(String.valueOf(label), formatDouble(val));
            label++;
        }


        // --------------------------------------------------------------------
        // Rendering per data set
        // --------------------------------------------------------------------

        for (PointList l : data) {

            MatrixData<Boolean> mat = canvas.getNewPage();

            // 1. Set diagram title
            BrailleText diagramTitle = new BrailleText(title + titleToDataSetSeparator + l.getName(), titleArea);

            // 2. Render actual tokens
            for (Point2DDouble p : l) {
                int x = (int) Math.round(Math.abs((p.getX() - xMin) * xRatio));
                int y = (int) Math.round(Math.abs((p.getY() - yMin) * yRatio));
                if (x < 0 || x > xDots || y < 0 || y > yDots) {
                    throw new RuntimeException("Calculated position not in bounds: (" + x + "," + y + "), (" + xDots + "," + yDots + ")");
                }

                final int xGlobal = xOrigin + x;
                final int yGlobal = yOrigin - y - 1;
                LOG.trace("Placing token at local: ({},{}), global: ({},{}), rational: ({},{}) for data point: ({},{})", x, y, xGlobal, yGlobal, ((double) x) / ((double) xDots), ((double) y) / ((double) yDots), p.getX(), p.getY());
                mat.setValue(yGlobal, xGlobal, true);
            }

            // 5. Render title and axis, or draw layout (for debugging purposes)
            final boolean printLayout = false;
            if (!printLayout) {
                textRasterizer.rasterize(diagramTitle, canvas);
                axisRasterizer.rasterize(xAxis, canvas);
                axisRasterizer.rasterize(yAxis, canvas);
            } else {
                Rasterizer.rectangle(titleArea, mat, true);
                Rasterizer.rectangle(xAxisArea, mat, true);
                Rasterizer.rectangle(yAxisArea, mat, true);
                Rasterizer.rectangle(printableArea, mat, true);
            }
        }

        // --------------------------------------------------------------------
        // Render Legend once
        // --------------------------------------------------------------------

        // 6. Render the legend
        LegendRasterizer legendRasterizer = new LegendRasterizer();
        legendRasterizer.rasterize(legend, canvas);
    }

    private static int toWholeCells(final int dots, final int cellDots) {
        int diff = dots % cellDots;
        if (diff != 0) {
            diff = cellDots - diff;
        }
        int result = dots + diff;
        LOG.trace("Stretching {} to {}, cellSize: {}", dots, result, cellDots);
        return result;
    }

    private static String formatDouble(final double d) {
        return String.format(NUMBER_LOCALE, "%.2f", d);
    }

    private static int calcTitleHeight(final String title, final PointListContainer<PointList> data, final LiblouisBrailleTextRasterizer textRasterizer, final int width, final RasterCanvas canvas, final BrailleLanguage.Language lang) {
        int minHeight = Integer.MIN_VALUE;
        for (PointList l : data) {
            final String s = title + l.getName();
            int height = textRasterizer.calculateRequiredHeight(s, width, canvas, lang);
            minHeight = Math.max(minHeight, height);
        }
        return minHeight;
    }
}
