package de.tudresden.inf.mci.brailleplot.printerbackend;

/**
 * Abstract class for extending purposes.
 */
public interface AbstractBrailleTableParser {

    /**
     * Common Method for querying the BrailleTable.
     * @param key Braillecell, represented as String ("111000).
     * @return Byte, represented as int, corresponding to the given Braillecell.
     */
    int getValue(String key);

}
