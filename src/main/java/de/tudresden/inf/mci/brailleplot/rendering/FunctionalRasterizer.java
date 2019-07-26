package de.tudresden.inf.mci.brailleplot.rendering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FunctionalRasterizer. This class implements a concrete rasterizer via a functional interface.
 * The rasterizing algorithm to be used is passed to the constructor as lambda function, method reference or rasterizer implementation.
 * @param <T> The concrete diagram class which can be rasterized with the rasterizer.
 * @author Leonard Kupper
 * @version 2019.07.20
 */
public class FunctionalRasterizer<T extends Renderable> implements Rasterizer {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    private Class<? extends T> mSupportedRenderableClass;
    private ThrowingBiConsumer<T, RasterCanvas, InsufficientRenderingAreaException> mRasterizingAlgorithm;

    /**
     * Constructor. Creates a new rasterizer from either a given rasterizer implementation or (keep in mind that
     * Rasterizer is a functional interface) from a ThrowingBiConsumer&lt;T, RasterCanvas, InsufficientRenderingAreaException&gt; method reference.
     * @param supportedRenderableClass A reference to the accepted diagram class (e.g. 'BarChart.class')
     * @param rasterizer A reference to a Rasterizer instance.
     */
    public FunctionalRasterizer(
            final Class<T> supportedRenderableClass,
            final Rasterizer<T> rasterizer) {
        mLogger.info("Creating new FunctionalRasterizer: Binding rasterizer reference (renderable type {}): {}.",
                supportedRenderableClass.getSimpleName(), rasterizer);
        mSupportedRenderableClass = supportedRenderableClass;
        mRasterizingAlgorithm = rasterizer::rasterize;
    }

    @Override
    public void rasterize(final Renderable data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        // invoke the given rasterizing algorithm
        T safeData = safeCast(data);
        mLogger.trace("Delegating task to bound rasterizing algorithm {}", mRasterizingAlgorithm);
        mRasterizingAlgorithm.accept(safeData, canvas);
    }

    final Class<? extends T> getSupportedRenderableClass() {
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
