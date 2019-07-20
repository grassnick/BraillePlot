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
        for (int y = yMin; y <= yMax; y++) {
            for (int x = xMin; x <= xMax; x++) {
                data.setValue(y, x, value);
            }
        }
    }

    static void rectangle(int x1, int y1, int x2, int y2, MatrixData<Boolean> data, boolean value) {
        int xMin = min(x1, x2);
        int xMax = max(x1, x2);
        int yMin = min(y1, y2);
        int yMax = max(y1, y2);
        rectangle(new Rectangle(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1), data, value);
    }

    static void rectangle(Rectangle rect, MatrixData<Boolean> data, boolean value) {
        Rectangle.IntWrapper intRect = rect.intWrapper();
        int x2 = max(intRect.getX() + intRect.getWidth() - 1, 0);
        int y2 = max(intRect.getY() + intRect.getHeight() - 1, 0);
        fill(intRect.getX(), intRect.getY(), intRect.getX(), y2, data, value);
        fill(intRect.getX(), y2, x2, y2, data, value);
        fill(x2, intRect.getY(), x2, y2, data, value);
        fill(intRect.getX(), intRect.getY(), x2, intRect.getY(), data, value);
    }
}
