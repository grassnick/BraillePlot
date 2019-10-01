package de.tudresden.inf.mci.brailleplot.printerbackend;

import de.tudresden.inf.mci.brailleplot.brailleparser.AbstractBrailleTableParser;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;

/**
 * This class provides an extension point for further implementation
 * and protocol building for documents that need to be send to the printer.
 * The common Interface is the getDocument() and assemble() method.
 * Its usable for all braille printers.
 * @param <T> Type of Data.
 * @author Andrey Ruzhanskiy
 * @version 28.05.2019
 */

abstract class AbstractDocumentBuilder<T extends PrintableData> {

    T mData;


    AbstractBrailleTableParser mParser;

    /**
     * Complex method for complex construction of an document for the printer.
     * @param data Raw data to be printed without any escapes equences
     * @return Fully build document as byte[]
     */
    abstract byte[] assemble(T data);

}
