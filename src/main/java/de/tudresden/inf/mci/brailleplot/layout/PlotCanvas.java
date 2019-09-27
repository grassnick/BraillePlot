package de.tudresden.inf.mci.brailleplot.layout;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.configparser.Representation;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleFloatingPointDataImpl;

/**
 * Representation of a target onto which an image can be plotted.
 * It wraps a {@link de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData} instance and describes the raster size and its layout.
 * @author Georg Graßnick
 * @version 2019.08.26
 */
public class PlotCanvas extends AbstractCanvas<FloatingPointData<Boolean>> {

    public PlotCanvas(final Printer printer, final Representation representation, final Format format) throws InsufficientRenderingAreaException {
        super(printer, representation, format);
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
}
