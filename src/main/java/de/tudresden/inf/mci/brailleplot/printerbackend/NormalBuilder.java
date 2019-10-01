package de.tudresden.inf.mci.brailleplot.printerbackend;

import de.tudresden.inf.mci.brailleplot.brailleparser.AbstractBrailleTableParser;
import de.tudresden.inf.mci.brailleplot.printabledata.BrailleCell6;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Objects;


/**
 * Class representing a normal document (for example a .txt) that should be printed without
 * any escape sequences.
 * @author Andrey Ruzhanskiy
 * @version 12.07.2019
 */
@SuppressWarnings("checkstyle:MagicNumber")
class NormalBuilder extends AbstractDocumentBuilder<Boolean> {


    /**
     * Constructor. Does not have any functionality. Should only be used in  {@link PrintDirector}
     */
    NormalBuilder() { }

    /**
     * Method for assembling the final document from the data parameter.
     * In normalbuilder, it first sets the correct parser according to the file extension, then initializes the iterator
     * from the {@link MatrixData} object and the stream for writing bytes into an array and lastly loops through the {@link MatrixData}
     * to build the correct document.
     * @param data Raw data to be printed without any escape sequences
     * @return the final, printable document.
     */
    @Override
    byte[] assemble(final MatrixData<Boolean> data) {


        //Check if null object was given.
        mData = Objects.requireNonNull(data);
        // Setting the right parser, catch if not found and throw RuntimeException which can be handled.
        try {
            mParser = AbstractBrailleTableParser.getParser(mData.getPrinterConfig(), "brailletable");
        } catch (NotSupportedFileExtensionException e) {
            throw new RuntimeException();
        }

        // Get iterator for cells.
        Iterator<BrailleCell6<Boolean>> iter = mData.getBrailleCell6Iterator();

        // Set stream for final output.
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // Getting width in braille cells.
        int width = mData.getColumnCount() / 2;

        // Declaration of  local variables for better readability.
        BrailleCell6 current;
        String key;
        int value;

        // Count variable for the loop
        int i = 0;

        // Loop through data and write to stream.
        while (iter.hasNext()) {
            current = iter.next();
            key = current.getBitRepresentationFromBool();
            value = mParser.getByteAsIntBackEnd(key);
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
