package de.tudresden.inf.mci.brailleplot.csvparser;

import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to represent the main parser. This parser chooses the corresponding parsing algorithm for the data.
 */
public class CsvParser {

    static final Logger LOG = LoggerFactory.getLogger(CsvParser.class);

    private ArrayList<ArrayList<String>> mCsvData;

    /**
     * Initiates the parser. The parser reads from the specified {@code reader}
     * and populates {@link #mCsvData}.
     *
     * @param reader
     *            a reader, like {@link Reader}
     * @param separator char
     * @param quoteChar char
     * @throws IOException
     *             if the {@link CSVReader} has problems parsing
     */
    public CsvParser(final Reader reader, final char separator, final char quoteChar) throws IOException {
        CSVReader csvReader = new CSVReader(reader, separator, quoteChar);

        mCsvData = new ArrayList<>();

        String[] nextLine;
        while ((nextLine = csvReader.readNext()) != null) {
            mCsvData.add(new ArrayList<String>(Arrays.asList(nextLine)));
        }

        csvReader.close();
    }

    /**
     * Chooses the right parsing algorithm.
     * @param csvType CsvType
     * @param csvOrientation CsvOrientation
     * @return PointListList
     */
    public PointListList parse(final CsvType csvType, final CsvOrientation csvOrientation) {
        CsvParseAlgorithm csvParseAlgorithm;

        LOG.info("Parse die Daten als \"{}\", Orientierung \"{}\"", csvType, csvOrientation);

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
