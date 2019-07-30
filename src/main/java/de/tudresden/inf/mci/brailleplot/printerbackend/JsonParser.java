package de.tudresden.inf.mci.brailleplot.printerbackend;

/**
 * Class representing a Json Parser entity.
 * @author Andrey Ruzhanskiy
 * @version 12.07.2019
 */
public class JsonParser implements AbstractBrailleTableParser {

    /**
     * Constructer for an Json Parser.
     * @param filePath File path to the Brailletable.
     */
    JsonParser(final String filePath) {
        throw new UnsupportedOperationException();
    }


    /**
     * Method for querying the BrailleTable.
     * @param key Braillecell, represented as String ("111000).
     * @return The Byte(int) representing the Braillecell specified in the BrailleTable.
     */
    @Override
    public int getValue(final String key) {
        throw new UnsupportedOperationException();
    }
}
