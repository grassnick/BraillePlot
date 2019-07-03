package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;

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

    public final AbstractRasterCanvas rasterize(final DiagramStub data) throws InsufficientRenderingAreaException {
        AbstractRasterCanvas canvas = createCompatibleRasterCanvas();
        mRenderingBase.setRasterCanvas(canvas);
        mRenderingBase.rasterize(data);
        return canvas;
    }

    private AbstractRasterCanvas createCompatibleRasterCanvas() {

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
