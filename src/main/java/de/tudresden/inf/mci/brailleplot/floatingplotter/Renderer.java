package de.tudresden.inf.mci.brailleplot.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.BarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renderer. Main interface for converting a diagram representation into a plottable format on Everest-D V4 and manages the current rendering context.
 * @author Leonard Kupper and Richard Schmidt
 */
public class Renderer {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    private RenderingBase mRenderingBase;

    public Renderer() {
        mLogger.info("Creating Renderer with default context");

        mLogger.trace("Instantiating default rendering base");
        // if no rendering base is given, create own rendering base with default set of algorithms
        RenderingBase renderingBase = new RenderingBase();

        mLogger.trace("Instantiating default plotting");

        // Default Algorithms:
        Plotter<BarChart> barChart = new BarChartPlotter();
        Plotter<ScatterPlot> scatterPlot = new ScatterPlotter();


        // Registering
        mLogger.trace("Registering default rasterizers");
        renderingBase.registerPlotter(new FunctionalPlotter<BarChart>(BarChart.class, barChart));
        renderingBase.registerPlotter(new FunctionalPlotter<ScatterPlot>(ScatterPlot.class, scatterPlot));

        setRenderingBase(renderingBase);
    }

    /**
     * Setter for mRenderingBase.
     * @param renderingBase
     */
    public void setRenderingBase(final RenderingBase renderingBase) {
        mRenderingBase = renderingBase;
    }
}
