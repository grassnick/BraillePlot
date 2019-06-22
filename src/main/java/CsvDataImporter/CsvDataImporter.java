package CsvDataImporter;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileReader;


/**
 * parser for compliant CSV files, creates specified representation of diagram information
 * @author Richard Schmidt
 */
public class CsvDataImporter {

    private ArrayList<ArrayList<String>> csvData;

    /**
     * copied from svgplot
     * Initiates the parser. The parser reads from the specified {@code reader}
     * and populates {@link #csvData} with lines of data.
     *
     * @param reader
     *             a reader, like {@link FileReader}, with CSV path
     * @throws IOException
     *             if the {@link CSVReader} has problems parsing
     */
    public CsvDataImporter(Reader reader) throws IOException {
        CSVReader csvReader = new CSVReader(reader);

        csvData = new ArrayList<>();

        String[] nextLine;
        while ((nextLine = csvReader.readNext()) != null) {
            csvData.add(new ArrayList<String>(Arrays.asList(nextLine)));
        }

        csvReader.close();
    }

    // TODO
    // parse csvData, return a List of points;
    // svgplot parser may be reused
}
