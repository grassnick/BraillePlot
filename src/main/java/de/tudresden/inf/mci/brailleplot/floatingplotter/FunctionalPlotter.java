package de.tudresden.inf.mci.brailleplot.floatingplotter;

import de.tudresden.inf.mci.brailleplot.rendering.Renderable;
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
    private Plotter<T> mPlotter;

    private Class<? extends T> mSupportedRenderableClass;

    public FunctionalPlotter(final Class<T> supportedRenderableClass, final Plotter<T> plotter) {
        mLogger.info("Creating new FunctionalRasterizer: Binding rasterizer reference (renderable type {}): {}",
                supportedRenderableClass.getSimpleName(), plotter);
        mSupportedRenderableClass = supportedRenderableClass;
        mPlotter = plotter;
    }

    /**
     * Getter for supported renderable class.
     * @return class
     */
    Class<? extends T> getSupportedRenderableClass() {
        return mSupportedRenderableClass;
    }
}
