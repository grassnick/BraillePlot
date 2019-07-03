package de.tudresden.inf.mci.brailleplot.rendering;


/**
 * Rasterizer. A functional interface for anything that is able to rasterize a diagram to a raster.
 * @param <T> The concrete diagram class which can be rasterized with the rasterizer.
 * @author Leonard Kupper
 * @version 2019.07.01
 */
@FunctionalInterface
public interface Rasterizer<T extends DiagramStub> {
    void rasterize(T data, AbstractRasterCanvas raster) throws InsufficientRenderingAreaException;
}
