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
public class CsvXAlignedParser extends CsvParseAlgorithm {

    @Override
    public PointListContainer<PointList> parseAsHorizontalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);

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
    public PointListContainer<PointList> parseAsVerticalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);

        PointListContainer<PointList> container = new SimplePointListContainerImpl();
        Iterator<? extends List<String>> rowIterator = csvData.iterator();

        if (!rowIterator.hasNext()) {
            return container;
        }

        Iterator<String> lineIterator = rowIterator.next().iterator();

        // Move the iterator to the first title
        if (!lineIterator.hasNext()) {
            return container;
        }
        lineIterator.next();
        if (!lineIterator.hasNext()) {
            return container;
        }

        // Add a PointList for each title
        while (lineIterator.hasNext()) {
            PointList pointList = new SimplePointListImpl();
            pointList.setName(lineIterator.next());
            container.pushBack(pointList);
        }

        // Add the data
        while (rowIterator.hasNext()) {
            lineIterator = rowIterator.next().iterator();
            if (!lineIterator.hasNext()) {
                continue;
            }

            // Find out the mX value
            Number xValue;
            try {
                xValue = Constants.NUMBER_FORMAT.parse(lineIterator.next());
            } catch (ParseException e) {
                continue;
            }

            // Find out the mY values and add the points to the respective lists
            int currentDataSet = 0;
            while (lineIterator.hasNext()) {
                Number yValue;
                try {
                    yValue = Constants.NUMBER_FORMAT.parse(lineIterator.next());
                } catch (ParseException e) {
                    currentDataSet++;
                    continue;
                }

                Point2DDouble newPoint = new Point2DDouble(xValue.doubleValue(), yValue.doubleValue());
                addPointToPointListList(container, currentDataSet, newPoint);
                currentDataSet++;
            }

        }

        // TODO First add points to PointList, then add PointList to PointListContainer, so that there is no need for a calculateExtrema call
        container.calculateExtrema();
        return container;
    }

}
