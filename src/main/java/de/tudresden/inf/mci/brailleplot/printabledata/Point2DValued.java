package de.tudresden.inf.mci.brailleplot.printabledata;

import javax.measure.Unit;
import javax.measure.quantity.Length;

/**
 * Representation of a 2 dimensional point with an associated value.
 * Encapsulates both the position on x and y axis, as well as a value (think of embossing intensity).
 * @param <T> The type used for representing the intensity. Could be set to {@link Boolean} for basic Braille support,
 *           but could also by set to {@link Short} if different embossing strengths are required.
 */
public class Point2DValued<T> extends Point2D {

    private final T mVal;

    /**
     * Constructor.
     * @param x Position on the x axis.
     * @param y Position on the y axis.
     * @param val The value of the dot
     */
    public Point2DValued(final Unit<Length> x, final Unit<Length> y, final T val) {
        super(x, y);
        if (val == null) {
            throw new NullPointerException();
        }
        mVal = val;
    }

    /**
     * Getter.
     * @return The value that is associated with this point.
     */
    public final T getVal() {
        return mVal;
    }
}
