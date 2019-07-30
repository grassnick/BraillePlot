package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;

/**
 * Represents a raster consisting of 6-dot braille cells. (May be removed completely in favor of dynamic {@link RasterCanvas})
 * @author Leonard Kupper
 * @version 2019.07.20
 */
class SixDotBrailleRasterCanvas extends RasterCanvas {

    private static final int CELL_WIDTH = 2;
    private static final int CELL_HEIGHT = 3;

    SixDotBrailleRasterCanvas(final Printer printer, final Format format) throws InsufficientRenderingAreaException {
        super(printer, format, CELL_WIDTH, CELL_HEIGHT);
    }
}
