package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.rendering.floatingplotter.FunctionalPlotter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;

/**
 * FunctionalRenderingBase. This class acts as a wrapper for multiple {@link FunctionalRasterizer} instances.
 * The rasterizer instances can be registered at runtime. The main purpose of the class is to take {@link Renderable}
 * representations of any type and select the correct concrete rasterizer.
 * @author Leonard Kupper
 * @version 2019.07.22
 */
public class FunctionalRenderingBase {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    private HashMap<Class<? extends Renderable>, FunctionalRasterizer> mRasterizingAlgorithms;
    private HashMap<Class<? extends Renderable>, FunctionalPlotter> mPlottingAlgorithms;
    private RasterCanvas mRaster;
    private PlotCanvas mPlot;

    public FunctionalRenderingBase() {
        mRasterizingAlgorithms = new HashMap<>();
        mPlottingAlgorithms = new HashMap<>();
        mLogger.info("FunctionalRenderingBase instance created");
    }

    /**
     * Rasterizes any given {@link Renderable} by passing it to the appropriate registered {@link FunctionalRasterizer}.
     * @param renderData Any instance of a class implementing {@link Renderable}.
     * @throws InsufficientRenderingAreaException If too few space is available on the currently set {@link RasterCanvas}
     * to display the amount of data contained in the given renderable representation.
     * @exception IllegalStateException If no {@link RasterCanvas} is set. Call {@link #setRasterCanvas(RasterCanvas)} beforehand.
     * @exception IllegalArgumentException If no rasterizer is registered for the given renderable type.
     */
    public void rasterize(final Renderable renderData) throws InsufficientRenderingAreaException {
        mLogger.info("Starting new rasterizing task for {}", renderData.getClass().getSimpleName());
        // First, check if a raster is set. No rasterizing without raster.
        if (Objects.isNull(mRaster)) {
            mLogger.error("No target raster set!");
            throw new IllegalStateException("No raster was set. The method 'setRasterCanvas' must be called before invoking the 'rasterize' method.");
        }
        // Then, look at the type of the renderData
        Class<? extends Renderable> renderableClass = renderData.getClass();
        mLogger.info("Selecting FunctionalRasterizer for {}", renderableClass.getSimpleName());

        // Is a rasterizer for the given renderData type available?
        if (mRasterizingAlgorithms.containsKey(renderableClass)) {
            // dispatch to concrete rasterizer implementation
            FunctionalRasterizer selectedRasterizer = mRasterizingAlgorithms.get(renderableClass);
            selectedRasterizer.rasterize(renderData, mRaster);
        } else {
            mLogger.error("No rasterizer found for given renderable type!");
            throw new IllegalArgumentException("No rasterizer registered for renderData class: '"
                    + renderableClass.getCanonicalName() + "'");
        }
    }

    /**
     * Plots any given {@link Renderable} by passing it to the appropriate registered {@link FunctionalPlotter}.
     * @param renderData Any instance of a class implementing {@link Renderable}.
     * @throws InsufficientRenderingAreaException If too few space is available on the currently set {@link PlotCanvas}
     * to display the amount of data contained in the given renderable representation.
     * @exception IllegalStateException If no {@link PlotCanvas} is set. Call {@link #setPlotCanvas(PlotCanvas)} beforehand.
     * @exception IllegalArgumentException If no plotter is registered for the given renderable type.
     */
    public void plot(final Renderable renderData) throws InsufficientRenderingAreaException {
        mLogger.info("Starting new plotting task for {}", renderData.getClass().getSimpleName());
        // First, check if a raster is set. No rasterizing without raster.
        if (Objects.isNull(mPlot)) {
            mLogger.error("No target plot set!");
            throw new IllegalStateException("No plot was set. The method 'setPlotCanvas' must be called before invoking the 'plot' method.");
        }
        // Then, look at the type of the renderData
        Class<? extends Renderable> renderableClass = renderData.getClass();
        mLogger.info("Selecting FunctionalPlotter for {}", renderableClass.getSimpleName());

        // Is a plotter for the given renderData type available?
        if (mPlottingAlgorithms.containsKey(renderableClass)) {
            // dispatch to concrete plotter implementation
            FunctionalPlotter selectedPlotter = mPlottingAlgorithms.get(renderableClass);
            selectedPlotter.plot(renderData, mPlot);
        } else {
            mLogger.error("No plotter found for given renderable type!");
            throw new IllegalArgumentException("No plotter registered for renderData class: '"
                    + renderableClass.getCanonicalName() + "'");
        }
    }

    /**
     * Registers a {@link FunctionalRasterizer} instance to the rendering base. The rendering base can ony hold one rasterizer
     * per {@link Renderable} type at the same time. This means that any rasterizer that has been registered for the same
     * type before will be replaced by the new instance.
     * @param rasterizer The instance of {@link FunctionalRasterizer} to be registered.
     */
    public void registerRasterizer(final FunctionalRasterizer<? extends Renderable> rasterizer) {
        mLogger.trace("Registering new rasterizer {} for type {}", rasterizer,
                rasterizer.getSupportedRenderableClass().getSimpleName());
        if (mRasterizingAlgorithms.containsKey(rasterizer.getSupportedRenderableClass())) {
            mLogger.warn("Already registered rasterizer {} will be overwritten!",
                    mRasterizingAlgorithms.get(rasterizer.getSupportedRenderableClass()));
        }

        mRasterizingAlgorithms.put(rasterizer.getSupportedRenderableClass(), rasterizer);

        mLogger.info("FunctionalRasterizer has been registered for renderable type {}",
                rasterizer.getSupportedRenderableClass().getSimpleName());
        mLogger.trace("Current count of registered rasterizers: {}", mRasterizingAlgorithms.size());
    }

    /**
     * Registers a {@link FunctionalPlotter} instance to the rendering base. The rendering base can ony hold one plotter
     * per {@link Renderable} type at the same time. This means that any plotter that has been registered for the same
     * type before will be replaced by the new instance.
     * @param plotter The instance of {@link FunctionalPlotter} to be registered.
     */
    public void registerPlotter(final FunctionalPlotter<? extends Renderable> plotter) {
        mLogger.trace("Registering new plotter {} for type {}", plotter,
                plotter.getSupportedRenderableClass().getSimpleName());
        if (mPlottingAlgorithms.containsKey(plotter.getSupportedRenderableClass())) {
            mLogger.warn("Already registered plotter {} will be overwritten!",
                    mPlottingAlgorithms.get(plotter.getSupportedRenderableClass()));
        }

        mPlottingAlgorithms.put(plotter.getSupportedRenderableClass(), plotter);

        mLogger.info("FunctionalPlotter has been registered for renderable type {}",
                plotter.getSupportedRenderableClass().getSimpleName());
        mLogger.trace("Current count of registered plotters: {}", mPlottingAlgorithms.size());
    }

    /**
     * Sets a new canvas for any rasterizing operations performed by this rendering base. The rasterizing results are
     * 'drawn' on the currently selected canvas instance. There are no restrictions on the raster canvas. It is also
     * possible to pass a canvas which already contains data to 'overlay' the new data.
     * @param raster The {@link RasterCanvas} instance which will be used for all subsequent rasterizing operations.
     */
    public void setRasterCanvas(final RasterCanvas raster) {
        mRaster = Objects.requireNonNull(raster);
        mLogger.info("RasterCanvas has been set to instance {}", raster);
    }

    /**
     * Sets a new canvas for any plotting operations performed by this rendering base. The plotting results are
     * 'drawn' on the currently selected canvas instance. There are no restrictions on the plot canvas. It is also
     * possible to pass a canvas which already contains data to 'overlay' the new data.
     * @param plot The {@link PlotCanvas} instance which will be used for all subsequent plotting operations.
     */
    public void setPlotCanvas(final PlotCanvas plot) {
        mPlot = Objects.requireNonNull(plot);
        mLogger.info("PlotCanvas has been set to instance {}", plot);
    }

    /**
     * Gets the currently set {@link RasterCanvas} of the rendering base.
     * @return An instance of {@link RasterCanvas}.
     */
    public RasterCanvas getRaster() {
        return mRaster;
    }

    /**
     * Gets the currently set {@link PlotCanvas} of the rendering base.
     * @return An instance of {@link PlotCanvas}.
     */
    public PlotCanvas getPlot() {
        return mPlot;
    }
}
