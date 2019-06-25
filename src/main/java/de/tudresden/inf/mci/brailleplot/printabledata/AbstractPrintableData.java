package de.tudresden.inf.mci.brailleplot.printabledata;

import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.configparser.Format;

/**
 * Abstract parent class for all {@link PrintableData} implementations.
 */
abstract class AbstractPrintableData implements PrintableData {

    private final Printer mPrinter;
    private final Format mFormat;

    AbstractPrintableData(final Printer printer, final Format format) {
        mPrinter = printer;
        mFormat = format;
    }

    @Override
    public Printer getPrinterConfig() {
        return mPrinter;
    }

    @Override
    public Format getFormatConfig() {
        return mFormat;
    }
}
