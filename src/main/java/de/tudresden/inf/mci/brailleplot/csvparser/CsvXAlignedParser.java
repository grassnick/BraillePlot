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
 * Parser for CSV files with aligned X-values. Inherits from CsvParseAlgorithm.
 * @author SVGPlott-Team, Georg Graßnick
 * @version 2019.07.29
 */
public class CsvXAlignedParser extends CsvParseAlgorithm<PointListContainer<PointList>> {

    @Override
    public PointListContainer<PointList> parseAsHorizontalDataSets(final List<? extends List<String>> csvData) {
        Objects.requireNonNull(csvData);

        // The csv representation of horizontal datasets is essentially the transposition of vertical datasets.
        List<? extends List<String>> transposedCsvData = transposeCSV(csvData);
        return parseAsVerticalDataSets(transposedCsvData);
    }

    @Override
    // This method has been implemented from scratch, as there is no documentation about the structure of the CSV files whatsoever.
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
