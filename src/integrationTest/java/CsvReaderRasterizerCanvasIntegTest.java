import de.tudresden.inf.mci.brailleplot.commandline.CommandLineParser;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsReader;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsWriter;
import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvOrientation;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvParser;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvType;
import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.BarChart;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * Integrationtests for the component rendering, csv reading and parsing and canvas.
 * The seperation from unittests is not done cleanly, mainly because the stubs needed to test them individually
 * are more boilercode then the test itself, hence these are also located here.
 * @author Andrey Ruzhanskiy
 * @version 28.09.2019
 */


public class CsvReaderRasterizerCanvasIntegTest {

    private static SettingsReader settingsReader;
    private static Printer printer;
    private static Format format;
    private static MatrixData<Boolean> data;
    private static CategoricalPointListContainer<PointList> container;
    private static BarChart barChart;

    @BeforeAll
    public static void setUp() {
        Assertions.assertDoesNotThrow(()-> {
            URL correct = ClassLoader.getSystemClassLoader().getResource("config/correct.properties");
            URL standard = ClassLoader.getSystemClassLoader().getResource("config/default.properties");
            JavaPropertiesConfigurationParser configParser = new JavaPropertiesConfigurationParser(correct, standard);
            printer = configParser.getPrinter();
            format = configParser.getFormat("A4");
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream csvStream = classloader.getResourceAsStream("examples/0_bar_chart_categorical_vertical_test.csv");
            Reader csvReader = new BufferedReader(new InputStreamReader(csvStream));
            CsvParser csvParser = new CsvParser(csvReader, ',', '\"');
            container = csvParser.parse(CsvType.X_ALIGNED_CATEGORIES, CsvOrientation.VERTICAL);
            barChart = new BarChart(container);
        });
    }


    @Test
    public void testGettersSmokeTestBarChart() {
        Assertions.assertDoesNotThrow(() -> {
            //BarChart tempBarchart = new BarChart(new PointListContainer<PointList>);
            barChart.getCategoryNames();
            barChart.getDataSet();
            barChart.getMaxY();
            barChart.getMinY();
            barChart.getTitle();
            barChart.getXAxisName();
            barChart.getYAxisName();
        });
    }

    @Test
    public void testGettersOutputBarChart() {
        /*Assertions.assertEquals(barChart.getMaxY());
        Assertions.assertEquals(barChart.getMinY());
        Assertions.assertEquals(barChart.getDataSet(), container);
        Assertions.assertEquals(barChart.getXAxisName());
        Assertions.assertEquals(barChart.getYAxisName());

         */
    }
    @Test
    public void testNullConstructorBarChart() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            BarChart wrongBar = new BarChart(null);
        });
    }





    @Test
    public void testCorrecctRasterizingBarChart() {
        Assertions.assertDoesNotThrow(() -> {

        });
    }
}
