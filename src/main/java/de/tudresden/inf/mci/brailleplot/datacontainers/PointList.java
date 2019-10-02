package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.ListIterator;


/**
 * Interface for a list-like data structure that holds a set of {@link Point2DDouble}.
 * Implementing classes can be instantiated by data parser classes and are used as a data representation
 * for use of the rasterizer.
 * @author Georg Graßnick, Andrey Ruzhanskiy
 * @version 2019.09.24
 */
public interface PointList extends PointContainer<Point2DDouble>, Named {

    /**
     * Returns an list iterator over all elements.
     * @return An {@link ListIterator} over all managed{ @link Point2DDouble}.
     */
    ListIterator<Point2DDouble> getListIterator();

    /**
     * Returns a sorted list copy in ascending fashion for the x-values.
     * @return An {@link PointList} which is sorted by the x-values in ascending fashion.
     */

    PointList sortXAscend();
}
