package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

/**
 * Rasterizer. A functional interface for anything that is able to rasterize a diagram to a raster.
 * @param <T> The concrete diagram class which can be rasterized with the rasterizer.
 * @author Leonard Kupper
 * @version 2019.07.01
 */
@FunctionalInterface
public interface Rasterizer<T extends DiagramStub> {
    MatrixData rasterize(T data, Raster raster) throws InsufficientRenderingAreaException;
}
