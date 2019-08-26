package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;

/**
 * ScatterPlotter. Provides a plotting algorithm for scatter plot data. Extends Plotter.
 * @author Richard Schmidt
 */
final class ScatterPlotter implements Plotter<ScatterPlot> {

    ScatterPlot mDiagram;
    PlotCanvas mCanvas;
    FloatingPointData mData;

    /**
     * Constructor. Create a new plotter for instances of {@link ScatterPlot}.
     */
    ScatterPlotter() {
    }

    /**
     * Plots a {@link ScatterPlot} instance onto a {@link PlotCanvas}.
     * @param data An instance of {@link ScatterPlot} representing the scatter plot.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas}, this is
     * to display the given diagram.
     */
    @Override
    public void plot(final ScatterPlot data, final PlotCanvas canvas) throws InsufficientRenderingAreaException {
        return;
    }
}
