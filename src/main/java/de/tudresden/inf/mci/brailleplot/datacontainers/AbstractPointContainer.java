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
 * @version 2019.08.02
 */
public abstract class AbstractPointContainer<T extends MinMaxPos2D<Double>> implements PointContainer<T>, MinMaxPos2D<Double> {

    protected LinkedList<T> mElements;
    protected Double mMaxX = Double.NEGATIVE_INFINITY;
    protected Double mMaxY = Double.NEGATIVE_INFINITY;
    protected Double mMinX = Double.POSITIVE_INFINITY;
    protected Double mMinY = Double.POSITIVE_INFINITY;
    protected String[] mAxes;

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

    private String getRecursiveIndentation(final int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }

    /**
     * Generates a String representing the data structure.
     * This function is called recursively on the child elements of a container, so that in the end,
     * the caller receives a visual overview of the contents of this container down to the leaf nodes (@link Point2DDouble).
     * @param depth The recursion depth of the current call.
     * @return A recursive String representation of the container.
     */
    protected String toRecursiveString(final int depth) {
        StringBuilder sb = new StringBuilder();

        if (depth == 0) {
            sb.append(this.getClass()).append(":\n");
        }
            for (T e: mElements) {
                sb.append(getRecursiveIndentation(depth + 1)).append(e.getClass()).append(":\n");
                if (e instanceof AbstractPointContainer) {
                    @SuppressWarnings("unchecked")
                    AbstractPointContainer<T> a = ((AbstractPointContainer<T>) e);
                    sb.append(a.toRecursiveString(depth + 1));
                } else {
                    sb.append(getRecursiveIndentation(depth + 2)).append(e.toString()).append("\n");
                }
            }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toRecursiveString(0);
    }

    /**
     * Getter for mElements.
     * @return mElements
     */
    public LinkedList<T> getElements() {
        return mElements;
    }

    @Override
    public void setAxes(final String[] axes) {
        mAxes = Objects.requireNonNull(axes);
    }

    @Override
    public String[] getAxes() {
        return mAxes;
    }

}
