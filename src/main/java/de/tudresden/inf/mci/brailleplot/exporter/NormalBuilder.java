package de.tudresden.inf.mci.brailleplot.exporter;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import java.util.Iterator;

/**
 * Class representing a normal Document (for example a .txt) to print without
 * any Escapesequences.
 * @author Andrey Ruzhanskiy
 */
public class NormalBuilder extends AbstractDocumentBuilder {

    @Override
    public byte[] assemble(final MatrixData data) {
        if(data == null) {
            throw new NullPointerException();
        }

        //6 Point Braille
        Iterator iter = data.getDotIterator(2, 3);
        while (iter.hasNext()){
            for (int i = 0; i < 6; i++) {

            }
        }
        return null;
    }
}
