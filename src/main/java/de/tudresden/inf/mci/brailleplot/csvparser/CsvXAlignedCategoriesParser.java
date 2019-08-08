package de.tudresden.inf.mci.brailleplot.csvparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Parser for CSV files with bar chart data. Inherits from CsvParseAlgorithm.
 * @author SVGPlott-Team, Georg Graßnick
 * @version 2019.07.29
 */
public class CsvXAlignedCategoriesParser extends CsvParseAlgorithm {

    @Override
    public PointListContainer<PointList> parseAsHorizontalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);

        PointListContainer<PointList> container = new SimplePointListContainerImpl();

        Iterator<? extends List<String>> rowIterator = csvData.iterator();

        if (!rowIterator.hasNext()) {
            return container;
        }

        Iterator<String> lineIterator = rowIterator.next().iterator();


        if (!lineIterator.hasNext()) {
            return container;
        }

        // Store all categories
        List<String> categories = new LinkedList<>();
        while (lineIterator.hasNext()) {
            categories.add(lineIterator.next());
        }

        Iterator<String> categoriesIt = categories.iterator();
        // Store each row's data set
        while (rowIterator.hasNext()) {
            lineIterator = rowIterator.next().iterator();

            // Create a PointList with the title of the data set
            if (!lineIterator.hasNext()) {
                continue;
            }

            if (!categoriesIt.hasNext()) {
                continue;
            }

            String category = categoriesIt.next();
            PointList pointList = new SimplePointListImpl(category);
            container.pushBack(pointList);

            // Add all the points
            int colPosition = 0;
            while (lineIterator.hasNext()) {
                if (colPosition >= container.getSize()) {
                    break;
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
                Point2DDouble newPoint = new Point2DDouble(colPosition, yValue.doubleValue());
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
        /*if (!lineIterator.hasNext()) {
            return container;
        }

        lineIterator.next();*/

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
        Iterator<PointList> it = container.iterator();
        int categoryCounter = 0;
        while (rowIterator.hasNext()) {
            lineIterator = rowIterator.next().iterator();
            if (!lineIterator.hasNext()) {
                categoryCounter++;
                continue;
            }

            // Find out the category title
            String currentCategory = lineIterator.next();
            if (it.hasNext()) {
                it.next().setName(currentCategory);
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

                Point2DDouble newPoint = new Point2DDouble(categoryCounter, yValue.doubleValue());
                addPointToPointListList(container, currentDataSet, newPoint);
                currentDataSet++;
            }

            categoryCounter++;
        }
        // TODO First add points to PointList, then add PointList to PointListContainer, so that there is no need for a calculateExtrema call
        container.calculateExtrema();
        return container;
    }

}
