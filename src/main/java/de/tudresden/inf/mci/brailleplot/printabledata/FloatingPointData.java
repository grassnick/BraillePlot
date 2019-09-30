package de.tudresden.inf.mci.brailleplot.printabledata;

import de.tudresden.inf.mci.brailleplot.point.Point2DValued;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Iterator;

/**
 * This data is used to describe the data for the "Floating Dot Area" print mode.
 * For each dot to emboss, there is one {@link Point2DValued} object which encapsulates both the position in width and height,
 * as well as the intensity of the point.
 * @param <T> The type used for representing the intensity. Could be set to {@link Boolean} for basic Braille support,
 *           but could also by set to {@link Short} if different embossing strengths are required.
 * @author Georg Graßnick
 * @version 2019.07.29
 */
public interface FloatingPointData<T> extends PrintableData {

    double RANGE = 1.5;

    /**
     * Returns an iterator over all {@link Point2DValued}.
     * @return An iterator over all points.
     */
    Iterator<Point2DValued<Quantity<Length>, T>> getIterator();

    /**
     * Add a point to the data structure if it is not already in there. This is necessary so that the printer
     * will not emboss the same coordinate twice.
     * @param point The point to be inserted.
     */
    void addPointIfNotExisting(Point2DValued<Quantity<Length>, T> point);

    /**
     * Checks if a point is already in the data. Takes an additional area of RANGE mm * RANGE mm around the point into account.
     * @param point The point to be checked.
     * @return true, if the point is already in the data and false, if not.
     */
    boolean pointExists(Point2DValued<Quantity<Length>, T> point);
}
