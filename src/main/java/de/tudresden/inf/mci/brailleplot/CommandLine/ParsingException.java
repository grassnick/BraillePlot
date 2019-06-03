package de.tudresden.inf.mci.brailleplot.CommandLine;

/**
 * Exception class.
 * Indicates, some error has occurred while parsing the command line parameters.
 * Most likely by created by malformed user input.
 * @author Georg Graßnick
 * @version 2019.05.31
 */
public class ParsingException extends Exception {

    public ParsingException() { }

    public ParsingException(final String message) {
        super(message);
    }

    public ParsingException(final Throwable cause) {
        super(cause);
    }

    public ParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
