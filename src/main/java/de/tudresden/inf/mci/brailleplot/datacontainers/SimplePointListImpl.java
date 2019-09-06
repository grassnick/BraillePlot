package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * A low effort implementation of {@link PointList}.
 * @author Georg Graßnick
 * @version 2019.07.29
 */
public class SimplePointListImpl extends AbstractPointContainer<Point2DDouble> implements PointList {

    private String mName;

    public SimplePointListImpl() {
        this("");
    }

    public SimplePointListImpl(final String name) {
        this(name, new LinkedList<>());
    }

    public SimplePointListImpl(final String name, final List<Point2DDouble> initialElements) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(initialElements);
        mName = name;
        mElements = new LinkedList<>(initialElements);
    }

    @Override
    public ListIterator<Point2DDouble> getListIterator() {
        return mElements.listIterator();
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(final String newName) {
        Objects.requireNonNull(newName);
        mName = newName;
    }

    @Override
    protected String toRecursiveString(final int depth) {
        return getName() + super.toRecursiveString(depth);
    }

    @Override
    public double getCorrespondingYValue(final double xValue) {
        Iterator<Point2DDouble> iterator = mElements.iterator();

        while (iterator.hasNext()) {
            Point2DDouble newPoint = iterator.next();
            if (newPoint.getX().equals(xValue)) {
                return newPoint.getY();
            }
        }

        return 0;
    }
}
