package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;

/**
 * Represents a raster consisting of 6-dot braille cells.
 * @author Leonard Kupper
 * @version 2019.07.12
 */
class SixDotBrailleRasterCanvas extends AbstractRasterCanvas {

    SixDotBrailleRasterCanvas(final Printer printer, final Format format) throws InsufficientRenderingAreaException {
        super(printer, format, 2, 3);
    }
}
