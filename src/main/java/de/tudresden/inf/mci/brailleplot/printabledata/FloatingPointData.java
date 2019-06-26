package de.tudresden.inf.mci.brailleplot.printabledata;

import java.util.Iterator;

/**
 * This data is used to describe the data for the "Floating Dot Area" print mode.
 * For each dot to emboss, there is one {@link Point2D} object which encapsulates both the position in width and height,
 * as well as the intensity of the point.
 * @param <T> The type used for representing the intensity. Could be set to {@link Boolean} for basic Braille support,
 *           but could also by set to {@link Short} if different embossing strengths are required.
 * @author Georg Graßnick
 * @version 2019.06.26
 */
public interface FloatingPointData<T> extends PrintableData {

    /**
     * Returns an iterator over all {@link Point2DValued}.
     * @return An iterator over all points.
     */
    Iterator<Point2DValued<T>> getIterator();

    /**
     * Add a point to the data structure.
     * @param point The point to be inserted.
     */
    void addPoint(Point2DValued<T> point);
}
