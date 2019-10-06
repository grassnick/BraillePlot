package de.tudresden.inf.mci.brailleplot.printabledata;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

import static tec.units.ri.unit.Units.METRE;

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
    public void addPointIfNotExisting(final Point2DValued<Quantity<Length>, T> point) {
        Objects.requireNonNull(point);

        if (!pointExists(point)) {
            mPoints.addLast(point);
        }
    }

    @Override
    public void addPoint(final Point2DValued<Quantity<Length>, T> point) {
        Objects.requireNonNull(point);
        mPoints.addLast(point);
    }

    @Override
    public boolean pointExists(final Point2DValued<Quantity<Length>, T> newPoint) {
        double newX = newPoint.getX().to(MetricPrefix.MILLI(METRE)).getValue().doubleValue();
        double newY = newPoint.getY().to(MetricPrefix.MILLI(METRE)).getValue().doubleValue();

        for (Point2DValued<Quantity<Length>, T> point : mPoints) {
            double oldX = point.getX().to(MetricPrefix.MILLI(METRE)).getValue().doubleValue();
            double oldY = point.getY().to(MetricPrefix.MILLI(METRE)).getValue().doubleValue();

            if (point.equals(newPoint)) {
                return true;
            }
            if (newX >= oldX - RANGE && newX <= oldX + RANGE && newY >= oldY - RANGE && newY <= oldY + RANGE) {
                return true;
            }
        }

        return false;
    }



}
