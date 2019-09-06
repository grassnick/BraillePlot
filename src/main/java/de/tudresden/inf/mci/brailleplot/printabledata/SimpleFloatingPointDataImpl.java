package de.tudresden.inf.mci.brailleplot.printabledata;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/**
 * A low effort implementation of the {@link FloatingPointData} interface.
 * The underlying data is organized in a {@link LinkedList}, which makes insertions fast, but slows down random access.
 * @param <T> The type used for representing the intensity. Could be set to {@link Boolean} for basic Braille support,
 *           but could also by set to {@link Short} if different embossing strengths are required.
 * @author Georg Graßnick
 * @version 2019.07.29
 */
public class SimpleFloatingPointDataImpl<T> extends AbstractPrintableData implements FloatingPointData<T> {

    private LinkedList<Point2DValued<Quantity<Length>, T>> mPoints;

    public SimpleFloatingPointDataImpl(final Printer printer, final Format format) {
        super(printer, format);
        mPoints = new LinkedList<>();
    }

    @Override
    public Iterator<Point2DValued<Quantity<Length>, T>> getIterator() {
        return mPoints.iterator();
    }

    @Override
    public void addPoint(final Point2DValued<Quantity<Length>, T> point) {
        Objects.requireNonNull(point);

        if (!checkPoint(point)) {
            mPoints.addLast(point);
        }
    }

    @Override
    public boolean checkPoint(final Point2DValued<Quantity<Length>, T> point) {
        boolean present = false;
        Quantity<Length> x = point.getX();
        Quantity<Length> y = point.getY();

        Iterator<Point2DValued<Quantity<Length>, T>> iterator = mPoints.iterator();

        while (iterator.hasNext()) {
            Point2DValued<Quantity<Length>, T> newPoint = iterator.next();
            if (newPoint.getX().equals(x) && newPoint.getY().equals(y)) {
                present = true;
            }
        }

        return present;
    }
}
