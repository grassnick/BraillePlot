package de.tudresden.inf.mci.brailleplot.rendering;

/**
 * FunctionalRasterizer. This class implements a concrete rasterizer via a functional interface.
 * The rasterizing algorithm to be used is passed to the constructor as lambda function, method reference or rasterizer implementation.
 * @param <T> The concrete diagram class which can be rasterized with the rasterizer.
 * @author Leonard Kupper
 * @version 2019.07.01
 */
public class FunctionalRasterizer<T extends Renderable> implements Rasterizer {

    private Class<? extends T> mSupportedDiagramClass;
    private ThrowingBiConsumer<T, AbstractRasterCanvas, InsufficientRenderingAreaException> mRasterizingAlgorithm;

    /**
     * Constructor. Creates a new rasterizer from either a given rasterizer implementation or (keep in mind that
     * Rasterizer is a functional interface) from a ThrowingBiConsumer&lt;T, AbstractRasterCanvas, InsufficientRenderingAreaException&gt; method reference.
     * @param supportedDiagramClass A reference to the accepted diagram class (e.g. 'BarChart.class')
     * @param rasterizer A reference to a Rasterizer instance.
     */
    public FunctionalRasterizer(
            final Class<T> supportedDiagramClass,
            final Rasterizer<T> rasterizer)
    {
        mSupportedDiagramClass = supportedDiagramClass;
        mRasterizingAlgorithm = rasterizer::rasterize;
    }

    /*
    public FunctionalRasterizer(
            final Class<? extends T> supportedDiagramClass,
            final ThrowingBiConsumer<T, AbstractRasterCanvas, InsufficientRenderingAreaException> rasterizingAlgorithm)
    {
        mSupportedDiagramClass = supportedDiagramClass;
        mRasterizingAlgorithm = rasterizingAlgorithm;
    }
     */

    @Override
    public void rasterize(final Renderable data, final AbstractRasterCanvas canvas) throws InsufficientRenderingAreaException {
        // invoke the given rasterizing algorithm
        T diagram = safeCast(data);
        mRasterizingAlgorithm.accept(diagram, canvas);
    }

    public final Class<? extends T> getSupportedDiagramClass() {
        return mSupportedDiagramClass;
    }

    @SuppressWarnings("unchecked")
    // This is allowed, because the code that calls the rasterize method does a lookup based on the diagram class beforehand
    // and will only select the appropriate rasterizer. (See FunctionalRenderingBase)
    // Since the FunctionalRasterizer is package private, there is no way to invoke it with the wrong type from 'outside'. TODO: MAKE IT PACKAGE PRIVATE!
    // Should somebody still force this to happen by intentional tampering, we have no choice but to catch this disgrace.
    private T safeCast(final Renderable data) {
        try {
            return (T) data;
        } catch (ClassCastException e) {
            // wow
            throw new IllegalArgumentException("Wrong diagram type! This rasterizer is not meant to be used with '"
                    + data.getClass().getCanonicalName() + "'", e);
        }
    }
}
