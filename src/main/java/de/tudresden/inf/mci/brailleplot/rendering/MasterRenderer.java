package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import java.util.Objects;

/**
 * MasterRenderer. This class is the main interface for conversion of a diagram representation into a printable format and manages the current rendering context.
 * The MasterRenderer takes representations of any diagram type, calculates the available raster/area from the given printer and format configuration and dispatches
 * any calls to the 'rasterize' and 'plot' methods to the given {@link FunctionalRenderingBase}.
 * @author Leonard Kupper
 * @version 2019.07.01
 */
public class MasterRenderer {

    Printer mPrinter;
    Format mFormat;
    FunctionalRenderingBase mRenderingBase;

    public MasterRenderer(final Printer printer, final Format format, final FunctionalRenderingBase renderingBase) {
        setRenderingContext(printer, format, renderingBase);
    }

    public final MatrixData rasterize(final DiagramStub data) throws InsufficientRenderingAreaException {
        mRenderingBase.setRaster(calculateRaster());
        return mRenderingBase.rasterize(data);
    }

    private Raster calculateRaster() {
        /*
        mPrinter.getProperty("raster.cells.width").toDouble();
        mPrinter.getProperty("raster.cells.height").toDouble();
        mPrinter.getProperty("raster.cells.dotDistance.horizontal").toDouble();
        mPrinter.getProperty("raster.cells.dotDistance.vertical").toDouble();
        mPrinter.getProperty("raster.cellDistance.horizontal").toDouble();
        mPrinter.getProperty("raster.cellDistance.vertical").toDouble();

        mFormat.getProperty("width").toDouble();
        mFormat.getProperty("height").toDouble();
        mFormat.getProperty("margin.top").toDouble();
        mFormat.getProperty("margin.bottom").toDouble();
        mFormat.getProperty("margin.left").toDouble();
        mFormat.getProperty("margin.right").toDouble();
        */

        return new SixDotBrailleRaster(35, 29, 0, 0, 0, 0);
    }

    // Getter & Setter

    public final void setRenderingContext(final Printer printer, final Format format, final FunctionalRenderingBase renderingBase) {
        setPrinter(printer);
        setFormat(format);
        setRenderingBase(renderingBase);
    }

    public final void setPrinter(final Printer printer) {
        mPrinter = Objects.requireNonNull(printer);
    }
    public final Printer getPrinter() {
        return mPrinter;
    }

    public final void setFormat(final Format format) {
        mFormat = Objects.requireNonNull(format);
    }
    public final Format getFormat() {
        return mFormat;
    }

    public final void setRenderingBase(final FunctionalRenderingBase renderingBase) {
        mRenderingBase = Objects.requireNonNull(renderingBase);
    }
    public final FunctionalRenderingBase getRenderingBase() {
        return mRenderingBase;
    }
}
