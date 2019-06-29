package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import java.util.Objects;

public class MasterRenderer {

    Printer mPrinter;
    Format mFormat;
    RenderingBase mRenderingBase;

    public MasterRenderer(Printer printer, Format format, RenderingBase renderingBase) {
        setRenderingContext(printer, format, renderingBase);
    }

    public MatrixData rasterize(DiagramStub data) {
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

        return new Raster();
    }

    // Getter & Setter

    public void setRenderingContext(Printer printer, Format format, RenderingBase renderingBase) {
        setPrinter(printer);
        setFormat(format);
        setRenderingBase(renderingBase);
    }

    public void setPrinter(Printer printer) {
        mPrinter = Objects.requireNonNull(printer);
    }
    Printer getPrinter(){
        return mPrinter;
    }

    public void setFormat(Format format) {
        mFormat = Objects.requireNonNull(format);
    }
    Format getFormat() {
        return mFormat;
    }

    public void setRenderingBase(RenderingBase renderingBase) {
        mRenderingBase = Objects.requireNonNull(renderingBase);
    }
    RenderingBase getRenderingBase() {
        return mRenderingBase;
    }
}
