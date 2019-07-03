package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;

/**
 * Representation of a target onto which can be drawn. It wraps a {@link PrintableData} instance and specifies the size of the drawing area (in mm).
 */
public abstract class AbstractCanvas {

    Printer mPrinter;
    Format mFormat;
    double mMillimeterWidth;
    double mMillimeterHeight;

    PrintableData mPrintableData;

    AbstractCanvas(final Printer printer, final Format format) {
        mPrinter = printer;
        mFormat = format;
    }

    /**
     * This method is supposed to return the full width of the canvas.
     * @return
     */
    public double getAbsoluteWidth() {
        return mMillimeterWidth;
    }

    /**
     * This method is supposed to return the full height of the canvas.
     * @return
     */
    public double getAbsoluteHeight() {
        return mMillimeterHeight;
    }

}
