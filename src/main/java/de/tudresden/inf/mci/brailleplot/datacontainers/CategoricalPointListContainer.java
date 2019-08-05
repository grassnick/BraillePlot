package de.tudresden.inf.mci.brailleplot.datacontainers;

import java.util.Iterator;

/**
 * A PointListContainer containing information about different categories.
 * This will e.g. be useful when dealing with stacked bar charts.
 * @param <T> The specific type of the {@link PointList}. Currently, there is only one generic type,
 *           but later on, one might define additional specialized {@link PointList} implementations.
 * @author Georg Graßnick
 * @version 2019.08.02
 */
public interface CategoricalPointListContainer<T extends PointList> extends PointListContainer<T> {

    /**
     * Adds a new category.
     * @param category The category to add.
     * @return The index, the category is inserted at.
     */
    int pushBackCategory(String category);

    /**
     * Get an iterator over all categories.
     * @return An {@link Iterator} that iterates all contained categories;
     */
     Iterator<String> categoriesIterator();

    /**
     * Get the number of categories.
     * @return The number of categories.
     */
    int getNumberOfCategories();

    /**
     * Get a category at a specif index.
     * Indices are ranged from 0 to {@link CategoricalPointListContainer#getNumberOfCategories()} (exclusive end).
     * @param index The index of the requested category.
     * @return The {@link String} representing the category at the requested index.
     * @throws IndexOutOfBoundsException If the index is {@literal <} 0 or index is {@literal >=} the return value of
     * {@link CategoricalPointListContainer#getNumberOfCategories()}
     */
     String getCategory(int index) throws IndexOutOfBoundsException;
}
