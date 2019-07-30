package de.tudresden.inf.mci.brailleplot.printerbackend;

/**
 * Exception Class for not recogniced/not supported FileExtension for brailletables.
 * Used in  {@link de.tudresden.inf.mci.brailleplot.printerbackend.NormalBuilder}.
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
