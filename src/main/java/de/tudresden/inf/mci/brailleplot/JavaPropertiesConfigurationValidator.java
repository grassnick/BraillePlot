package de.tudresden.inf.mci.brailleplot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * JavaPropertiesConfigurationValidator.
 * @author Leonard Kupper
 * @version 04.06.19
 */
public class JavaPropertiesConfigurationValidator implements ConfigurationValidator {

    private final HashMap<String, Predicate<String>> mValidPrinterProperties = new HashMap<>();
    private final HashMap<String, Predicate<String>> mValidFormatProperties = new HashMap<>();

    /**
     * JavaPropertiesConfigurationValidator.
     */
    public JavaPropertiesConfigurationValidator() {

        // Definition of type checker predicates
        Predicate<String> requireEmpty = String::isEmpty;
        Predicate<String> requireNotEmpty = requireEmpty.negate();
        Predicate<String> requireInteger = JavaPropertiesConfigurationValidator::checkIfInteger;
        Predicate<String> requireFloat = JavaPropertiesConfigurationValidator::checkIfFloat;
        Predicate<String> requireBoolean = JavaPropertiesConfigurationValidator::checkIfBoolean;
        Predicate<String> requirePositive = JavaPropertiesConfigurationValidator::checkIfPositive;

        // Definition of valid printer properties
        Map<String, Predicate<String>> p = new HashMap<>();
        p.put("name", requireNotEmpty);
        p.put("minCharsPerLine", requireInteger.and(requirePositive));
        p.put("maxCharsPerLine", requireInteger.and(requirePositive));
        p.put("minLinesPerPage", requireInteger.and(requirePositive));
        p.put("maxLinesPerPage", requireInteger.and(requirePositive));
        p.put("equidistantSupport", requireBoolean);
        p.put("minCharacterDistance", requireFloat.and(requirePositive));
        p.put("maxCharacterDistance", requireFloat.and(requirePositive));
        p.put("minLineDistance", requireFloat.and(requirePositive));
        p.put("maxLineDistance", requireFloat.and(requirePositive));

        // Definition of valid format properties
        Map<String, Predicate<String>> f = new HashMap<>();
        f.put("pageWidth", requireInteger.and(requirePositive));
        f.put("pageHeight", requireInteger.and(requirePositive));

        // Add definitions
        mValidPrinterProperties.putAll(p);
        mValidFormatProperties.putAll(f);
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
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        int maxParts = 3;
        String[] keyParts = key.split("\\.", maxParts);
        String prefix = keyParts[0];

        // Decide whether printer or format property and do lookup in respective validation table.
        if (prefix.equals("printer")) {
            String propertyName = keyParts[1] + ((keyParts.length > 2) ? keyParts[2] : "");
            validationLookup(mValidPrinterProperties, propertyName, value);
            return new PrinterProperty(propertyName, value);
        } else if (prefix.equals("format")) {
            String formatName = keyParts[1];
            String propertyName = keyParts[2];
            validationLookup(mValidFormatProperties, propertyName, value);
            return new FormatProperty(formatName, propertyName, value);
        } else {
            throw new RuntimeException("Invalid property prefix: " + prefix);
        }
    }

    private void validationLookup(HashMap<String, Predicate<String>> validation, String propertyName, String value) {
        // Is the property valid?
        if (!validation.containsKey(propertyName)) {
            throw new RuntimeException("Invalid property name: " + propertyName);
        }
        // Check against its type requirement predicate
        if (!validation.get(propertyName).test(value)) {
            throw new RuntimeException("Invalid value '" + value + "' for property '" + propertyName + "'");
        }
    }

    private static boolean checkIfInteger(final String value) {
        if (!Pattern.matches("-?[0-9]+", value)) {
            return false;
        }
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    private static boolean checkIfFloat(final String value) {
        //if (!Pattern.matches("-?[0-9]+", value)) {
        //    return false;
        //}
        try {
            Float.parseFloat(value);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    private static boolean checkIfBoolean(final String value) {
        return Pattern.matches("(?i)^true$|^false$", value);
    }

    private static boolean checkIfPositive(final String value) {
        try {
            return (Double.parseDouble(value) >= 0);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
