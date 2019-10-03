package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A low effort implementation of {@link PointList}.
 * @author Georg Graßnick, Andrey Ruzhanskiy
 * @version 2019.09.24
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
    public PointList sortXAscend() {
        PointList list = this;
        List<Point2DDouble> temp = list.stream().sorted((o1, o2) -> {
            if (o1.getX() < o2.getX()) {
                return -1;
            } else {
                return 1;
            }
        }).collect(Collectors.toList());
        SimplePointListImpl result = new SimplePointListImpl(list.getName(), temp);
        result.calculateExtrema();
        return result;
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

}
