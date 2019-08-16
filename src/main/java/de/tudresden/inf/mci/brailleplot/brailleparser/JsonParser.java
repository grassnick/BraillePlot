package de.tudresden.inf.mci.brailleplot.brailleparser;

/**
 * Class representing a Json parser entity.
 * @author Andrey Ruzhanskiy
 * @version 12.07.2019
 */
public class JsonParser extends AbstractBrailleTableParser {

    /**
     * Currently not supported.
     * @param filePath File path to the braille table.
     */
    public JsonParser(final String filePath) {
        throw new UnsupportedOperationException();
    }


    /**
     * Currently not supported.
     * @param key Braille cell, represented as string ("111000).
     * @return The byte(int) representing the Braille cell specified in the braille table.
     */
    @Override
    public int getValue(final String key) {
        throw new UnsupportedOperationException();
    }
}
