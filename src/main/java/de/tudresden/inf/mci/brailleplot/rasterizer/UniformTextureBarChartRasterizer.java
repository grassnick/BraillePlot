package de.tudresden.inf.mci.brailleplot.rasterizer;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

/**
 * UniformTextureBarChartRasterizer.
 * @param <T>
 */
public class UniformTextureBarChartRasterizer<T extends Number> implements BarChartRasterizer {

    Printer mPrinter;
    Format mFormat;

    public UniformTextureBarChartRasterizer(final Printer printer, final Format format) {
        mPrinter = printer;
        mFormat = format;
    }

    @Override
    public MatrixData<T> rasterize(final RepresentationStub diagram) {
        return null;
    }

    @Override
    public FloatingPointData<T> plot(final RepresentationStub diagram) {
        return null;
    }
}
