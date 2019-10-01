package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.ListIterator;
import java.util.NoSuchElementException;

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
     * This function is to find the next point in the csv data for line plotting in order to calculate the slope.
     * @param xValue Given x-value of the next point.
     * @return The next point.
     * @throws NoSuchElementException If no corresponding y-value was found.
     */
    Point2DDouble getFirstXOccurence(double xValue) throws NoSuchElementException;

}
