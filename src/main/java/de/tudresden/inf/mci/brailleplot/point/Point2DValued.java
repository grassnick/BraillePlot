package de.tudresden.inf.mci.brailleplot.point;

import java.util.Objects;

/**
 * Representation of a 2 dimensional point with an associated value.
 * Encapsulates both the position on x and y axis, as well as a value (think of embossing intensity).
 * @param <T> The type used to represent the position.
 * @param <U> The type used for representing the intensity. Could be set to {@link Boolean} for basic Braille support,
 *           but could also by set to {@link Short} if different embossing strengths are required.
 * @author Georg Graßnick
 * @version 2019.07.28
 */
public class Point2DValued<T, U> extends Point2D<T> {

    private final U mVal;

    /**
     * Constructor.
     * @param x Position on the x axis.
     * @param y Position on the y axis.
     * @param val The value of the dot
     */
    public Point2DValued(final T x, final T y, final U val) {
        super(x, y);
        Objects.requireNonNull(val);
        mVal = val;
    }

    /**
     * Getter.
     * @return The value that is associated with this point.
     */
    public final U getVal() {
        return mVal;
    }

    /**
     * Check for Object equality.
     * @param other The other to compare to.
     * @return true, if X and Y positions and the values of both points are equal; else false
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Point2DValued)) {
            return false;
        }
        Point2D point = (Point2DValued) other;
        return super.equals(point) && mVal.equals(((Point2DValued) other).mVal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mVal, super.hashCode());
    }

    /**
     * Create a human readable String to represent the point.
     * @return A human readable String.
     */
    @Override
    public String toString() {
        return "(" + super.getX() + ", " + super.getY() + ": " + mVal + ")";
    }
}
