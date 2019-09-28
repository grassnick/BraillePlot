package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.point.MinMaxPos2D;

import java.util.stream.Stream;

/**
 * Parent Interface of {@link PointListContainer} and {@link PointList}.
 * @param <T> The type of the elements stored in this container.
 * @author Georg Graßnick
 * @version 2019.07.29
 */
public interface PointContainer<T> extends Iterable<T>, MinMaxPos2D<Double> {

    /**
     * Returns the current size of the List.
     * @return The number of elements currently stored
     */
    int getSize();

    /**
     * Adds a point to the list.
     * Also updates min and max values if required.
     * @param element The element to be inserted.
     */
    void pushBack(T element);

    /**
     * Removes the first occurrence of the specified Point from the list.
     * This does not update the minimum and maximum values itself, you have to call
     * {@link MinMaxPos2D#calculateExtrema()} manually after removing.
     * @param elementToRemove The element to be removed from this list
     * @return True if this list contained the specified element, else false
     */
    boolean removeFirstOccurrence(T elementToRemove);

    Stream<T> stream();

}
