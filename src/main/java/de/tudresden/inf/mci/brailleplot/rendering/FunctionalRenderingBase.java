package de.tudresden.inf.mci.brailleplot.rendering;

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
    private RasterCanvas mRaster;

    public FunctionalRenderingBase() {
        mRasterizingAlgorithms = new HashMap<>();
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
            mLogger.error("No target raster set.");
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
            mLogger.error("No rasterizer found for given renderable type.");
            throw new IllegalArgumentException("No rasterizer registered for renderData class: '"
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
     * Sets a new canvas for any rasterizing operations performed by this rendering base. The rasterizing results are
     * 'drawn' on the currently selected canvas instance. There are no restrictions on the raster canvas. It is also
     * possible to pass a canvas which already contains data to 'overlay' the new data.
     * @param raster The {@link AbstractCanvas} instance which will be used for all subsequent rasterizing operations.
     */
    public void setRasterCanvas(final RasterCanvas raster) {
        mRaster = Objects.requireNonNull(raster);
        mLogger.info("RasterCanvas has been set to instance {}", raster);
    }

    /**
     * Gets the currently set {@link AbstractCanvas} of the rendering base.
     * @return An instance of {@link AbstractCanvas}.
     */
    public RasterCanvas getRaster() {
        return mRaster;
    }
}
