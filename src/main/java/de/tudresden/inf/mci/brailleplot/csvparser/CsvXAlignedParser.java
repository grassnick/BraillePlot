package de.tudresden.inf.mci.brailleplot.csvparser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parser for CSV files with aligned mX-values. Inherits from CsvParseAlgorithm.
 */
public class CsvXAlignedParser extends CsvParseAlgorithm {

    @Override
    public PointListList parseAsHorizontalDataSets(final List<? extends List<String>> csvData) {
        PointListList pointListList = new PointListList();
        List<Number> xValues = new ArrayList<>();
        Iterator<? extends List<String>> rowIterator = csvData.iterator();

        if (!rowIterator.hasNext()) {
            return pointListList;
        }

        Iterator<String> lineIterator = rowIterator.next().iterator();

        // Move the iterator to the mX value
        if (!lineIterator.hasNext()) {
            return pointListList;
        }
        lineIterator.next();
        if (!lineIterator.hasNext()) {
            return pointListList;
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
            PointListList.PointList pointList = new PointListList.PointList();
            pointList.setName(lineIterator.next());
            pointListList.add(pointList);

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
                Point newPoint = new Point(xValue.doubleValue(), yValue.doubleValue());
                pointList.insertSorted(newPoint);
                colPosition++;
            }
        }

        return pointListList;
    }

    @Override
    public PointListList parseAsVerticalDataSets(final List<? extends List<String>> csvData) {
        PointListList pointListList = new PointListList();
        Iterator<? extends List<String>> rowIterator = csvData.iterator();

        if (!rowIterator.hasNext()) {
            return pointListList;
        }

        Iterator<String> lineIterator = rowIterator.next().iterator();

        // Move the iterator to the first title
        if (!lineIterator.hasNext()) {
            return pointListList;
        }
        lineIterator.next();
        if (!lineIterator.hasNext()) {
            return pointListList;
        }

        // Add a PointList for each title
        while (lineIterator.hasNext()) {
            PointListList.PointList pointList = new PointListList.PointList();
            pointList.setName(lineIterator.next());
            pointListList.add(pointList);
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

                Point newPoint = new Point(xValue.doubleValue(), yValue.doubleValue());
                addPointToPointListList(pointListList, currentDataSet, newPoint);
                currentDataSet++;
            }

        }

        return pointListList;
    }

}
