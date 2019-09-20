package de.tudresden.inf.mci.brailleplot.rendering;


import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Rasterizer. A functional interface for anything that is able to rasterize renderable data onto a raster.
 * This interface also defines a static set of tool methods for basic operations on a raster's data container ({@link MatrixData}).
 * @param <T> The concrete class implementing {@link Renderable} which can be rasterized with the rasterizer.
 * @author Leonard Kupper
 * @version 2019.07.22
 */
@FunctionalInterface
public interface Rasterizer<T extends Renderable> {

    /**
     * Rasterizes a {@link Renderable} instance onto a {@link RasterCanvas}.
     * @param data The renderable representation.
     * @param canvas An instance of {@link RasterCanvas} representing the target for the rasterizer output.
     * @throws InsufficientRenderingAreaException If too few space is available on the {@link RasterCanvas}
     * to display the given data.
     */
    void rasterize(T data, RasterCanvas canvas) throws InsufficientRenderingAreaException;

    // Basic geometric rasterizing toolset:

    /**
     * Fills the space on the raster between two arbitrary opposite points with a given value.
     * @param x1 X coordinate of first point.
     * @param y1 Y coordinate of first point.
     * @param x2 X coordinate of second point.
     * @param y2 Y coordinate of second point.
     * @param data The target raster data container.
     * @param value The value to fill the area with.
     */
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

    /**
     * Draws a rectangle border with a given value onto the raster. The rectangle is defined by two arbitrary opposite points.
     * @param x1 X coordinate of first point.
     * @param y1 Y coordinate of first point.
     * @param x2 X coordinate of second point.
     * @param y2 Y coordinate of second point.
     * @param data The target raster data container.
     * @param value The value to fill the area with.
     */
    static void rectangle(int x1, int y1, int x2, int y2, MatrixData<Boolean> data, boolean value) {
        int xMin = min(x1, x2);
        int xMax = max(x1, x2);
        int yMin = min(y1, y2);
        int yMax = max(y1, y2);
        rectangle(new Rectangle(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1), data, value);
    }

    /**
     * Draws a rectangle border with a given value onto the raster.
     * @param rect The {@link Rectangle} instance to draw.
     * @param data The target raster data container.
     * @param value The value to fill the area with.
     */
    static void rectangle(Rectangle rect, MatrixData<Boolean> data, boolean value) {
        Rectangle.IntWrapper intRect = rect.intWrapper();
        int x2 = max(intRect.getX() + intRect.getWidth() - 1, 0);
        int y2 = max(intRect.getY() + intRect.getHeight() - 1, 0);
        fill(intRect.getX(), intRect.getY(), intRect.getX(), y2, data, value);
        fill(intRect.getX(), y2, x2, y2, data, value);
        fill(x2, intRect.getY(), x2, y2, data, value);
        fill(intRect.getX(), intRect.getY(), x2, intRect.getY(), data, value);
    }

    /**
     * Draws an orthogonal line of specified length from given point onto the raster.
     * @param xStart X coordinate of start point.
     * @param yStart Y coordinate of start point.
     * @param length Length of the line.
     * @param orientation Pass true for vertical, false for horizontal.
     * @param data The target raster data container.
     * @param value The value to fill the area with.
     */
    static void line(int xStart, int yStart, int length, boolean orientation, MatrixData<Boolean> data, boolean value) {
        if (length != 0) {
            int xEnd = xStart;
            int yEnd = yStart;
            //int span = (int) Math.signum(length) * max(length - 1, 0);
            if (orientation) {
                xEnd = xStart + length;
            } else {
                yEnd = yStart + length;
            }
            fill(xStart, yStart, xEnd, yEnd, data, value);
        }
    }

    /**
     * Draws an orthogonal dashed line of specified length from given point onto the raster.
     * @param xStart X coordinate of start point.
     * @param yStart Y coordinate of start point.
     * @param length Length of the line.
     * @param orientation Pass true for vertical, false for horizontal.
     * @param data The target raster data container.
     * @param dashSize The size of the line segments.
     */
    static void dashedLine(int xStart, int yStart, int length, boolean orientation, MatrixData<Boolean> data, int dashSize) {
        if (dashSize <= 0) {
            throw new IllegalArgumentException("Dash size cannot be zero or negative!");
        }
        if (length != 0) {
            boolean value = false;
            int xEnd = xStart;
            int yEnd = yStart;
            //int span = (int) Math.signum(length) * max(length - 1, 0);
            if (orientation) {
                xEnd = xStart + length;
            } else {
                yEnd = yStart + length;
            }
            int i = 0;
            for (int y = min(yStart, yEnd); y <= Math.max(yStart, yEnd); y++) {
                for (int x = min(xStart, xEnd); x <= Math.max(xStart, xEnd); x++) {
                    if (i % dashSize == 0) {
                        value = !value;
                    }
                    data.setValue(y, x, value);
                    i++;
                }
            }
        }
    }
}
