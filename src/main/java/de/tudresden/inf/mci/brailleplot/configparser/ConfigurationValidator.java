package de.tudresden.inf.mci.brailleplot.configparser;

/**
 * An interface for a validator that checks properties as (key, value) pairs.
 * @author Leonard Kupper
 * @version 2019.06.04
 */
public interface ConfigurationValidator {
    /**
     * Check whether a given pair of key and value is valid as a property or not.
     * This method should check the key to be a legal property name and the corresponding type.
     * @param key The property key/name
     * @param value The property value
     * @return A {@link ValidProperty} object representing the validated property.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    ValidProperty validate(String key, String value) throws ConfigurationValidationException;
}
