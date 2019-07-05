package de.tudresden.inf.mci.brailleplot.rendering;


import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Rasterizer. A functional interface for anything that is able to rasterize a diagram to a raster.
 * @param <T> The concrete diagram class which can be rasterized with the rasterizer.
 * @author Leonard Kupper
 * @version 2019.07.01
 */
@FunctionalInterface
public interface Rasterizer<T extends Renderable> {

    void rasterize(T data, AbstractRasterCanvas canvas) throws InsufficientRenderingAreaException;

    // Basic geometric rasterizing toolset:

    static void fill(int x1, int y1, int x2, int y2, MatrixData<Boolean> data, boolean value) {
        int xMin = min(x1, x2);
        int xMax = max(x1, x2);
        int yMin = min(y1, y2);
        int yMax = max(y1, y2);
        System.out.println("Filling from " + xMin + "," + yMin + " to " + xMax + "," + yMax);
        for (int y = yMin; y <= yMax; y++) {
            for (int x = xMin; x <= xMax; x++) {
                data.setValue(y,x,value);
            }
        }
    }

    static void rectangle(int x, int y, int w, int h, MatrixData<Boolean> data, boolean value) {
        int x2 = x + w - 1;
        int y2 = y + h - 1;
        System.out.println("Drawing rectangle from " + x + "," + y + " to " + x2 + "," + y2);
        fill(x, y, x, y2, data, value);
        fill(x, y2, x2, y2, data, value);
        fill(x2, y, x2, y2, data, value);
        fill(x, y, x2, y, data, value);
    }
}
