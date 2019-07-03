package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;

/**
 * Represents a raster consisting of 6-dot braille cells.
 */
class SixDotBrailleRasterCanvas extends AbstractRasterCanvas {

    SixDotBrailleRasterCanvas(final Printer printer, final Format format) {
        super(printer, format, 2, 3);
    }
}
