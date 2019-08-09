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
                    yValue = Constants.NUMBER_FORMAT.parse(yRowIterator.next());
                } catch (ParseException e) {
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

        if (true) {
            throw new UnsupportedOperationException("Vertical parsing is currently not supported");
        }

        int row = 0;

        PointListContainer<PointList> container = new SimplePointListContainerImpl();

        if (csvData.isEmpty()) {
            return container;
        }

        // Iterate over the first row in order to get the headers
        int col = 0;
        for (String header : csvData.get(0)) {
            if (col % 2 == 0) {
                PointList pointList = new SimplePointListImpl();
                pointList.setName(header);
                container.pushBack(pointList);
            }
            col++;
        }

        row++;

        // Continue as long as there is at least one further rows left
        while (csvData.size() >= row + 1) {
            List<String> fields = csvData.get(row);
            Iterator<String> fieldIterator = fields.iterator();

            col = -1;

            while (fieldIterator.hasNext()) {
                String xRaw = fieldIterator.next();
                String yRaw;

                col++;

                if (!fieldIterator.hasNext()) {
                    break;
                }

                yRaw = fieldIterator.next();

                Number xValue;
                Number yValue;

                try {
                    xValue = Constants.NUMBER_FORMAT.parse(xRaw);
                    yValue = Constants.NUMBER_FORMAT.parse(yRaw);
                } catch (ParseException e) {
                    col++;
                    continue;
                }

                Point2DDouble point = new Point2DDouble(xValue.doubleValue(), yValue.doubleValue());

                addPointToPointListList(container, col / 2, point);

                col++;
            }

            row++;
        }

        // TODO First add points to PointList, then add PointList to PointListContainer, so that there is no need for a calculateExtrema call
        container.calculateExtrema();
        return container;
    }
}
