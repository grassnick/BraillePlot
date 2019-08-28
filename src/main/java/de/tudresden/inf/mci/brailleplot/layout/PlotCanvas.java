package de.tudresden.inf.mci.brailleplot.layout;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleFloatingPointDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representation of a target onto which an image can be plotted.
 * It wraps a {@link de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData} instance and describes the raster size and its layout.
 * @author Georg Graßnick
 * @version 2019.08.26
 */
public class PlotCanvas extends AbstractCanvas<FloatingPointData<Boolean>> {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    //floating dot area resolution
    private double mResolution;

    // diamensions for Braille characters (e.g. axes, title)
    private int mCellWidth;
    private int mCellHeight;

    // cell distances horizontal and vertical
    private int mCellDistHor;
    private int mCellDistVer;

    public PlotCanvas(final Printer printer, final Format format) throws InsufficientRenderingAreaException {
        super(printer, format);
    }

    public final FloatingPointData<Boolean> getNewPage() {
        mPageContainer.add(new SimpleFloatingPointDataImpl<>(mPrinter, mFormat));
        return getCurrentPage();
    }

    @Override
    public double getFullConstraintLeft() {
        return getConstraintLeft();
    }

    @Override
    public double getFullConstraintTop() {
        return getConstraintTop();
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
