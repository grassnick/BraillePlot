package de.tudresden.inf.mci.brailleplot.rendering;

public class InsufficientRenderingAreaException extends Exception {

    public InsufficientRenderingAreaException() { }

    public InsufficientRenderingAreaException(final String message) {
        super(message);
    }

    public InsufficientRenderingAreaException(final Throwable cause) {
        super(cause);
    }

    public InsufficientRenderingAreaException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
