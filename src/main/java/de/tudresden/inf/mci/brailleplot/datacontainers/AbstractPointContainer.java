package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.point.MinMaxPos2D;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Abstract parent class for {@link SimplePointListContainerImpl} and {@link SimplePointListImpl}.
 * @param <T> The type of the elements stored in this container.
 * @author Georg Graßnick
 * @version 2019.07.29
 */
public abstract class AbstractPointContainer<T extends MinMaxPos2D<Double>> implements PointContainer<T>, MinMaxPos2D<Double> {

    protected LinkedList<T> mElements;
    protected Double mMaxX = Double.NEGATIVE_INFINITY;
    protected Double mMaxY = Double.NEGATIVE_INFINITY;
    protected Double mMinX = Double.POSITIVE_INFINITY;
    protected Double mMinY = Double.POSITIVE_INFINITY;

    @Override
    public final int getSize() {
        return mElements.size();
    }

    @Override
    public final void pushBack(final T element) {
        Objects.requireNonNull(element);
        mElements.add(element);
        checkExtrema(element);
    }

    @Override
    public final Iterator<T> iterator() {
        return mElements.iterator();
    }

    @Override
    public final boolean removeFirstOccurrence(final T elementToRename) {
        Objects.requireNonNull(elementToRename);
        return mElements.removeFirstOccurrence(elementToRename);
    }

    @Override
    public Stream<T> stream() {
        return mElements.stream();
    }

    @Override
    public final Double getMinX() {
        return mMinX;
    }

    @Override
    public final Double getMaxX() {
        return mMaxX;
    }

    @Override
    public final Double getMinY() {
        return mMinY;
    }

    @Override
    public final Double getMaxY() {
        return mMaxY;
    }

    @Override
    public final void calculateExtrema() {
        for (T e : mElements) {
            checkExtrema(e);
        }
    }

    private void checkExtrema(final T element) {
        mMaxX = Math.max(element.getMaxX(), mMaxX);
        mMinX = Math.min(element.getMinX(), mMinX);
        mMaxY = Math.max(element.getMaxY(), mMaxY);
        mMinY = Math.min(element.getMinY(), mMinY);
    }
}
