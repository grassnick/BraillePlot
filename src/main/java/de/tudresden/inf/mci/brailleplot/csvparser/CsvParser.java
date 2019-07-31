package de.tudresden.inf.mci.brailleplot.csvparser;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class to represent the main parser. This parser chooses the corresponding parsing algorithm for the data.
 * @author SVGPlott-Team, Georg Graßnick
 * @version 2019.07.29
 */
public class CsvParser {

    private final Logger mLogger = LoggerFactory.getLogger(CsvParser.class);

    public ArrayList<ArrayList<String>> mCsvData;

    /**
     * Initiates the parser. The parser reads from the specified {@code reader}
     * and populates the internal data structures.
     *
     * @param reader The {@link Reader} interfacing the actual csv file object. Must not be null.
     * @param separator char The character which is used to separate the values in the csv file.
     * @param quoteChar char The character which is used to quote text paragraphs.
     * @throws IOException Is thrown, if an error occurs while performing read operations on the reader.
     */
    public CsvParser(final Reader reader, final char separator, final char quoteChar) throws IOException {
        Objects.requireNonNull(reader);
        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withCSVParser(new CSVParserBuilder()
                        .withQuoteChar(quoteChar)
                        .withSeparator(separator)
                        .build())
                .build();

        mCsvData = new ArrayList<>();

        for (String[] line : csvReader) {
            mCsvData.add(new ArrayList<>(Arrays.asList(line)));
            mLogger.trace("Read line: {}", Arrays.toString(line));
        }

        csvReader.close();
    }

    /**
     * Chooses the right parsing algorithm.
     * @param csvType CsvType
     * @param csvOrientation CsvOrientation
     * @return PointListList
     */
    public PointListContainer<PointList> parse(final CsvType csvType, final CsvOrientation csvOrientation) {
        CsvParseAlgorithm csvParseAlgorithm;

        mLogger.debug("Parsing data as \"{}\", orientation \"{}\"", csvType, csvOrientation);

        switch (csvType) {
        case DOTS:
            csvParseAlgorithm = new CsvDotParser();
            break;
        case X_ALIGNED:
            csvParseAlgorithm = new CsvXAlignedParser();
            break;
        case X_ALIGNED_CATEGORIES:
            csvParseAlgorithm = new CsvXAlignedCategoriesParser();
            break;
        default:
            return null;
        }

        switch (csvOrientation) {
        case HORIZONTAL:
            return csvParseAlgorithm.parseAsHorizontalDataSets(mCsvData);
        case VERTICAL:
            return csvParseAlgorithm.parseAsVerticalDataSets(mCsvData);
        default:
            return null;
        }
    }
}
