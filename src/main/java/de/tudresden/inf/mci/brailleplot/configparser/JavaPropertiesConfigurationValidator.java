package de.tudresden.inf.mci.brailleplot.configparser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Concrete validator for properties parsed from configuration files in Java Property File format.
 * @author Leonard Kupper
 * @version 2019.06.26
 */
public class JavaPropertiesConfigurationValidator implements ConfigurationValidator {

    private final HashMap<String, Predicate<String>> mValidPrinterProperties = new HashMap<>();
    private final HashMap<String, Predicate<String>> mValidFormatProperties = new HashMap<>();

    JavaPropertiesConfigurationValidator() {

        // Definition of type checker predicates
        Predicate<String> requireEmpty = String::isEmpty;
        Predicate<String> requireNotEmpty = requireEmpty.negate();
        Predicate<String> requireInteger = JavaPropertiesConfigurationValidator::checkIfInteger;
        Predicate<String> requireDouble = JavaPropertiesConfigurationValidator::checkIfDouble;
        Predicate<String> requireBoolean = JavaPropertiesConfigurationValidator::checkIfBoolean;
        Predicate<String> requirePositive = JavaPropertiesConfigurationValidator::checkIfPositive;

        // Definition of valid printer properties
        Map<String, Predicate<String>> p = new HashMap<>();
        p.put("name", requireNotEmpty);
        p.put("min.charsPerLine", requireInteger.and(requirePositive));
        p.put("max.charsPerLine", requireInteger.and(requirePositive));
        p.put("min.linesPerPage", requireInteger.and(requirePositive));
        p.put("max.linesPerPage", requireInteger.and(requirePositive));
        p.put("equidistantSupport", requireBoolean);
        p.put("min.characterDistance", requireDouble.and(requirePositive));
        p.put("max.characterDistance", requireDouble.and(requirePositive));
        p.put("min.lineDistance", requireDouble.and(requirePositive));
        p.put("max.lineDistance", requireDouble.and(requirePositive));

        // Definition of valid format properties
        Map<String, Predicate<String>> f = new HashMap<>();
        f.put("page.width", requireInteger.and(requirePositive));
        f.put("page.height", requireInteger.and(requirePositive));
        f.put("margin.top", requireInteger);
        f.put("margin.right", requireInteger);
        f.put("margin.bottom", requireInteger);
        f.put("margin.left", requireInteger);
        f.put("isPortrait", requireBoolean);

        // Add definitions
        mValidPrinterProperties.putAll(p);
        mValidFormatProperties.putAll(f);
    }

    /**
     * This method validates a Java Property (key, value) pair against a predefined map of properties and their
     * regarding type restriction predicates. Invalid pairs will cause a ConfigurationValidationException.
     * @param key The Java Property key
     * @param value The Java Property value
     * @return A {@link ValidProperty} object representing the validated property.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    public ValidProperty validate(final String key, final String value) throws ConfigurationValidationException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        final int maxParts = 3;
        String[] keyParts = key.split("\\.", maxParts);
        String prefix = keyParts[0];

        // Decide whether printer or format property and do lookup in respective validation table.
        if (prefix.equals("printer")) {
            if (keyParts.length <= 1) {
                throw new ConfigurationValidationException("Invalid printer property key: " + key);
            }
            String propertyName = keyParts[1];
            if (keyParts.length > 2) {
                propertyName = propertyName + "." + keyParts[2];
            }
            validationLookup(mValidPrinterProperties, propertyName, value);
            return new PrinterProperty(propertyName, value);
        } else if (prefix.equals("format")) {
            if (keyParts.length <= 2) {
                throw new ConfigurationValidationException("Invalid format property key: " + key);
            }
            String formatName = keyParts[1];
            String propertyName = keyParts[2];
            validationLookup(mValidFormatProperties, propertyName, value);
            return new FormatProperty(formatName, propertyName, value);
        } else {
            throw new ConfigurationValidationException("Invalid property prefix: " + prefix);
        }
    }

    private void validationLookup(
            final HashMap<String, Predicate<String>> validation,
            final String propertyName,
            final String value
    ) throws ConfigurationValidationException {
        // Is the property valid?
        if (!validation.containsKey(propertyName)) {
            throw new ConfigurationValidationException("Invalid property name: " + propertyName);
        }
        // Check against its type requirement predicate
        if (!validation.get(propertyName).test(value)) {
            throw new ConfigurationValidationException(
                    "Invalid value '" + value + "' for property '" + propertyName + "'"
            );
        }
    }

    private static boolean checkIfInteger(final String value) {
        if (!Pattern.matches("-?[0-9]+", value)) {
            return false;
        }
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean checkIfDouble(final String value) {
        //if (!Pattern.matches("-?[0-9]+", value)) {
        //    return false;
        //}
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
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
            return false;
        }
    }
}
