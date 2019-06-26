package de.tudresden.inf.mci.brailleplot.printabledata;

import javax.measure.Unit;
import javax.measure.quantity.Length;

/**
 * Representation of a 2 dimensional point.
 * Encapsulates a position on x and y axis.
 * @author Georg Graßnick
 * @version 2019.06.26
 */
public class Point2D {

    private final Unit<Length> mX;
    private final Unit<Length> mY;

    /**
     * Constructor.
     * @param x Position on the x axis.
     * @param y Position on the y axis.
     */
    public Point2D(final Unit<Length> x, final Unit<Length> y) {
        if (x == null || y == null) {
            throw new NullPointerException();
        }
        mX = x;
        mY = y;
    }

    /**
     * Getter.
     * @return The position on the x axis.
     */
    public final Unit<Length> getX() {
        return mX;
    }

    /**
     * Getter.
     * @return The position on the y axis.
     */
    public final Unit<Length> getY() {
        return mY;
    }

}
