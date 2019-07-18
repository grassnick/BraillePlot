package de.tudresden.inf.mci.brailleplot.configparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Concrete validator for properties parsed from configuration files in Java Property File format.
 * @author Leonard Kupper
 * @version 2019.06.26
 */
class JavaPropertiesConfigurationValidator implements ConfigurationValidator {

    private final String PRINTER_PREFIX = "printer";
    private final String FORMAT_PREFIX = "format";

    private final HashMap<String, Predicate<String>> mValidPrinterProperties = new HashMap<>();
    private final HashMap<String, Predicate<String>> mValidFormatProperties = new HashMap<>();
    private final HashMap<String, ArrayList<String>> mCompletenessCheck = new HashMap<>();
    private final ArrayList<String> mRequiredPrinterProperties = new ArrayList<>();
    private final ArrayList<String> mRequiredFormatProperties = new ArrayList<>();

    JavaPropertiesConfigurationValidator() {

        // Reset state for check of required properties.
        resetCompletenessCheck();

        // Definition of type checker predicates
        Predicate<String> requireEmpty = String::isEmpty;
        Predicate<String> requireNotEmpty = requireEmpty.negate();
        Predicate<String> requireInteger = JavaPropertiesConfigurationValidator::checkIfInteger;
        Predicate<String> requireDouble = JavaPropertiesConfigurationValidator::checkIfDouble;
        Predicate<String> requireBoolean = JavaPropertiesConfigurationValidator::checkIfBoolean;
        Predicate<String> requirePositive = JavaPropertiesConfigurationValidator::checkIfPositive;
        Predicate<String> requireFileExists = JavaPropertiesConfigurationValidator::checkIfFileExists;

        // Definition of valid printer properties
        Map<String, Predicate<String>> p = new HashMap<>();
        definePrinterProperty("name", requireNotEmpty);
        definePrinterProperty("mode", requireNotEmpty);
        definePrinterProperty("indent.top", requireDouble.and(requirePositive));
        definePrinterProperty("indent.left", requireDouble.and(requirePositive));
        definePrinterProperty("indent.bottom", requireDouble.and(requirePositive));
        definePrinterProperty("indent.right", requireDouble.and(requirePositive));
        definePrinterProperty("raster.indent.top", requireInteger.and(requirePositive));
        definePrinterProperty("raster.indent.left", requireInteger.and(requirePositive));
        definePrinterProperty("raster.indent.bottom", requireInteger.and(requirePositive));
        definePrinterProperty("raster.indent.right", requireInteger.and(requirePositive));
        definePrinterProperty("raster.dotDistance.horizontal", requireDouble.and(requirePositive));
        definePrinterProperty("raster.dotDistance.vertical", requireDouble.and(requirePositive));
        definePrinterProperty("raster.cellDistance.horizontal", requireDouble.and(requirePositive));
        definePrinterProperty("raster.cellDistance.vertical", requireDouble.and(requirePositive));
        definePrinterProperty("raster.dotDiameter", requireDouble.and(requirePositive));
        definePrinterProperty("floatingDotSupport", requireBoolean);
        definePrinterProperty("min.characterDistance", requireDouble.and(requirePositive), false);
        definePrinterProperty("max.characterDistance", requireDouble.and(requirePositive), false);
        definePrinterProperty("min.lineDistance", requireDouble.and(requirePositive), false);
        definePrinterProperty("max.lineDistance", requireDouble.and(requirePositive), false);
        definePrinterProperty("brailletable", requireFileExists);

        // Definition of valid format properties
        Map<String, Predicate<String>> f = new HashMap<>();
        defineFormatProperty("page.width", requireInteger.and(requirePositive));
        defineFormatProperty("page.height", requireInteger.and(requirePositive));
        defineFormatProperty("margin.top", requireInteger.and(requirePositive));
        defineFormatProperty("margin.right", requireInteger.and(requirePositive));
        defineFormatProperty("margin.bottom", requireInteger.and(requirePositive));
        defineFormatProperty("margin.left", requireInteger.and(requirePositive));
        defineFormatProperty("isPortrait", requireBoolean, false);

    }

    private void definePrinterProperty(String propertyName, Predicate<String> validation) {
        definePrinterProperty(propertyName, validation, true);
    }
    private void definePrinterProperty(String propertyName, Predicate<String> validation, boolean required) {
        defineProperty(mValidPrinterProperties, propertyName, validation, required, mRequiredPrinterProperties);
    }
    private void defineFormatProperty(String propertyName, Predicate<String> validation) {
        defineFormatProperty(propertyName, validation, true);
    }
    private void defineFormatProperty(String propertyName, Predicate<String> validation, boolean required) {
        defineProperty(mValidFormatProperties, propertyName, validation, required, mRequiredFormatProperties);
    }
    private void defineProperty(HashMap<String, Predicate<String>> defTable, String propertyName,
                                Predicate<String> validation, boolean required, ArrayList<String> checkList) {
        defTable.put(propertyName, validation);
        if (Objects.nonNull(checkList) && required) {
            checkList.add(propertyName);
        }
    }

    /**
     * This method validates a Java Property (key, value) pair against a predefined map of properties and their
     * regarding type restriction predicates. Invalid pairs will cause a ConfigurationValidationException.
     * @param key The Java Property key
     * @param value The Java Property value
     * @return A {@link ValidProperty} object representing the validated property.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    @Override
    public ValidProperty validate(final String key, final String value) throws ConfigurationValidationException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        final int maxParts = 3;
        String[] keyParts = key.split("\\.", maxParts);
        String prefix = keyParts[0];

        // Decide whether printer or format property and do lookup in respective validation table.
        if (prefix.equals(PRINTER_PREFIX)) {
            if (keyParts.length <= 1) {
                throw new ConfigurationValidationException("Invalid printer property key: " + key);
            }
            String propertyName = keyParts[1];
            if (keyParts.length > 2) {
                propertyName = propertyName + "." + keyParts[2];
            }
            validationLookup(mValidPrinterProperties, propertyName, value);
            updateCompletenessCheck(PRINTER_PREFIX, propertyName, mRequiredPrinterProperties);
            return new PrinterProperty(propertyName, value);
        } else if (prefix.equals(FORMAT_PREFIX)) {
            if (keyParts.length <= 2) {
                throw new ConfigurationValidationException("Invalid format property key: " + key);
            }
            String formatName = keyParts[1];
            String namespace = FORMAT_PREFIX + "." + formatName;
            String propertyName = keyParts[2];
            validationLookup(mValidFormatProperties, propertyName, value);
            updateCompletenessCheck(namespace, propertyName, mRequiredFormatProperties);
            return new FormatProperty(formatName, propertyName, value);
        } else {
            throw new ConfigurationValidationException("Invalid property prefix: " + prefix);
        }
    }

    @SuppressWarnings("unchecked")
    // mRequiredPrinterProperties is ArrayList<String>.
    private void resetCompletenessCheck() {
        mCompletenessCheck.clear();
        // This namespace is always required:
        mCompletenessCheck.put(PRINTER_PREFIX, (ArrayList<String>) mRequiredPrinterProperties.clone());
    }

    @SuppressWarnings("unchecked")
    // checklist must be an ArrayList<String> in first place.
    private void updateCompletenessCheck(String namespace, String propertyName, ArrayList<String> checklist) {
        // If this is a new namespace, first add the checklist for the namespace.
        if (!mCompletenessCheck.containsKey(namespace)) {
            mCompletenessCheck.put(namespace, (ArrayList<String>) checklist.clone());
        }
        // 'tick off' the given property from the checklist associated with the regarding namespace.
        mCompletenessCheck.get(namespace).remove(propertyName);
    }

    @Override
    public void assertComplete() {
        for (String namespace : mCompletenessCheck.keySet()) {
            List checklist = mCompletenessCheck.get(namespace);
            if (!checklist.isEmpty()) {
                throw new IllegalStateException("Incomplete validation. Missing required properties: '" + checklist +
                        "' in namespace '" + namespace + "'");
            }
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

    // Validation Predicates

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

    private static boolean checkIfFileExists(final String filePath){
        try {
            FileInputStream stream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
