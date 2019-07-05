package de.tudresden.inf.mci.brailleplot.rendering;

import java.util.HashMap;
import java.util.Objects;

/**
 * FunctionalRenderingBase. This class acts as a wrapper for multiple {@link FunctionalRasterizer} instances.
 * The rasterizer instances can be registered at runtime. The main purpose of the class is to take diagram representations of any type and select the correct concrete rasterizer.
 * @author Leonard Kupper
 * @version 2019.07.01
 */
public class FunctionalRenderingBase {

    private HashMap<Class<? extends Renderable>, FunctionalRasterizer> mRasterizingAlgorithms;
    private AbstractRasterCanvas mRaster;

    public FunctionalRenderingBase() {
        mRasterizingAlgorithms = new HashMap<>();
    }

    // Rasterizing

    public final void rasterize(final Renderable diagram) throws InsufficientRenderingAreaException {
        // first, check if a raster is set. No rasterizing without raster.
        if (Objects.isNull(mRaster)) {
            throw new IllegalStateException("No raster was set. The method 'setRasterCanvas' must be called before invoking the 'rasterize' method.");
        }
        // then, look at the type of the diagram
        Class<? extends Renderable> diagramClass = diagram.getClass();
        // is a rasterizer for the given diagram type available?
        if (mRasterizingAlgorithms.containsKey(diagramClass)) {
            // dispatch to concrete rasterizer implementation
            FunctionalRasterizer selectedRasterizer = mRasterizingAlgorithms.get(diagramClass);
            selectedRasterizer.rasterize(diagram, mRaster);
        } else {
            throw new IllegalArgumentException("No rasterizer registered for diagram class: '"
                    + diagramClass.getCanonicalName() + "'");
        }
    }


    public final void registerRasterizer(final FunctionalRasterizer<? extends Renderable> rasterizer) {
        mRasterizingAlgorithms.put(rasterizer.getSupportedDiagramClass(), rasterizer);
    }

    public final void setRasterCanvas(final AbstractRasterCanvas raster) {
        mRaster = Objects.requireNonNull(raster);
    }
    public final AbstractRasterCanvas getRaster() {
        return mRaster;
    }
}
