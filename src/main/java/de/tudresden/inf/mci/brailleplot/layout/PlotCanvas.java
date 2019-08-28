package de.tudresden.inf.mci.brailleplot.layout;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleFloatingPointDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representation of a target onto which an image can be plotted.
 * It wraps a {@link de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData} instance and describes the plot size and its equidistant layout.
 * @author Leonard Kupper and Richard Schmidt
 */

public class PlotCanvas extends AbstractCanvas {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    //floating dot area resolution
    private double mResolution;

    // diamensions for Braille characters (e.g. axes, title)
    private int mCellWidth;
    private int mCellHeight;

    // cell distances horizontal and vertical
    private int mCellDistHor;
    private int mCellDistVer;

    /**
     * Constructor. Creates a new PlotCanvas, which is a canvas that represents it pages as instances of
     * {@link de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData} and holds information about the layout.
     * @param printer The {@link Printer} configuration to be used.
     * @param format The {@link Format} configuration to be used.
     * @throws InsufficientRenderingAreaException If the given configuration leads to an printable area of negative
     * size or zero size, e.g. if the sum of defined margins and constraints adds up to be greater than the original page size.
     */
    public PlotCanvas(final Printer printer, final Format format, final double resolution) throws InsufficientRenderingAreaException {
        super(printer, format);
        mResolution = resolution;
    }

    public final FloatingPointData<Boolean> getNewPage() {
        mPageContainer.add(new SimpleFloatingPointDataImpl<Boolean>(mPrinter, mFormat));
        return getCurrentPage();
    }

    @SuppressWarnings("unchecked")
    // This is allowed because the mPageContainer fields are always initialized with the correct type by the page getters,
    // cannot be accessed from the outside and are never changed anywhere else.
    public final FloatingPointData<Boolean> getCurrentPage() {
        if (mPageContainer.size() < 1) {
            return getNewPage();
        }
        return (FloatingPointData<Boolean>) mPageContainer.get(mPageContainer.size() - 1);
    }

    /**
     * Reads config file to get parameters to calculate class variables.
     */
    @SuppressWarnings("MagicNumber")
    public void readConfig() {
        mLogger.trace("Reading raster specific configuration");

        mResolution = mFormat.getProperty("floatingDot.resolution").toDouble();
        mCellWidth = mFormat.getProperty("raster.dotDistance.horizontal").toInt() + 2 * mFormat.getProperty("raster.dotDiameter").toInt();
        mCellHeight = 2 * mFormat.getProperty("raster.dotDistance.vertical").toInt() + 3 * mFormat.getProperty("raster.dotDiameter").toInt();
        mCellDistHor = mFormat.getProperty("raster.cellDistance.horizontal").toInt();
        mCellDistVer = mFormat.getProperty("raster.cellDistance.vertical").toInt();

    }

    public final double getResolution() {
        return mResolution;
    }

    public final int getCellWidth() {
        return mCellWidth;
    }

    public final int getCellHeight() {
        return mCellHeight;

    }
    public final int getCellDistHor() {
        return mCellDistHor;
    }

    public final int getCellDistVer() {
        return mCellDistVer;
    }


}
