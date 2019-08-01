package de.tudresden.inf.mci.brailleplot.brailleparser;

/**
 * Defines an Interface which should be implemented in all Parsers of Brailletables.
 */
public interface AbstractBrailleTableParser {

    /**
     * Common Method for querying the BrailleTable.
     * @param key Braillecell, represented as String ("111000).
     * @return Byte, represented as int, corresponding to the given Braillecell.
     */
    int getValue(String key);

}
