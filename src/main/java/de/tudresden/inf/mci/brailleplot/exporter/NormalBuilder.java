package de.tudresden.inf.mci.brailleplot.exporter;

import de.tudresden.inf.mci.brailleplot.PrintableData.MatrixData;

/**
 * Class representing a normal Document (for example a .txt) to print without
 * any Escapesequences.
 * @author Andrey Ruzhanskiy
 */
public class NormalBuilder extends AbstractDocumentBuilder {

    @Override
    public byte[] assemble(MatrixData data) {
        return null;
    }
}
