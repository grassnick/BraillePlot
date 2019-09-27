package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Representation;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.SixDotBrailleRasterCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;

import java.util.Objects;

/**
 * MasterRenderer. This class is the main interface for conversion of a diagram representation into a printable format and manages the current rendering context.
 * The MasterRenderer takes representations of any diagram type, calculates the available raster/area from the given printer and format configuration and dispatches
 * any calls to the 'rasterize' and 'plot' methods to the given {@link FunctionalRenderingBase}.
 * @author Leonard Kupper
 * @version 2019.07.20
 */
public final class MasterRenderer {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    Printer mPrinter;
    Representation mRepresentation;
    Format mFormat;
    FunctionalRenderingBase mRenderingBase;

    public MasterRenderer(final Printer printer, final Representation representation, final Format format) {

        mLogger.info("Creating MasterRenderer with default context");

        mLogger.trace("Instantiating default rendering base");
        // if no rendering base is given, create own rendering base with default set of algorithms
        FunctionalRenderingBase renderingBase = new FunctionalRenderingBase();

        mLogger.trace("Instantiating default rasterizers");
        // Default Algorithms:

        Rasterizer<CategoricalBarChart> barChartRasterizer = new BarChartRasterizer();
        Rasterizer<Image> linearImageMapping = new ImageRasterizer();

        mLogger.trace("Registering default rasterizers");
        renderingBase.registerRasterizer(new FunctionalRasterizer<CategoricalBarChart>(CategoricalBarChart.class, barChartRasterizer));
        renderingBase.registerRasterizer(new FunctionalRasterizer<Image>(Image.class, linearImageMapping));
        //renderingBase.registerRasterizer(new FunctionalRasterizer<ScatterPlot>(ScatterPlot.class, ScatterPlotRasterizing::fooRasterizing));
        //...

        setRenderingContext(printer, representation, format, renderingBase);
    }

    public MasterRenderer(final Printer printer, final Representation representation, final Format format, final FunctionalRenderingBase renderingBase) {
        mLogger.info("Creating MasterRenderer with custom context");
        setRenderingContext(printer, representation, format, renderingBase);
    }

    public RasterCanvas rasterize(final Renderable data) throws InsufficientRenderingAreaException {
        mLogger.info("Preparing a new {} rasterizing on RenderingBase {}",
                data.getClass().getSimpleName(), mRenderingBase);
        RasterCanvas canvas = createCompatibleRasterCanvas();
        mRenderingBase.setRasterCanvas(canvas);
        mRenderingBase.rasterize(data);
        mLogger.info("Rasterizing of {} on RenderingBase {} has finished, result containing {} pages",
                data.getClass().getSimpleName(), mRenderingBase, canvas.getPageCount());
        return canvas;
    }

    private RasterCanvas createCompatibleRasterCanvas() throws InsufficientRenderingAreaException {
        mLogger.info("Creating compatible RasterCanvas for current rendering context");
        return new SixDotBrailleRasterCanvas(mPrinter, mRepresentation, mFormat);

        /*
        TODO: support 6 and 8 dot layout#
        String rasterType = mPrinter.getProperty("raster.type").toString();
        if (rasterType == "6-dot") {
            return new SixDotBrailleRasterCanvas(mPrinter, mFormat);
        } else {
            ...
        }
         */
    }

    // Getter & Setter

    public void setRenderingContext(final Printer printer, final Representation representation, final Format format, final FunctionalRenderingBase renderingBase) {
        setPrinter(printer);
        setRepresentation(representation);
        setFormat(format);
        setRenderingBase(renderingBase);
    }

    public void setPrinter(final Printer printer) {
        mPrinter = Objects.requireNonNull(printer);
        mLogger.info("Rendering context: Printer was set to {}", mPrinter);
    }
    public Printer getPrinter() {
        return mPrinter;
    }

    public void setRepresentation(final Representation representation) {
        mRepresentation = Objects.requireNonNull(representation);
        mLogger.info("Rendering context: Representation was set to {}", mRepresentation);
    }
    public Representation getRepresentation() {
        return mRepresentation;
    }

    public void setFormat(final Format format) {
        mFormat = Objects.requireNonNull(format);
        mLogger.info("Rendering context: Format was set to {}", mFormat);
    }
    public Format getFormat() {
        return mFormat;
    }

    public void setRenderingBase(final FunctionalRenderingBase renderingBase) {
        mRenderingBase = Objects.requireNonNull(renderingBase);
        mLogger.info("Rendering context: Set RenderingBase to instance {}", mRenderingBase);
    }
    public FunctionalRenderingBase getRenderingBase() {
        return mRenderingBase;
    }
}
