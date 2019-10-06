package de.tudresden.inf.mci.brailleplot.csvparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Parser for CSV files that contain data for a scatter plot.
 * @author SVGPlott-Team, Georg Graßnick
 * @version 2019.07.29
 */
public class CsvDotParser extends CsvParseAlgorithm<PointListContainer<PointList>> {

    /**
     * Parses scattered point data in horizontal data sets, alternating mX and mY. The
     * first column contains the row mName in the mX row.
     * @param csvData The parsed input String.
     * @return A {@link PointListContainer}{@literal <}{@link PointList}{@literal >} representing the data.
     */
    public PointListContainer<PointList> parseAsHorizontalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);
        int row = 0;

        PointListContainer<PointList> container = new SimplePointListContainerImpl();

        // Continue as long as there are at least two further rows left
        while (csvData.size() >= row + 2) {
            PointList rowPoints = new SimplePointListImpl();

            Iterator<String> xRowIterator = csvData.get(row).iterator();
            Iterator<String> yRowIterator = csvData.get(row + 1).iterator();

            row += 2;

            // Get the row mName
            if (xRowIterator.hasNext() && yRowIterator.hasNext()) {
                rowPoints.setName(xRowIterator.next());
                yRowIterator.next();
            } else {
                continue;
            }

            // Get the row values
            while (xRowIterator.hasNext() && yRowIterator.hasNext()) {
                Number xValue;
                Number yValue;
                try {
                    xValue = Constants.NUMBER_FORMAT.parse(xRowIterator.next());
                } catch (ParseException pe) {
                    // TODO: actually throw exceptions
                    // Currently this does not work because some of the csv data examples from SVGPlott include empty cells,
                    // and their correct layout is not documented.
                    mLogger.warn("Line: " + (row - 1) + ": Could not parse value", pe);
                    continue;
                }
                try {
                    yValue = Constants.NUMBER_FORMAT.parse(yRowIterator.next());
                } catch (ParseException pe) {
                    mLogger.warn("Line: " + row + ": Could not parse value", pe);
                    continue;
                }
                Point2DDouble newPoint = new Point2DDouble(xValue.doubleValue(), yValue.doubleValue());
                rowPoints.pushBack(newPoint);
            }

            // If there were no points found, do not add the row to the list
            if (rowPoints.getSize() > 0) {
                container.pushBack(rowPoints);
            }
        }

        // TODO First add points to PointList, then add PointList to PointListContainer, so that there is no need for a calculateExtrema call
        container.calculateExtrema();
        return container;
    }

    /**
     * Parses scattered point data in vertical data sets, alternating mX and mY. The
     * first row contains the column mName in the mX column.
     * @param csvData The parsed input String.
     * @return A {@link PointListContainer}{@literal <}{@link PointList}{@literal >} representing the data.
     */
    @Override
    public PointListContainer<PointList> parseAsVerticalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);

            throw new UnsupportedOperationException("Vertical parsing is currently not supported");
    }
}
