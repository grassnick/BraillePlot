package de.tudresden.inf.mci.brailleplot.printerbackend;

/**
 * Exception class for not recogniced/not supported file extension for braille tables.
 * Used in  {@link NormalBuilder}.
 * @author Andrey Ruzhanskiy
 * @version 11.07.2019
 */

public class NotSupportedFileExtensionException extends Exception {

    public NotSupportedFileExtensionException() { }

    public NotSupportedFileExtensionException(final String message) {
        super(message);
    }

    public NotSupportedFileExtensionException(final Throwable cause) {
        super(cause);
    }

    public NotSupportedFileExtensionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
