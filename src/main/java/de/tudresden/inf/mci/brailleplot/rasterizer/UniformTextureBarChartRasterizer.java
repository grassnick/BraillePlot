package de.tudresden.inf.mci.brailleplot.rasterizer;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

public class UniformTextureBarChartRasterizer implements BarChartRasterizer {

    Printer mPrinter;
    Format mFormat;

    public UniformTextureBarChartRasterizer(final Printer printer, final Format format) {
        mPrinter = printer;
        mFormat = format;
    }

    @Override
    public MatrixData rasterize(RepresentationStub diagram) {
        return null;
    }

    @Override
    public FloatingPointData plot(RepresentationStub diagram) {
        return null;
    }
}
