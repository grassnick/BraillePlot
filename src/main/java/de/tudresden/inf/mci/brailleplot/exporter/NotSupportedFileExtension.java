package de.tudresden.inf.mci.brailleplot.exporter;

/**
 * Exception Class for not recogniced/not supported FIleExtension for brailletables.
 * Used in NormalBuilder.
 * @author Andrey Ruzhanskiy
 * @version 11.07.2019
 */

public class NotSupportedFileExtension extends Exception {

    public NotSupportedFileExtension() { }

    public NotSupportedFileExtension(final String message) {
        super(message);
    }

    public NotSupportedFileExtension(final Throwable cause) {
        super(cause);
    }

    public NotSupportedFileExtension(final String message, final Throwable cause) {
        super(message, cause);
    }
}
