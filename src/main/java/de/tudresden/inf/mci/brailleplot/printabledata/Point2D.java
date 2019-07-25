package de.tudresden.inf.mci.brailleplot.printabledata;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Objects;

/**
 * Representation of a 2 dimensional point.
 * Encapsulates a position on mX and mY axis.
 * @author Georg Graßnick
 * @version 2019.06.26
 */
public class Point2D {

    private final Quantity<Length> mX;
    private final Quantity<Length> mY;

    /**
     * Constructor.
     * @param x Position on the mX axis.
     * @param y Position on the mY axis.
     */
    public Point2D(final Quantity<Length> x, final Quantity<Length> y) {
        if (x == null || y == null) {
            throw new NullPointerException();
        }
        mX = x;
        mY = y;
    }

    /**
     * Getter.
     * @return The position on the mX axis.
     */
    public final Quantity<Length> getX() {
        return mX;
    }

    /**
     * Getter.
     * @return The position on the mY axis.
     */
    public final Quantity<Length> getY() {
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
}
