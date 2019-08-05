package de.tudresden.inf.mci.brailleplot.printerbackend;


import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

/**
 * Class representing the FloatingDotAre protocol for the braille Index Everest V4 for printing
 * variable areas on paper via coordinates.
 * @author Andrey Ruzhanskiy, Leonard Kupper
 * @version 29.05.2019
 */

class FloatingDotAreaBuilder extends AbstractIndexV4Builder {

    /**
     * Constructor. Does not have any functionality. Should only be used in  {@link PrintDirector}
     */
    FloatingDotAreaBuilder() { }

    /**
     * Currently not implemented.
     * @param data Raw data to be printed without any escape sequences
     * @return Exception.
     */
    byte[] assemble(final MatrixData data) {
        throw new UnsupportedOperationException();
    }
}
