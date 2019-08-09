package de.tudresden.inf.mci.brailleplot.brailleparser;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Class representing a properties parser.
 * @author Andrey Ruzhanskiy
 * @version 12.07.2019
 */

public class PropertiesParser implements AbstractBrailleTableParser {
    private Properties mProperties = new Properties();

    /**
     * Constructor for properties parser. Takes a filePath to the braille table file with the .properties file extension.
     * @param filePath The path to the braille table.
     * @throws RuntimeException If the file Path does not exists.
     */
    public PropertiesParser(final String filePath) {
        Objects.requireNonNull(filePath);

        FileInputStream stream;
        try {
            stream = new FileInputStream(filePath);
            mProperties.load(stream);
            stream.close();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Method for querying the byte (represented via int) for a given cell from the braille table.
     * @param key The braille cell, represented as a string (for example "111000" for 6 braille cell).
     * @return The byte(int) representing the braille cell specified in the braille table,
     */
    @Override
    public int getValue(final String key) {
        Objects.requireNonNull(key);
        return Integer.parseInt(mProperties.getProperty(key));
    }
}
