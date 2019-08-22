package de.tudresden.inf.mci.brailleplot.layout;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;

/**
 * PlotCanvas. Represents a matrix for plotting on Everest-D V4. Extends AbsractCanvas.
 * @author Richard Schmidt
 */
public class PlotCanvas extends AbstractCanvas {

    public PlotCanvas(final Printer printer, final Format format) throws InsufficientRenderingAreaException {
        super(printer, format);
    }
}
