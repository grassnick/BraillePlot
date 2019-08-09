package de.tudresden.inf.mci.brailleplot.csvparser;

/**
 * Exception class.
 * Indicates, that the parsed CSV file was malformed.
 * Most likely created by malformed user input.
 * @author Georg Graßnick
 * @version 2019.08.08
 */
public class MalformedCsvException extends RuntimeException {

    public MalformedCsvException() { }

    public MalformedCsvException(final String message) {
        super(message);
    }

    public MalformedCsvException(final Throwable cause) {
        super(cause);
    }

    public MalformedCsvException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
