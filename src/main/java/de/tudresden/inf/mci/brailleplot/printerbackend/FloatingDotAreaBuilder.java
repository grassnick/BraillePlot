package de.tudresden.inf.mci.brailleplot.printerbackend;


import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

/**
 * Class representing the FloatingDotAre-Protocol form the Braille Index Everest V4 for printing
 * variable Areas on paper via Coordinates.
 * @author Andrey Ruzhanskiy, Leonard Kupper
 * @version 29.05.2019
 */

public class FloatingDotAreaBuilder extends AbstractIndexV4Builder {

    /**
     * Constructor. Does not have any functionality. Should only be used in  {@link de.tudresden.inf.mci.brailleplot.printerbackend.PrintDirector}
     */
    FloatingDotAreaBuilder() { }

    /**
     * Currently not implemented.
     * @param data Raw Data to be printed without any escapesequences
     * @return Exception.
     */
    public byte[] assemble(final MatrixData data) {
        throw new UnsupportedOperationException();
    }
}
