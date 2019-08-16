package de.tudresden.inf.mci.brailleplot.layout;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.BrailleCell6;

/**
 * Represents a raster consisting of 6-dot braille cells. (May be removed completely in favor of dynamic {@link RasterCanvas})
 * @author Leonard Kupper
 * @version 2019.07.20
 */
public class SixDotBrailleRasterCanvas extends RasterCanvas {

    public SixDotBrailleRasterCanvas(final Printer printer, final Format format) throws InsufficientRenderingAreaException {
        super(printer, format, BrailleCell6.COLUMN_COUNT, BrailleCell6.ROW_COUNT);
    }
}
