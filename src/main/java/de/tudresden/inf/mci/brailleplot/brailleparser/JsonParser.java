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
    public int getByteAsInt(final String key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Currently not supported.
     * @param value Byte, as String represented (property files knows only Strings)
     * @return Braillecell, as String encoded : 123456.
     */
    @Override
    public String getDots(final String value) {
        throw new UnsupportedOperationException();
    }
}
