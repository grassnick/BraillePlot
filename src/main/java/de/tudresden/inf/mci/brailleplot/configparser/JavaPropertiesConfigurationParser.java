package de.tudresden.inf.mci.brailleplot.configparser;

import java.io.IOException;
import java.util.Properties;

/**
 * Concrete parser for configuration files in Java Property File format.
 * @author Leonard Kupper
 * @version 2019.07.18
 */
public final class JavaPropertiesConfigurationParser extends ConfigurationParser {

    private Properties mProperties = new Properties();

    /**
     * Constructor.
     *
     * Parse the configuration from a Java Property File (.properties) with a given default configuration.
     * @param filePath The path of the Java Property File.
     * @param defaultPath The path to the Java Property File containing the default properties.
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    public JavaPropertiesConfigurationParser(
            final String filePath,
            final String defaultPath
    ) throws ConfigurationParsingException, ConfigurationValidationException {
        setValidator(new JavaPropertiesConfigurationValidator());
        parseConfigFile(defaultPath, false);
        setDefaults(getPrinter(), getFormat("default"));
        parseConfigFile(filePath, true);
    }

    /**
     * Concrete internal algorithm used for parsing the Java Property File.
     * This method is called by ({@link #parseConfigFile(String, boolean)}).
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    protected void parse() throws ConfigurationParsingException, ConfigurationValidationException {
        // Load properties from the .properties file
        try {
            // Reset java property instance
            mProperties.clear();
            mProperties.load(getInput());
        } catch (IOException e) {
            throw new ConfigurationParsingException("Unable to load properties from file.", e);
        }
        // Iterate over all properties as key -> value pairs
        for (String key : mProperties.stringPropertyNames()) {
            String value = mProperties.getProperty(key);
            parseProperty(key, value);
        }
    }

    private void parseProperty(final String key, final String value) throws ConfigurationValidationException {
        ValidProperty property = getValidator().validate(key, value);
        if (property instanceof FormatProperty) {
            addProperty((FormatProperty) property);
        }
        if (property instanceof PrinterProperty) {
            addProperty((PrinterProperty) property);
        }
    }
}
