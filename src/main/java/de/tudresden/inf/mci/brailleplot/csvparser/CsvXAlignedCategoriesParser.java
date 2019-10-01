package de.tudresden.inf.mci.brailleplot.csvparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimpleCategoricalPointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Parser for CSV files with bar chart data. Inherits from CsvParseAlgorithm.
 * @author SVGPlott-Team, Georg Graßnick
 * @version 2019.07.29
 */
public class CsvXAlignedCategoriesParser extends CsvParseAlgorithm<CategoricalPointListContainer<PointList>> {

    /*
    @Override
    public CategoricalPointListContainer<PointList> parseAsHorizontalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);

        if (true) {
            throw new UnsupportedOperationException("Horizontal parsing is currently not supported");
        }

        CategoricalPointListContainer<PointList> container = new SimpleCategoricalPointListContainerImpl();

        Iterator<? extends List<String>> rowIterator = csvData.iterator();

        if (!rowIterator.hasNext()) {
            return container;
        }

        Iterator<String> lineIterator = rowIterator.next().iterator();

        // Move the iterator to the first category name
        if (!lineIterator.hasNext()) {
            return container;
        }

        lineIterator.next();

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
    */

    @Override
    public CategoricalPointListContainer<PointList> parseAsHorizontalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);

        // The csv representation of horizontal datasets is essentially the transposition of vertical datasets.
        List<? extends List<String>> transposedCsvData = transposeCSV(csvData);
        return parseAsVerticalDataSets(transposedCsvData);
    }

    @Override
    public CategoricalPointListContainer<PointList> parseAsVerticalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);

        CategoricalPointListContainer<PointList> container = new SimpleCategoricalPointListContainerImpl();
        Iterator<? extends List<String>> rowIt = csvData.iterator();

        int rowNum = 0; // Keep track of the row number, so that we can include the erroneous row number in the exception.
        while (rowIt.hasNext()) {
            Iterator<String> lineIt = rowIt.next().iterator();
            rowNum++;

            // Check if we are in the first line, were all the categories are defined ...
            if (rowNum == 1) {
                while (lineIt.hasNext()) {
                    String catName = lineIt.next();
                    if (!catName.isEmpty()) {
                        container.pushBackCategory(catName);
                    }
                }
            // ... or if we are in a row, were the actual data sets are defined
            } else {
                // Get the name for the values of a data set
                if (!lineIt.hasNext()) {
                    throw new MalformedCsvException("Line: " + rowNum + ": Data set must contain a name");
                }
                String name = lineIt.next().trim();
                PointList pl = new SimplePointListImpl(name);

                // Parse all values
                // Set the x value of each Point to the index of the category, they belong to
                int columnNum = 0;
                while (lineIt.hasNext()) {
                    columnNum++;
                    String value = lineIt.next().trim();

                    Number val;
                    try {
                        val = Constants.NUMBER_FORMAT.parse(value);
                    } catch (final ParseException pe) {
                        throw new MalformedCsvException("Line: " + rowNum + ": Could not parse value", pe);
                    }
                    Point2DDouble p = new Point2DDouble(columnNum, val.doubleValue());
                    pl.pushBack(p);
                }
                container.pushBack(pl);
            }
        }
        return container;
    }

}
