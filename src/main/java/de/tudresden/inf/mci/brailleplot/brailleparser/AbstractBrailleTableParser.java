package de.tudresden.inf.mci.brailleplot.brailleparser;

/**
 * Defines an Interface which should be implemented in all Parsers of Braille tables.
 */
public interface AbstractBrailleTableParser {

    /**
     * Common Method for querying the BrailleTable.
     * @param key Braille cell, represented as String ("111000).
     * @return Byte, represented as int, corresponding to the given Braille cell.
     */
    int getValue(String key);

}
