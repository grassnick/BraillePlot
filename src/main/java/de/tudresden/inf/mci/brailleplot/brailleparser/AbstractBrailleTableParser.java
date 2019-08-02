package de.tudresden.inf.mci.brailleplot.brailleparser;

/**
 * Defines an interface which should be implemented in all parsers of braille tables.
 */
public interface AbstractBrailleTableParser {

    /**
     * Common method for querying the braille table.
     * @param key Braille cell, represented as string ("111000).
     * @return Byte, represented as int, corresponding to the given braille cell.
     */
    int getValue(String key);

}
