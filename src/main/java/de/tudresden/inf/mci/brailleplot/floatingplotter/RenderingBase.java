package de.tudresden.inf.mci.brailleplot.floatingplotter;

import de.tudresden.inf.mci.brailleplot.layout.PlotMatrix;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * RenederingBase. This class acts as a wrapper for multiple {@link Plotter} instances.
 * The floatingplotter instances can be registered at runtime. The main purpose of the class is to take {@link Renderable}
 * representations of any type and select the correct concrete floatingplotter.
 * @author Leonard Kupper and Richard Schmidt
 */
public class RenderingBase {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    private HashMap<Class<? extends Renderable>, Plotter> mPlottingAlgorithms;
    private PlotMatrix mMatrix;

    public RenderingBase() {
        mPlottingAlgorithms = new HashMap<Class<? extends Renderable>, Plotter>();
        mLogger.info("RenderingBase instance created");
    }

    /**
     * Registers a plotting algorithm by its supported renderable class in mPlottingAlgorithms.
     * @param plotter
     */
    public void registerPlotter(final FunctionalPlotter plotter) {
        mLogger.trace("Registering new floatingplotter {} for type {}", plotter, plotter.getSupportedRenderableClass().getSimpleName());
        if (mPlottingAlgorithms.containsKey(plotter.getSupportedRenderableClass())) {
            mLogger.warn("Already registered floatingplotter {} will be overwritten!", mPlottingAlgorithms.get(plotter.getSupportedRenderableClass()));
        }

        mPlottingAlgorithms.put(plotter.getSupportedRenderableClass(), plotter);

        mLogger.info("Plotter has been registered for renderable type {}",
                plotter.getSupportedRenderableClass().getSimpleName());
        mLogger.trace("Current count of registered rasterizers: {}", mPlottingAlgorithms.size());
    }
}
