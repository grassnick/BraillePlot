package de.tudresden.inf.mci.brailleplot.configparser;

/**
 * An interface for a validator that checks properties as (key, value) pairs.
 * @author Leonard Kupper
 * @version 2019.07.18
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

    /**
     * Check whether the given {@link Printer} configuration is complete, meaning that it contains all
     * properties that were declared as 'required' for the printer namespace.
     * @param printerConfig The {@link Printer} configuration instance to be checked.
     */
    void checkPrinterConfigComplete(Printer printerConfig);

    /**
     * Check whether the given {@link Format} configuration is complete, meaning that it contains all
     * properties that were declared as 'required' for the format namespace.
     * @param formatConfig The {@link Format} configuration instance to be checked.
     */
    void checkFormatConfigComplete(Format formatConfig);

    void setSearchPath(String searchPath);
}
