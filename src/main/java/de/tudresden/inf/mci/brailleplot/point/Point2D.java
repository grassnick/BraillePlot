package de.tudresden.inf.mci.brailleplot.point;

import java.util.Objects;

/**
 * Representation of a 2 dimensional point.
 * Encapsulates a position on x and y axis.
 * @param <T> The type used to represent the position.
 * @author Georg Graßnick
 * @version 2019.07.29
 */
public class Point2D<T> implements MinMaxPos2D<T> {

    private final T mX;
    private final T mY;

    /**
     * Constructor.
     * @param x Position on the x axis.
     * @param y Position on the y axis.
     */
    public Point2D(final T x, final T y) {
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        mX = x;
        mY = y;
    }

    /**
     * Getter.
     * @return The position on the x axis.
     */
    public final T getX() {
        return mX;
    }

    /**
     * Getter.
     * @return The position on the y axis.
     */
    public final T getY() {
        return mY;
    }

    /**
     * Check for Object equality.
     * @param other The other to compare to.
     * @return true, if X and Y positions of both points are equal; else false
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Point2D)) {
            return false;
        }
        Point2D point = (Point2D) other;
        return mX.equals(point.mX) && mY.equals(point.mY);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mX, mY);
    }

    /**
     * Create a human readable String to represent the point.
     * @return A human readable String.
     */
    @Override
    public String toString() {
        return "(" + mX + ", " + mY + ")";
    }

    @Override
    public T getMinX() {
        return mX;
    }

    @Override
    public T getMaxX() {
        return mX;
    }

    @Override
    public T getMinY() {
        return mY;
    }

    @Override
    public T getMaxY() {
        return mY;
    }

    @Override
    public void calculateExtrema() {

    }
}
