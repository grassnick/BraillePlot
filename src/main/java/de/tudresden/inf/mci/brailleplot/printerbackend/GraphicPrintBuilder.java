package de.tudresden.inf.mci.brailleplot.printerbackend;


import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

/**
 * Class representing the Graphic-Mode-Protocol from Braille Index Everest D4.
 * @author Andrey Ruzhanskiy
 */
public class GraphicPrintBuilder extends AbstractIndexV4Builder {

    /**
     * Constructor. Does not have any functionality. Should only be used in  {@link de.tudresden.inf.mci.brailleplot.printerbackend.PrintDirector}
     */
     GraphicPrintBuilder() { }

    /**
     * Currently not implemented.
     * @param data Raw Data to be printed without any escapesequences
     * @return Exception.
     */
    @Override
    public byte[] assemble(final MatrixData data) {
        throw new UnsupportedOperationException();
    }
}
