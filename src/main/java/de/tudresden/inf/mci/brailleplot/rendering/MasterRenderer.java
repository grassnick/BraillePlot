package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.diagrams.BarChart;

import java.util.Objects;

/**
 * MasterRenderer. This class is the main interface for conversion of a diagram representation into a printable format and manages the current rendering context.
 * The MasterRenderer takes representations of any diagram type, calculates the available raster/area from the given printer and format configuration and dispatches
 * any calls to the 'rasterize' and 'plot' methods to the given {@link FunctionalRenderingBase}.
 * @author Leonard Kupper
 * @version 2019.07.20
 */
public final class MasterRenderer {

    Printer mPrinter;
    Format mFormat;
    FunctionalRenderingBase mRenderingBase;

    public MasterRenderer(final Printer printer, final Format format) {
        // if no rendering base is given, create own rendering base with default set of algorithms
        FunctionalRenderingBase renderingBase = new FunctionalRenderingBase();

        // here are the default algorithms:

        Rasterizer<BarChart> uniformTexture = new UniformTextureBarChartRasterizer();
        Rasterizer<Image> linearImageMapping = new ImageRasterizer();

        renderingBase.registerRasterizer(new FunctionalRasterizer<BarChart>(BarChart.class, uniformTexture));
        renderingBase.registerRasterizer(new FunctionalRasterizer<Image>(Image.class, linearImageMapping));
        //renderingBase.registerRasterizer(new FunctionalRasterizer<ScatterPlot>(ScatterPlot.class, ScatterPlotRasterizing::fooRasterizing));
        //...

        setRenderingContext(printer, format, renderingBase);
    }

    public MasterRenderer(final Printer printer, final Format format, final FunctionalRenderingBase renderingBase) {
        setRenderingContext(printer, format, renderingBase);
    }

    public RasterCanvas rasterize(final Renderable data) throws InsufficientRenderingAreaException {
        RasterCanvas canvas = createCompatibleRasterCanvas();
        mRenderingBase.setRasterCanvas(canvas);
        mRenderingBase.rasterize(data);
        return canvas;
    }

    private RasterCanvas createCompatibleRasterCanvas() throws InsufficientRenderingAreaException {

        return new SixDotBrailleRasterCanvas(mPrinter, mFormat);

        /*
        TODO: support 6 and 8 dot layout#
        String rasterType = mPrinter.getProperty("raster.type").toString();
        if (rasterType == "6-dot") {
            return new SixDotBrailleRasterCanvas(mPrinter, mFormat);
        } else {

        }
         */
    }

    // Getter & Setter

    public void setRenderingContext(final Printer printer, final Format format, final FunctionalRenderingBase renderingBase) {
        setPrinter(printer);
        setFormat(format);
        setRenderingBase(renderingBase);
    }

    public void setPrinter(final Printer printer) {
        mPrinter = Objects.requireNonNull(printer);
    }
    public Printer getPrinter() {
        return mPrinter;
    }

    public void setFormat(final Format format) {
        mFormat = Objects.requireNonNull(format);
    }
    public Format getFormat() {
        return mFormat;
    }

    public void setRenderingBase(final FunctionalRenderingBase renderingBase) {
        mRenderingBase = Objects.requireNonNull(renderingBase);
    }
    public FunctionalRenderingBase getRenderingBase() {
        return mRenderingBase;
    }
}
