package de.tudresden.inf.mci.brailleplot.csvparser;

import java.util.List;

/**
 * An algorithm for parsing CSV data. Contains implementations for two
 * orientations of the data in the file.
 */
public abstract class CsvParseAlgorithm {
    /**
     * If the data sets are oriented horizontally, i.e. in rows, parse the rows
     * into {@link PointListList.PointList PointLists}.
     *
     * @param csvData
     * @return
     */
    public abstract PointListList parseAsHorizontalDataSets(List<? extends List<String>> csvData);

    /**
     * If the data sets are oriented vertically, i.e. in columns, parse the
     * columns into {@link PointListList.PointList PointLists}.
     *
     * @param csvData
     * @return
     */
    public abstract PointListList parseAsVerticalDataSets(List<? extends List<String>> csvData);

    /**
     * Adds a {@code point} to a {@link PointListList.PointList} in a {@link PointListList},
     * specified by {@code listIndex}. Adds more {@link PointListList.PointList PointLists} if
     * needed.
     *
     * @param pointListList
     *            the {@link PointListList} to which the point shall be added
     * @param listIndex
     *            the index of the list to which the point shall be added
     * @param point
     *            the point which shall be added
     */
    protected void addPointToPointListList(PointListList pointListList, int listIndex, Point point) {
        while (pointListList.size() < listIndex) {
            pointListList.add(new PointListList.PointList());
        }

        pointListList.get(listIndex).insertSorted(point);
    }
}
