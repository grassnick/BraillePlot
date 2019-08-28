package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;

import java.util.Objects;

/**
 * ScatterPlotter. Provides a plotting algorithm for scatter plot data. Extends Plotter.
 * @author Richard Schmidt
 */
public final class ScatterPlotter extends AbstractPlotter implements Plotter<ScatterPlot> {

    // constants
    private static final double STEPSIZE = 0.5;
    private static final int XTICS = 5;
    private static final int YTICS = 5;

    /**
     * Constructor. Create a new plotter for instances of {@link ScatterPlot}.
     */
    public ScatterPlotter() {
        mStepSize = STEPSIZE;
        mNumberXTics = XTICS;
        mNumberYTics = YTICS;
    }

    /**
     * Plots a {@link ScatterPlot} instance onto a {@link PlotCanvas}.
     * @param diagram An instance of {@link ScatterPlot} representing the scatter plot.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas}, this is
     * to display the given diagram.
     */
    @Override
    public void plot(final ScatterPlot diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException {

        mDiagram = Objects.requireNonNull(diagram);
        mCanvas = Objects.requireNonNull(canvas);
        mData = mCanvas.getCurrentPage();
        mCanvas.readConfig();
        mResolution = mCanvas.getResolution();
        mPageWidth = mCanvas.getPrintableWidth();
        mPageHeight = mCanvas.getPrintableHeight();

        // calculating ranges
        double xRange = mDiagram.getMaxX() - mDiagram.getMinX();
        double yRange = mDiagram.getMaxY() - mDiagram.getMinY();

        drawAxes();

    }
}
