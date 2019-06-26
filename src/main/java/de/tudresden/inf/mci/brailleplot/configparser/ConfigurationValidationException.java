package de.tudresden.inf.mci.brailleplot.configparser;

/**
 * Exception that indicates some error has occurred while validating a property.
 * Most likely created by illegal property names or incompatible values.
 * @author Leonard Kupper
 * @version 2019.06.24
 */
public class ConfigurationValidationException extends Exception {

    public ConfigurationValidationException() { }

    public ConfigurationValidationException(final String message) {
        super(message);
    }

    public ConfigurationValidationException(final Throwable cause) {
        super(cause);
    }

    public ConfigurationValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
