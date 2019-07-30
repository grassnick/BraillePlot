package de.tudresden.inf.mci.brailleplot.csvparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * An algorithm for parsing CSV data. Contains implementations for two
 * orientations of the data in the file.
 * @author SVGPlott-Team, Georg Graßnick
 * @version 2019.07.29
 */
public abstract class CsvParseAlgorithm {

    /**
     * If the data sets are oriented horizontally, i.e. in rows, parse the rows into
     * {@link @link PointListContainer<PointList>}.
     * @param csvData The parsed input String.
     * @return A {@link PointListContainer<PointList>} representing the data.
     */
    public abstract PointListContainer<PointList> parseAsHorizontalDataSets(List<? extends List<String>> csvData);

    /**
     * If the data sets are oriented horizontally, i.e. in rows, parse the rows into
     * {@link @link PointListContainer<PointList>}.
     * @param csvData The parsed input String.
     * @return A {@link PointListContainer<PointList>} representing the data.
     */
    public abstract PointListContainer<PointList> parseAsVerticalDataSets(List<? extends List<String>> csvData);

    /**
     * Adds a {@link Point2DDouble} to a {@link PointList} in a {@link PointListContainer<PointList>},
     * specified by {@code listIndex}. Adds more {@link PointList PointLists} if
     * needed.
     * @param container The {@link PointListContainer<PointList>} to which the point shall be added
     * @param index The index of the list to which the point shall be added
     * @param point The {@link Point2DDouble} which shall be added
     */
    protected void addPointToPointListList(final PointListContainer<PointList> container, final int index, final Point2DDouble point) {
        Objects.requireNonNull(container);
        Objects.requireNonNull(point);

        // TODO: Check if this actually works

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

}
