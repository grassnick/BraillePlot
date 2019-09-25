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
 * @author Georg Graßnick and Richard Schmidt
 * @version 2019.08.26
 */
public class PlotCanvas extends AbstractCanvas<FloatingPointData<Boolean>> {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    // cell distances horizontal and vertical
    private double mCellDistHor;
    private double mCellDistVer;

    // dimensions for Braille characters (e.g. axes, title)
    private double mCellHeight;
    private double mCellWidth;

    // bar dimensions
    private double mMaxBarWidth;
    private double mMinBarDist;
    private double mMinBarWidth;

    //floating dot area resolution
    private double mResolution;

    // frames for line plot
    private boolean mFrames;

    // second y-axis
    private boolean mSecondAxis;

    // scale factors
    private int mXScaleFactor;
    private int mYScaleFactor;

    // constants
    private static final int THREE = 3;

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
    public void readConfig() {
        mLogger.trace("Reading plot specific configuration");

        mResolution = mPrinter.getProperty("floatingDot.resolution").toDouble();
        mCellWidth = mPrinter.getProperty("raster.dotDistance.horizontal").toDouble() + 2 * mPrinter.getProperty("raster.dotDiameter").toDouble();
        mCellHeight = 2 * mPrinter.getProperty("raster.dotDistance.vertical").toDouble() + THREE * mPrinter.getProperty("raster.dotDiameter").toDouble();
        mCellDistHor = mPrinter.getProperty("raster.cellDistance.horizontal").toDouble();
        mCellDistVer = mPrinter.getProperty("raster.cellDistance.vertical").toDouble();
        mMinBarWidth = mFormat.getProperty("floatingDot.minBarWidth").toDouble();
        mMaxBarWidth = mFormat.getProperty("floatingDot.maxBarWidth").toDouble();
        mMinBarDist = mFormat.getProperty("floatingDot.minBarDist").toDouble();
        mSecondAxis = mFormat.getProperty("floatingDot.secondAxis").toBool();
        mFrames = mFormat.getProperty("floatingDot.frames").toBool();

    }

    public final double getResolution() {
        return mResolution;
    }

    public final double getCellWidth() {
        return mCellWidth;
    }

    public final double getCellHeight() {
        return mCellHeight;
    }

    public final double getCellDistHor() {
        return mCellDistHor;
    }

    public final double getCellDistVer() {
        return mCellDistVer;
    }

    public final double getMinBarWidth() {
        return mMinBarWidth;
    }

    public final double getMaxBarWidth() {
        return mMaxBarWidth;
    }

    public final double getMinBarDist() {
        return mMinBarDist;
    }

    public final boolean getSecondAxis() {
        return mSecondAxis;
    }

    public final boolean getFrames() {
        return mFrames;
    }

    public final void setXScaleFactor(final int factor) {
        mXScaleFactor = factor;
    }

    public final int getXScaleFactor() {
        return mXScaleFactor;
    }

    public final void setYScaleFactor(final int factor) {
        mYScaleFactor = factor;
    }

    public final int getYScaleFactor() {
        return mYScaleFactor;
    }



}
