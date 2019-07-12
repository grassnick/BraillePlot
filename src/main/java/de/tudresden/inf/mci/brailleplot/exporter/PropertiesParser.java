package de.tudresden.inf.mci.brailleplot.exporter;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Class representing a Properties Parser.
 * @author Andrey Ruzhanskiy
 * @version 12.07.2019
 */

public class PropertiesParser implements AbstractBrailleTableParser {
    private Properties mProperties = new Properties();

    /**
     * Constructor for PropertiesParser. Takes an filePath to the Brailletable file with the .properties filextension.
     * @param filePath The Path to the BrailleTable.
     * @throws RuntimeException if an I... TODO
     */
    PropertiesParser(String filePath) {
        FileInputStream stream;
        try {
            stream = new FileInputStream(filePath);
            mProperties.load(stream);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Method for querying the Byte (represented via int) for a given Cell from the BrailleTable.
     * @param key The Braillecell, represented as a String (for example "111000" for 6 BrailleCell).
     * @return The Byte(int) representing the Braillecell specified in the BrailleTable,
     */
    @Override
    public int getValue(String key) {
        return Integer.parseInt(mProperties.getProperty(key));
    }
}
