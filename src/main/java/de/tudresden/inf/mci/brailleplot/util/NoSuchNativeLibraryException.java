package de.tudresden.inf.mci.brailleplot.util;

/**
 * Indicates, some error has occurred while trying to load a native library from the java resources.
 * @author Georg Graßnick
 * @version 2019.09.26
 */
public class NoSuchNativeLibraryException extends RuntimeException {

        public NoSuchNativeLibraryException() { }

        public NoSuchNativeLibraryException(final String message) {
            super(message);
        }

        public NoSuchNativeLibraryException(final Throwable cause) {
            super(cause);
        }

        public NoSuchNativeLibraryException(final String message, final Throwable cause) {
            super(message, cause);
        }
}
