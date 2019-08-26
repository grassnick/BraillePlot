package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;
import de.tudresden.inf.mci.brailleplot.rendering.ThrowingBiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FunctionalPlotter. This class implements a concrete floatingplotter via a functional interface.
 * The plotting algorithm to be used is passed to the constructor as lambda function, method reference or plot implementation.
 * @param <T> The concrete diagram class which can be plotted with the floatingplotter.
 * @author Leonard Kupper and Richard Schmidt
 */
public class FunctionalPlotter<T extends Renderable> implements Plotter {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    private Class<? extends T> mSupportedRenderableClass;
    private ThrowingBiConsumer<T, PlotCanvas, InsufficientRenderingAreaException> mPlottingAlgorithm;

    /**
     * Constructor. Creates a new plotter from either a given plotter implementation or (keep in mind that
     * Plotter is a functional interface) from a ThrowingBiConsumer&lt;T, PlotCanvas, InsufficientRenderingAreaException&gt; method reference.
     * @param supportedRenderableClass A reference to the accepted diagram class (e.g. 'BarChart.class')
     * @param plotter A reference to a Plotter instance.
     */
    public FunctionalPlotter(final Class<T> supportedRenderableClass, final Plotter<T> plotter) {
        mLogger.info("Creating new FunctionalRasterizer: Binding rasterizer reference (renderable type {}): {}",
                supportedRenderableClass.getSimpleName(), plotter);
        mSupportedRenderableClass = supportedRenderableClass;
        mPlottingAlgorithm = plotter::plot;
    }

    @Override
    public void plot(final Renderable data, final PlotCanvas canvas) throws InsufficientRenderingAreaException {
        // invoke the given rasterizing algorithm
        T safeData = safeCast(data);
        mLogger.trace("Delegating task to bound rasterizing algorithm {}", mPlottingAlgorithm);
        mPlottingAlgorithm.accept(safeData, canvas);
    }

    /**
     * Getter for supported renderable class.
     * @return class
     */
    Class<? extends T> getSupportedRenderableClass() {
        return mSupportedRenderableClass;
    }

    @SuppressWarnings("unchecked")
    // This is allowed, because the code that calls the rasterize method does a lookup based on the renderable class beforehand
    // and will only select the appropriate rasterizer. (See FunctionalRenderingBase)
    // Since the FunctionalRasterizer is package private, there is no way to invoke it with the wrong type from 'outside'.
    // Should somebody still force this to happen by intentional tampering, we have no choice but to catch this disgrace.
    private T safeCast(final Renderable data) {
        try {
            return getSupportedRenderableClass().cast(data);
        } catch (ClassCastException e) {
            // wow
            throw new IllegalArgumentException("Wrong renderable type! This rasterizer is not meant to be used with '"
                    + data.getClass().getCanonicalName() + "'", e);
        }
    }
}
