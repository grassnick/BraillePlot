package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a target onto which can be drawn. It wraps a {@link PrintableData} instance and specifies the size of the drawing area (in mm).
 */
public abstract class AbstractCanvas {

    Printer mPrinter;
    Format mFormat;
    double mMillimeterWidth;
    double mMillimeterHeight;

    List<PrintableData> mPageContainer;

    AbstractCanvas(final Printer printer, final Format format) {
        mPrinter = printer;
        mFormat = format;
        mPageContainer = new ArrayList<>();
    }

    /**
     * This method is supposed to return the full width of the canvas.
     * @return The width of the canvas in millimeters.
     */
    public double getAbsoluteWidth() {
        return mMillimeterWidth;
    }

    /**
     * This method is supposed to return the full height of the canvas.
     * @return The height of the canvas in millimeters.
     */
    public double getAbsoluteHeight() {
        return mMillimeterHeight;
    }

}
