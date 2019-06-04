package de.tudresden.inf.mci.brailleplot;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaPropertiesConfigurationValidator.
 * @author Leonard Kupper
 * @version 04.06.19
 */
public class JavaPropertiesConfigurationValidator implements ConfigurationValidator {

    private final HashMap<String, String> mValidPrinterProperties = new HashMap<>();
    //private final HashMap<String, String> mValidFormatProperties = new HashMap<>();

    /**
     * JavaPropertiesConfigurationValidator.
     */
    public JavaPropertiesConfigurationValidator() {
        // Definition of valid printer properties
        mValidPrinterProperties.putAll(Map.ofEntries(
                Map.entry("printer.name", "String"),
                Map.entry("printer.maxChars", "int"),
                Map.entry("printer.maxWidth", "int")
        ));
    }

    /**
     * This method validates a Java Property key->value pair against a predefined map of properties and their
     * regarding type restrictions. Invalid pairs will cause a RuntimeException.
     * @param key
     * The Java Property key
     * @param value
     * The Java Property value
     */
    public ValidProperty validate(final String key, final String value) {
        /*
        ArrayList<String> keyLayers = new ArrayList<>(Arrays.asList(key.split("\\.", mMaxKeyLayers)));
        String prefix = keyLayers.remove(0);
        if (prefix.equals(mPrinterPrefix)) {
            String printerPropertyName = "";
            while (!keyLayers.isEmpty()) {
                printerPropertyName = printerPropertyName.concat(keyLayers.remove(0));
            }
            addProperty(printerPropertyName, value);
        } else if (prefix.equals(mFormatPrefix)) {
            String formatName = keyLayers.remove(0);
            String formatPropertyName = "";
            while (!keyLayers.isEmpty()) {
                formatPropertyName = formatPropertyName.concat(keyLayers.remove(0));
            }
            addProperty(formatName, formatPropertyName, value);
        } else {
            throw new RuntimeException("Can't parse unknown configuration prefix: '" + prefix +
                    "' The given property was: '" + key + "=" + value + "'");
        }
        */
        return new FormatProperty("a4","foo", "bar");
    }
}
