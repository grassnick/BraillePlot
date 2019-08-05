package de.tudresden.inf.mci.brailleplot.datacontainers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Low effort implementation of {@link CategoricalPointListContainer}{@literal <}{@link PointList}{@literal >}.
 * @author Georg Graßnick
 * @version 2019.08.02
 */
public class SimpleCategoricalPointListContainerImpl extends SimplePointListContainerImpl implements CategoricalPointListContainer<PointList> {

    private ArrayList<String> mCategories;

    public SimpleCategoricalPointListContainerImpl() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public SimpleCategoricalPointListContainerImpl(final List<String> initialCategories) {
        this(new ArrayList<>(), initialCategories);
    }

    public SimpleCategoricalPointListContainerImpl(final List<PointList> initialElements, final List<String> initialCategories) {
        super(initialElements);
        mCategories = new ArrayList<>(Objects.requireNonNull(initialCategories));
    }

    @Override
    public int pushBackCategory(final String category) {
        Objects.requireNonNull(category);
        mCategories.add(category);
        return mCategories.size() - 1;
    }

    @Override
    public Iterator<String> categoriesIterator() {
        return mCategories.iterator();
    }

    @Override
    public int getNumberOfCategories() {
        return mCategories.size();
    }

    @Override
    public String getCategory(final int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= mCategories.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds. Current bounds: 0 and " + mCategories.size());
        }
        return mCategories.get(index);
    }
}
