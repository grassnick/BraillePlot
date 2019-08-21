package de.tudresden.inf.mci.brailleplot.layout;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;

/**
 * PlotMatrix. Represents a matrix for plotting on Everest-D V4. Extends AbsractCanvas.
 * @author Richard Schmidt
 */
public class PlotMatrix extends AbstractCanvas {

    public PlotMatrix(final Printer printer, final Format format) throws InsufficientRenderingAreaException {
        super(printer, format);
    }
}
