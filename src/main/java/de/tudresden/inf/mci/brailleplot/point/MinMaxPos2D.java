package de.tudresden.inf.mci.brailleplot.point;

/**
 * Interface for classes supporting a minimum and maximum value for x, as well as y values.
 * @author Georg Graßnick
 * @version 2019.07.29
 * @param <T> The type of the values.
 */
public interface MinMaxPos2D<T> {

    /**
     * Get the current minimum on the x axis.
     * @return The current minimum on the x axis.
     */
    T getMinX();
    /**
     * Get the current maximum on the x axis.
     * @return The current maximum on the x axis.
     */
    T getMaxX();

    /**
     * Get the current minimum on the y axis.
     * @return The current minimum on the y axis.
     */
    T getMinY();

    /**
     * Get the current maximum on the y axis.
     * @return The current maximum on the y axis.
     */
    T getMaxY();

    /**
     * Enforce the recalculation of the minimum and maximum value of this object.
     */
    void calculateExtrema();

}
