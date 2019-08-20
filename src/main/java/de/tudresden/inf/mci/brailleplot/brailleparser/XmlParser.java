package de.tudresden.inf.mci.brailleplot.brailleparser;


/**
 * Class representing a XML parser entity.
 * @author Andrey Ruzhanskiy
 * @version 12.07.2019
 */
public class XmlParser extends AbstractBrailleTableParser {


    /**
     * Currently not supported.
     *
     * @param filePath Path to the braille table.
     */
    public XmlParser(final String filePath) {
        throw new UnsupportedOperationException();
    }


    /**
     * Currently not supported.
     * @param key braille cell, represented as String ("111000).
     * @return The byte(int) representing the braille cell specified in the braille table.
     */
    @Override
    public int getByteAsIntBackEnd(final String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCharToBraille(String key) {
        throw new UnsupportedOperationException();
    }
}
