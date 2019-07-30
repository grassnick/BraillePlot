package de.tudresden.inf.mci.brailleplot.printerbackend;

import de.tudresden.inf.mci.brailleplot.printabledata.BrailleCell6;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Objects;


/**
 * Class representing a normal Document (for example a .txt) to print without
 * any Escapesequences.
 * @author Andrey Ruzhanskiy
 * @version 12.07.2019
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class NormalBuilder extends AbstractDocumentBuilder<Boolean> {


    /**
     * Method for assembling the final document from the data parameter.
     * In Normalbuilder, it first sets the correct parser according to the file extension, then initializes the iterator
     * from the Matrixdata object and the Stream for writing bytes into an array and lastly loops through the Matrixdata
     * to build the correct Document.
     * @param data Raw Data to be printed without any escapesequences
     * @return the final, printable document.
     */
    @Override
    public byte[] assemble(final MatrixData<Boolean> data) {


        //Check if Null Object was given.
        mData = Objects.requireNonNull(data);
        // Setting the right parser, catch if not found and throw RuntimeException which can be handled.
        try {
            setParser();
        } catch (NotSupportedFileExtensionException e) {
            throw new RuntimeException();
        }

        // Get Iterator for Cells.
        Iterator<BrailleCell6<Boolean>> iter = mData.getBrailleCell6Iterator();

        // Set stream for final output.
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // Getting Width in BrailleCells.
        int width = mData.getColumnCount() / 2;

        // Declaration of  local variables for better readability.
        BrailleCell6 current;
        String key;
        int value;

        // Count Variable for the loop
        int i = 0;

        // Loop through data and write to stream.
        while (iter.hasNext()) {
            current = iter.next();
            key = current.getBitRepresentationFromBool();
            value = mParser.getValue(key);
            stream.write(value);
            i++;
            // Setting the Linebreaks
            if (i == width) {
                i = 0;
                stream.write(0x0D);
                stream.write(0x0A);
            }
        }
        return stream.toByteArray();
    }
}
