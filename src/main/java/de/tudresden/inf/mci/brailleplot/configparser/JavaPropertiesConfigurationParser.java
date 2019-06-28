package de.tudresden.inf.mci.brailleplot.configparser;

import java.io.IOException;
import java.util.Properties;

/**
 * Concrete parser for configuration files in Java Property File format.
 * @author Leonard Kupper
 * @version 2019.06.26
 */
public final class JavaPropertiesConfigurationParser extends ConfigurationParser {

    private Properties mProperties = new Properties();

    /**
     * Constructor.
     *
     * Parse the configuration from a JAVA Property File (.properties) without a default configuration.
     * @param filePath The path of the JAVA Property File.
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    public JavaPropertiesConfigurationParser(
            final String filePath
    ) throws ConfigurationParsingException, ConfigurationValidationException {
        setValidator(new JavaPropertiesConfigurationValidator());
        parseConfigFile(filePath);
    }

    /**
     * Constructor.
     *
     * Parse the configuration from a Java Property File (.properties) with a given default configuration.
     * @param filePath The path of the Java Property File.
     * @param defaultPrinter A {@link Printer} object containing the default properties or null for no default to be set.
     * @param defaultFormat A {@link Format} object containing the default properties or null for no default to be set.
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    public JavaPropertiesConfigurationParser(
            final String filePath,
            final Printer defaultPrinter,
            final Format defaultFormat
    ) throws ConfigurationParsingException, ConfigurationValidationException {
        setValidator(new JavaPropertiesConfigurationValidator());
        setDefaults(defaultPrinter, defaultFormat);
        parseConfigFile(filePath);
    }

    /**
     * Concrete internal algorithm used for parsing the Java Property File.
     * This method is called by ({@link #parseConfigFile(String)}).
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    protected void parse() throws ConfigurationParsingException, ConfigurationValidationException {
        // Load properties from the .properties file
        try {
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
