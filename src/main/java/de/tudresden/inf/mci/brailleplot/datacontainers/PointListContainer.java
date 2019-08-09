package de.tudresden.inf.mci.brailleplot.datacontainers;

/**
 * Interface for a container managing {@link PointList} objects.
 * Implementing classes can be instantiated by data parser classes and are used as a data representation
 * for use of the rasterizer.
 * @param <T> The specific type of the {@link PointList}. Currently, there is only one generic type,
 *           but later on, one might define additional specialized {@link PointList} implementations.
 * @author Georg Graßnick
 * @version 2019.07.29
 */
public interface PointListContainer<T extends PointList> extends PointContainer<T> {
}
