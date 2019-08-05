package de.tudresden.inf.mci.brailleplot.printerbackend;


import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

/**
 * Class representing the graphic mode protocol from braille Index Everest D4.
 * @author Andrey Ruzhanskiy
 */
public class GraphicPrintBuilder extends AbstractIndexV4Builder {

    /**
     * Constructor. Does not have any functionality. Should only be used in  {@link PrintDirector}
     */
     GraphicPrintBuilder() { }

    /**
     * Currently not implemented.
     * @param data Raw data to be printed without any escape sequences
     * @return Exception.
     */
    @Override
    byte[] assemble(final MatrixData data) {
        throw new UnsupportedOperationException();
    }
}
