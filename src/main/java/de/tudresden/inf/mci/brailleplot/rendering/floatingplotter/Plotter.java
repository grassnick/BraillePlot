package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;

/**
 * A functional interface for anything that is able to plot renderable data onto a matrix.
 * This interface also defines a static set of tool methods for basic operations on a plot's data container ({@link PlotCanvas}).
 * @param <T> The concrete class implementing {@link Renderable} which can be plotted by plotter.
 * @author Richard Schmidt
 */
@FunctionalInterface
public interface Plotter<T extends Renderable> {

    /**
     * Rasterizes a {@link Renderable} instance onto a {@link PlotCanvas}.
     * @param data The renderable representation.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the rasterizer output.
     * @throws InsufficientRenderingAreaException If too few space is available on the {@link PlotCanvas}
     * to display the given data.
     */
    void plot(T data, PlotCanvas canvas) throws InsufficientRenderingAreaException;
}
