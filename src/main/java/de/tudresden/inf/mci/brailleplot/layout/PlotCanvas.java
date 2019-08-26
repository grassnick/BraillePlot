package de.tudresden.inf.mci.brailleplot.layout;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representation of a target onto which an image can be plotted.
 * It wraps a {@link de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData} instance and describes the plot size and its equidistant layout.
 * @author Richard Schmidt
 */
public class PlotCanvas extends AbstractCanvas {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    //floating dot area resolution
    private float mResolution;

    /**
     * Constructor. Creates a new PlotCanvas, which is a canvas that represents it pages as instances of
     * {@link de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData} and holds information about the layout.
     * @param printer The {@link Printer} configuration to be used.
     * @param format The {@link Format} configuration to be used.
     * @throws InsufficientRenderingAreaException If the given configuration leads to an printable area of negative
     * size or zero size, e.g. if the sum of defined margins and constraints adds up to be greater than the original page size.
     */
    public PlotCanvas(final Printer printer, final Format format, final float resolution) throws InsufficientRenderingAreaException {
        super(printer, format);
        mResolution = resolution;
    }
}
