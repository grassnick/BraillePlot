package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.ListIterator;

/**
 * Interface for a list-like data structure that holds a set of {@link Point2DDouble}.
 * Implementing classes can be instantiated by data parser classes and are used as a data representation
 * for use of the rasterizer.
 * @author Georg Graßnick
 * @version 2019.07.29
 */
public interface PointList extends PointContainer<Point2DDouble>, Named {

    /**
     * Returns an list iterator over all elements.
     * @return An {@link ListIterator} over all managed{ @link Point2DDouble}.
     */
    ListIterator<Point2DDouble> getListIterator();

    /**
     * Gives the corresponding y-value to a given x-value.
     * @param xValue
     * @return double y-value
     */
    double getCorrespondingYValue(double xValue);

}
