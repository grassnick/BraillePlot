package de.tudresden.inf.mci.brailleplot.csvparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Parser for CSV files with aligned mX-values. Inherits from CsvParseAlgorithm.
 * @author SVGPlott-Team, Georg Graßnick
 * @version 2019.07.29
 */
public class CsvXAlignedParser extends CsvParseAlgorithm<PointListContainer<PointList>> {

    @Override
    public PointListContainer<PointList> parseAsHorizontalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);

        if (true) {
            throw new UnsupportedOperationException("Horizontal parsing is currently not supported");
        }

        PointListContainer<PointList> container = new SimplePointListContainerImpl();
        List<Number> xValues = new ArrayList<>();
        Iterator<? extends List<String>> rowIterator = csvData.iterator();

        if (!rowIterator.hasNext()) {
            return container;
        }

        Iterator<String> lineIterator = rowIterator.next().iterator();

        // Move the iterator to the mX value
        if (!lineIterator.hasNext()) {
            return container;
        }
        lineIterator.next();
        if (!lineIterator.hasNext()) {
            return container;
        }

        // Store all mX values, if one is not specified store NaN
        while (lineIterator.hasNext()) {
            Number xValue;
            try {
                xValue = Constants.NUMBER_FORMAT.parse(lineIterator.next());
            } catch (ParseException e) {
                xValue = Double.NaN;
            }
            xValues.add(xValue);
        }

        // Store each row's data set
        while (rowIterator.hasNext()) {
            lineIterator = rowIterator.next().iterator();

            // Create a PointList with the title of the data set
            if (!lineIterator.hasNext()) {
                continue;
            }
            PointList pointList = new SimplePointListImpl();
            pointList.setName(lineIterator.next());
            container.pushBack(pointList);

            // Add all the points
            int colPosition = 0;
            while (lineIterator.hasNext()) {
                if (colPosition >= xValues.size()) {
                    break;
                }
                Number xValue = xValues.get(colPosition);
                if (xValue.equals(Double.NaN)) {
                    lineIterator.next();
                    colPosition++;
                    continue;
                }

                // Find out the mY value
                Number yValue;
                try {
                    yValue = Constants.NUMBER_FORMAT.parse(lineIterator.next());
                } catch (ParseException e) {
                    colPosition++;
                    continue;
                }

                // Add the new point
                Point2DDouble newPoint = new Point2DDouble(xValue.doubleValue(), yValue.doubleValue());
                pointList.pushBack(newPoint);
                colPosition++;
            }
        }

        // TODO First add points to PointList, then add PointList to PointListContainer, so that there is no need for a calculateExtrema call
        container.calculateExtrema();
        return container;
    }

    @Override
    /**
     * This method has been implemented from scratch, as there is no documentation about the structure of the CSV files whatsoever.
     */
    public PointListContainer<PointList> parseAsVerticalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);

        PointListContainer<PointList> container = new SimplePointListContainerImpl();
        Iterator<? extends List<String>> rowIt = csvData.iterator();

        int rowNum = 0; // Keep track of the row number, so that we can include the erroneous row number in the exception.
        while (rowIt.hasNext()) {
            Iterator<String> lineIt = rowIt.next().iterator();
            rowNum++;
            if (!lineIt.hasNext()) {
                throw new MalformedCsvException("Line: " + rowNum + ": Data set must contain a name for a value");
            }
                String name = lineIt.next().trim();
            if (!lineIt.hasNext()) {
                throw new MalformedCsvException("Line: " + rowNum + ": Data set must contain a name for a value");
            }
                String value = lineIt.next().trim();

            // Log if there are more inputs that are not parsed
            if (lineIt.hasNext()) {
                mLogger.debug("Skipping additional column in line {}", rowNum);
            }
            Number val;
            try {
                val = Constants.NUMBER_FORMAT.parse(value);
            } catch (final ParseException pe) {
                throw new MalformedCsvException("Line: " + rowNum + ": Could not parse value", pe);
            }
            Point2DDouble p = new Point2DDouble(0, val.doubleValue());
            PointList pl = new SimplePointListImpl(name);
            pl.pushBack(p);
            container.pushBack(pl);
        }
        return container;
    }

}
