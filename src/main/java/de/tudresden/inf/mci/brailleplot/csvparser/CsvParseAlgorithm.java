package de.tudresden.inf.mci.brailleplot.csvparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * An algorithm for parsing CSV data. Contains implementations for two
 * orientations of the data in the file.
 * @param <T> The type of PointContainer, that is parsed to.
 * @author SVGPlott-Team, Georg Graßnick
 * @version 2019.07.29
 */
public abstract class CsvParseAlgorithm<T extends PointListContainer<PointList>> {

    protected final Logger mLogger = LoggerFactory.getLogger(getClass());

    /**
     * If the data sets are oriented horizontally, i.e. in rows, parse the rows into
     * {@link PointListContainer}{@literal <}{@link PointList}{@literal >}.
     * @param csvData The parsed input String.
     * @return A {@link PointListContainer}{@literal <PointList>} representing the data.
     */
    public abstract T parseAsHorizontalDataSets(List<? extends List<String>> csvData);

    /**
     * If the data sets are oriented horizontally, i.e. in rows, parse the rows into
     * {@link PointListContainer}{@literal <}{@link PointList}{@literal >}.
     * @param csvData The parsed input String.
     * @return A {@link PointListContainer}{@literal <}{@link PointList}{@literal >} representing the data.
     */
    public abstract T parseAsVerticalDataSets(List<? extends List<String>> csvData);

    /**
     * Adds a {@link Point2DDouble} to a {@link PointList} in a {@link PointListContainer}{@literal <}{@link PointList}{@literal >},
     * specified by {@code listIndex}. Adds more {@link PointList PointLists} if
     * needed.
     * @param container The {@link PointListContainer}{@literal <}{@link PointList}{@literal >} to which the point shall be added
     * @param index The index of the list to which the point shall be added
     * @param point The {@link Point2DDouble} which shall be added
     */
    protected void addPointToPointListList(final PointListContainer<PointList> container, final int index, final Point2DDouble point) {
        Objects.requireNonNull(container);
        Objects.requireNonNull(point);

        // TODO: Check if this actually works --> does not work (ConcurrentModificationException)

        if (true) {
            throw new UnsupportedOperationException();
        }

        int currentIdx = 0;
        int listsToAddCount = index - container.getSize();

        Iterator<PointList> it = container.iterator();

        // Add extra intermediate lists if required
        if (listsToAddCount > 0) {
            for (int i = 0; i < listsToAddCount; i++) {
                container.pushBack(new SimplePointListImpl());
            }
        }
        // Add the element itself
        for (; currentIdx < index; currentIdx++) {
            it.next();
        }
        it.next().pushBack(point);
    }

    /**
     * Transpose CSV data (List of Lists) as if it were a matrix.
     * @param csvData CSV as List of Lists&lt;T&gt;
     * @param <T> Generic list type (e.g. String)
     * @return The transposed version of the CSV data as List of Lists
     */
    static <T> List<List<T>> transposeCSV(final List<? extends List<T>> csvData) {
        List<List<T>> transposedCsvData = new ArrayList<>();
        final int columns = csvData.get(0).size();
        for (int i = 0; i < columns; i++) {
            List<T> col = new ArrayList<T>();
            for (List<T> row : csvData) {
                col.add(row.get(i));
            }
            // add column from original table as row of new one.
            transposedCsvData.add(col);
        }
        return transposedCsvData;
    }

}
