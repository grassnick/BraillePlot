package de.tudresden.inf.mci.brailleplot;

import java.io.IOException;
import java.util.Properties;
import java.util.HashMap;
import java.util.Set;

/**
 * JavaPropertiesConfigurationParser.
 */
public final class JavaPropertiesConfigurationParser extends ConfigurationParser {

    private Properties mProperties = new Properties();
    private String mPrinterPrefix = "printer";
    private String mFormatPrefix = "format";
    private String mDot = ".";

    public JavaPropertiesConfigurationParser() {
        mPrinter = new Printer();
        mFormats = new HashMap<String, Format>();
    }

    public Boolean parse() {
        try {
            mProperties.load(mInput);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        System.out.println(mProperties.toString());
        Set<String> propertyNames = mProperties.stringPropertyNames();
        for (String name : propertyNames) {
            String value = mProperties.getProperty(name);
            ConfigProperties targetConfiguration; // This will receive the property entry
            int fullPrefixLength;
            // Check property name prefix.
            // Is it a printer property ...
            if (name.startsWith(mPrinterPrefix)) {
                fullPrefixLength = (mPrinterPrefix + mDot).length();
                targetConfiguration = mPrinter;
            } else if (name.startsWith(mFormatPrefix)) { // ... or a format definition?
                String formatName = readFormatNameFromProperty(name);
                fullPrefixLength = (mFormatPrefix + mDot + formatName + mDot).length();
                // add format to Map if not already added
                if (!mFormats.containsKey(formatName)) {
                    Format newFormat = new Format();
                    mFormats.put(formatName, newFormat);
                }
                targetConfiguration = mFormats.get(formatName);
            } else {
                // Raise Exception?
                return false;
            }
            String propertyName = name.substring(fullPrefixLength);
            targetConfiguration.setProperty(propertyName, value);
        }
        return true;
    }

    private String readFormatNameFromProperty(final String propertyName) {
        return propertyName.split("\\.")[1];
    }
}
