package de.tudresden.inf.mci.brailleplot.brailleparser;

/**
 * Class representing a Json Parser entity.
 * @author Andrey Ruzhanskiy
 * @version 12.07.2019
 */
public class JsonParser implements AbstractBrailleTableParser {

    /**
     * Currently not supported.
     * @param filePath File path to the Braille table.
     */
    public JsonParser(final String filePath) {
        throw new UnsupportedOperationException();
    }


    /**
     * Currently not supported.
     * @param key Braille cell, represented as String ("111000).
     * @return The Byte(int) representing the Braille cell specified in the BrailleTable.
     */
    @Override
    public int getValue(final String key) {
        throw new UnsupportedOperationException();
    }
}
