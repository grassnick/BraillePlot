package de.tudresden.inf.mci.brailleplot.rendering;

import java.util.function.BiConsumer;

/**
 * FunctionalRasterizer. This class implements a concrete rasterizer via a functional interface.
 * The rasterizing algorithm to be used is passed to the constructor as lambda function or method reference.
 * @param <T> The concrete diagram class which can be rasterized with the rasterizer.
 * @author Leonard Kupper
 * @version 2019.07.01
 */
public class FunctionalRasterizer<T extends DiagramStub> implements Rasterizer {

    private Class<? extends T> mSupportedDiagramClass;
    private BiConsumer<T, AbstractRasterCanvas> mRasterizingAlgorithm;

    public FunctionalRasterizer(final Class<? extends T> supportedDiagramClass, final BiConsumer<T, AbstractRasterCanvas> rasterizingAlgorithm) {
        mSupportedDiagramClass = supportedDiagramClass;
        mRasterizingAlgorithm = rasterizingAlgorithm;
    }

    @Override
    public void rasterize(final DiagramStub data, final AbstractRasterCanvas raster) throws InsufficientRenderingAreaException {
        // invoke the given rasterizing algorithm
        T diagram = safeCast(data);
        mRasterizingAlgorithm.accept(diagram, raster);
    }

    public final Class<? extends T> getSupportedDiagramClass() {
        return mSupportedDiagramClass;
    }

    @SuppressWarnings("unchecked")
    // This is allowed, because the code that calls the rasterize method does a lookup based on the diagram class beforehand
    // and will only select the appropriate rasterizer. (See FunctionalRenderingBase)
    // Since the FunctionalRasterizer is package private, there is no way to invoke it with the wrong type from 'outside'. TODO: MAKE IT PACKAGE PRIVATE!
    // Should somebody still force this to happen by intentional tampering, we have no choice but to catch this disgrace.
    private T safeCast(final DiagramStub data) {
        try {
            return (T) data;
        } catch (ClassCastException e) {
            // wow
            throw new IllegalArgumentException("Wrong diagram type! This rasterizer is not meant to be used with '"
                    + data.getClass().getCanonicalName() + "'", e);
        }
    }
}
