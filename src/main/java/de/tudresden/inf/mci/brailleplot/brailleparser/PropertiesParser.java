package de.tudresden.inf.mci.brailleplot.brailleparser;

import de.tudresden.inf.mci.brailleplot.util.GeneralResource;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Class representing a properties parser.
 * @author Andrey Ruzhanskiy
 * @version 12.07.2019
 */

public class PropertiesParser extends AbstractBrailleTableParser {
    private Properties mProperties = new Properties();

    /**
     * Constructor for properties parser. Takes a filePath to the braille table file with the .properties file extension.
     * @param filePath The path to the braille table.
     * @throws RuntimeException If the file Path does not exists.
     */
    public PropertiesParser(final String filePath) {
        Objects.requireNonNull(filePath);

        try (InputStream stream = new GeneralResource(filePath).getInputStream()) {  // use input stream (jar resource) instead of file input stream.
            mProperties.load(stream);
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
    public int getByteAsIntBackEnd(final String key) {
        Objects.requireNonNull(key);
        return Integer.parseInt(mProperties.getProperty(key));
    }

    @Override
    public String getCharToBraille(final String key) {
        String result = (String) mProperties.get(key);
        if (Objects.isNull(result)) {
            throw new RuntimeException("Could not find the letter: '" + key + "' in the table! Maybe using the wrong"
                    + "semantic table in the config?");
        }
        return result;

    }
}
