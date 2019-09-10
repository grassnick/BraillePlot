package de.tudresden.inf.mci.brailleplot.datacontainers;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A low effort implementation of {@link PointListContainer}{@literal <}{@link PointList}{@literal >}.
 * @author Georg Graßnick, Leonard Kupper
 * @version 2019.08.29
 */
public class SimplePointListContainerImpl extends AbstractPointContainer<PointList> implements PointListContainer<PointList> {

    public SimplePointListContainerImpl() {
        this(new LinkedList<>());
    }

    public SimplePointListContainerImpl(final List<PointList> initialElements) {
        Objects.requireNonNull(initialElements);
        mElements = new LinkedList<>(initialElements);
    }

    public SimplePointListContainerImpl(final PointListContainer<PointList> pointListContainer) {
        Objects.requireNonNull(pointListContainer);
        mElements = new LinkedList<>(pointListContainer.stream().collect(Collectors.toList()));
        calculateExtrema();
    }
}
