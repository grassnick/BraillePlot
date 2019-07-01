package de.tudresden.inf.mci.brailleplot.rendering;

/**
 * Exception that indicates too few space available to display the amount of data contained in the given diagram representation.
 * Typical circumstances that lead the rasterizer/plotter to throw this exception are that there are simply too much elements to
 * display them physically in the given raster/area or that the value range cannot be mapped to the given output resolution.
 * @author Leonard Kupper
 * @version 2019.07.01
 */
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
