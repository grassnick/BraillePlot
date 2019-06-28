package de.tudresden.inf.mci.brailleplot.configparser;

/**
 * Exception that indicates some error has occurred while parsing a configuration file.
 * Most likely created by missing file or access problems.
 * @author Leonard Kupper
 * @version 2019.06.24
 */
public class ConfigurationParsingException extends Exception {

    public ConfigurationParsingException() { }

    public ConfigurationParsingException(final String message) {
        super(message);
    }

    public ConfigurationParsingException(final Throwable cause) {
        super(cause);
    }

    public ConfigurationParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
