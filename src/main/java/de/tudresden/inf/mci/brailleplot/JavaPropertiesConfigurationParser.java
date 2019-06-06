package de.tudresden.inf.mci.brailleplot;

import java.io.IOException;
import java.util.Properties;

/**
 * JavaPropertiesConfigurationParser.
 * @author Leonard Kupper
 * @version 04.06.19
 */
public final class JavaPropertiesConfigurationParser extends ConfigurationParser {

    private Properties mProperties = new Properties();

    public JavaPropertiesConfigurationParser(
            final String filePath,
            final JavaPropertiesConfigurationValidator validator
    ) {
        mValidator = validator;
        parseConfigFile(filePath);
    }

    public JavaPropertiesConfigurationParser(
            final String filePath,
            final JavaPropertiesConfigurationValidator validator,
            final Printer defaultPrinter,
            final Format defaultFormat
    ) {
        mValidator = validator;
        setDefaults(defaultPrinter, defaultFormat);
        parseConfigFile(filePath);
    }


    protected Boolean parse() {
        // Load properties from the .properties file
        try {
            mProperties.load(mInput);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        // Iterate over all properties as key -> value pairs
        for (String key : mProperties.stringPropertyNames()) {
            String value = mProperties.getProperty(key);
            //System.out.println(key + "=" + value);
            //try {
                parseProperty(key, value);
            //} catch (RuntimeException e) {
            //    System.out.println("Exception while parsing: " + e.getMessage());
            //}
        }
        return true;
    }

    private void parseProperty(final String key, final String value) {
        ValidProperty property = mValidator.validate(key, value);
        if (property instanceof FormatProperty) {
            addProperty((FormatProperty) property);
        }
        if (property instanceof PrinterProperty) {
            addProperty((PrinterProperty) property);
        }
    }
}
