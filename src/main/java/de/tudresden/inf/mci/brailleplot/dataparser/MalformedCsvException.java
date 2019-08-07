package de.tudresden.inf.mci.brailleplot.dataparser;

/**
 * This Exception is thrown when parsed CSV files are malformed.
 */
public class MalformedCsvException extends RuntimeException {

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
