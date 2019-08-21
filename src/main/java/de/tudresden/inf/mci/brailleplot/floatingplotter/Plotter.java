package de.tudresden.inf.mci.brailleplot.floatingplotter;

import de.tudresden.inf.mci.brailleplot.rendering.Renderable;

/**
 * Plotter. A functional interface for anything that is able to plot renderable data onto a matrix.
 * This interface also defines a static set of tool methods for basic operations on a plot's data container ({@link de.tudresden.inf.mci.brailleplot.layout.PlotMatrix}).
 * @param <T> The concrete class implementing {@link Renderable} which can be rasterized with the rasterizer.
 * @author Richard Schmidt
 */
public interface Plotter<T extends Renderable> {

}
