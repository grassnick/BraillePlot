package de.tudresden.inf.mci.brailleplot.brailleparser;


/**
 * Class representing a Xmp Parser entity.
 * @author Andrey Ruzhanskiy
 * @version 12.07.2019
 */
public class XmlParser implements AbstractBrailleTableParser {


    /**
     * Currently not supported.
     *
     * @param filePath
     */
    public XmlParser(final String filePath) {
        throw new UnsupportedOperationException();
    }


    /**
     * Currently not supported.
     * @param key Braillecell, represented as String ("111000).
     * @return The Byte(int) representing the Braillecell specified in the BrailleTable.
     */
    @Override
    public int getValue(final String key) {
        throw new UnsupportedOperationException();
    }
}
