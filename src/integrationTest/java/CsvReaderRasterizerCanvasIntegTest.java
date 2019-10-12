import de.tudresden.inf.mci.brailleplot.commandline.CommandLineParser;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsReader;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsWriter;
import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.configparser.Representation;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvOrientation;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvParser;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvType;
import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.BarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.LineChart;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.SixDotBrailleRasterCanvas;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrintDirector;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrinterCapability;
import de.tudresden.inf.mci.brailleplot.rendering.BarChartRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.FunctionalRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.Image;
import de.tudresden.inf.mci.brailleplot.rendering.ImageRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.LiblouisBrailleTextRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.MasterRenderer;
import de.tudresden.inf.mci.brailleplot.rendering.UniformTextureBarChartRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.floatingplotter.BarChartPlotter;
import de.tudresden.inf.mci.brailleplot.rendering.floatingplotter.GroupedBarChartPlotter;
import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolFloatingPointDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolMatrixDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.SvgExporter;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.io.BufferedReader;
import java.io.File;
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
    private static CategoricalPointListContainer<PointList> containerCatBarPlot;
    private static PointListContainer<PointList> containerLinePlot;
    private static PointListContainer<PointList> containerScatter;
    private static CategoricalBarChart catBarPlot;
    private static CategoricalBarChart catBarChart;
    private static ScatterPlot scatterPlot;
    private static LineChart lineChart;
    private static MasterRenderer renderer;
    private static  MasterRenderer rendererBarAcc;

    private static MasterRenderer rendererFloating;
    private static Printer printerFloating;
    private static Printer printerFloatingBarAcc;
    private static Format formatFloating;
    private static Format formatFloatingBarAcc;

    private static Representation representationParameters;
    private static Representation representationParametersFloating;
    private static Representation representationParametersFloatingBarAcc;


    @BeforeAll
    public static void setUp() {
        Assertions.assertDoesNotThrow(()-> {
            URL correct = ClassLoader.getSystemClassLoader().getResource("config/correct.properties");
            URL standard = ClassLoader.getSystemClassLoader().getResource("config/default.properties");

            URL correctFloating = ClassLoader.getSystemClassLoader().getResource("config/correct_floatingdot.properties");
            URL defaultFloating = ClassLoader.getSystemClassLoader().getResource("config/default_floatingdot.properties");
            URL defaultFloatingBarAcc = ClassLoader.getSystemClassLoader().getResource("config/default_floatingdot_bar_accumulation.properties");

            JavaPropertiesConfigurationParser configParser = new JavaPropertiesConfigurationParser(correct, standard);
            JavaPropertiesConfigurationParser configParserFloating = new JavaPropertiesConfigurationParser(correctFloating, defaultFloating);
            JavaPropertiesConfigurationParser configParserFloatingBarAcc = new JavaPropertiesConfigurationParser(correctFloating, defaultFloatingBarAcc);

            printerFloating = configParserFloating.getPrinter();
            formatFloating = configParserFloating.getFormat("A3");

            printerFloatingBarAcc = configParserFloatingBarAcc.getPrinter();
            formatFloatingBarAcc = configParserFloatingBarAcc.getFormat("A4");


            printer = configParser.getPrinter();
            format = configParser.getFormat("A4");
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();

            InputStream csvStream = classloader.getResourceAsStream("examples/0_bar_chart_categorical_vertical_test.csv");
            InputStream csvStreamScatter = classloader.getResourceAsStream("examples/1_scatter_plot_dense_test.csv");
            InputStream csvStreamLinePlot = classloader.getResourceAsStream("examples/2_line_chart_test.csv");
            InputStream csvStreamBarCatPlot = classloader.getResourceAsStream("examples/0_bar_chart_categorical_max_test.csv");

            Reader csvReader = new BufferedReader(new InputStreamReader(csvStream));
            Reader csvReaderScatter = new BufferedReader(new InputStreamReader(csvStreamScatter));
            Reader csvReaderLinePlot = new BufferedReader(new InputStreamReader(csvStreamLinePlot));
            Reader csvReaderBarCatPlot = new BufferedReader(new InputStreamReader(csvStreamBarCatPlot));


            CsvParser csvParser = new CsvParser(csvReader, ',', '\"');
            CsvParser csvParserScatter = new CsvParser(csvReaderScatter, ',', '\"');
            CsvParser csvParserLinePlot = new CsvParser(csvReaderLinePlot, ',', '\"');
            CsvParser csvParserBarCatPlot = new CsvParser(csvReaderBarCatPlot, ',', '\"');

            container = csvParser.parse(CsvType.X_ALIGNED_CATEGORIES, CsvOrientation.VERTICAL);
            containerScatter = csvParserScatter.parse(CsvType.DOTS, CsvOrientation.HORIZONTAL);
            containerLinePlot = csvParserLinePlot.parse(CsvType.DOTS, CsvOrientation.HORIZONTAL);
            containerCatBarPlot = csvParserBarCatPlot.parse(CsvType.X_ALIGNED_CATEGORIES, CsvOrientation.VERTICAL);


            catBarPlot = new CategoricalBarChart(containerCatBarPlot);
            catBarChart = new CategoricalBarChart(container);
            scatterPlot = new ScatterPlot(containerScatter);
            lineChart = new LineChart(containerLinePlot);

            representationParameters = configParser.getRepresentation();
            representationParametersFloating = configParserFloating.getRepresentation();
            representationParametersFloatingBarAcc = configParserFloatingBarAcc.getRepresentation();

            rendererFloating = new MasterRenderer(printerFloating, representationParametersFloating, formatFloating);
            renderer = new MasterRenderer(printer, representationParameters, format);
            rendererBarAcc = new MasterRenderer(printerFloatingBarAcc, representationParametersFloatingBarAcc, formatFloatingBarAcc);
            System.setProperty("jna.library.path", System.getProperty("user.dir") + "/third_party");
            LiblouisBrailleTextRasterizer.initModule();
        });
    }


    @Test
    public void testGettersSmokeTestCatBarChart() {
        Assertions.assertDoesNotThrow(() -> {
            //BarChart tempBarchart = new BarChart(new PointListContainer<PointList>);
            catBarChart.getDataSet();
            catBarChart.getMaxY();
            catBarChart.getMinY();
            catBarChart.getTitle();
            catBarChart.getXAxisName();
            catBarChart.getYAxisName();
        });
    }

    @Test
    public void testGettersOutputCatBarChart() {
        Assertions.assertEquals(catBarChart.getMaxY(), 4.5);
        Assertions.assertEquals(catBarChart.getMinY(), 1.0);
        Assertions.assertEquals(catBarChart.getDataSet(), container);
        Assertions.assertDoesNotThrow(() -> {
            catBarChart.setTitle("Test");
            catBarChart.setYAxisName("y");
            catBarChart.setXAxisName("x");
        });
        Assertions.assertEquals(catBarChart.getXAxisName(),"x");
        Assertions.assertEquals(catBarChart.getTitle(),"Test");
        Assertions.assertEquals(catBarChart.getYAxisName(), "y");

    }

    /*
        It would be better if a nullpointerException was thrown
     */
    @Test
    public void testGettersNullCatBarChart() {
        BarChart wrongBarChart = new BarChart(container);
        Assertions.assertEquals(wrongBarChart.getTitle(), null);
        Assertions.assertEquals(wrongBarChart.getXAxisName(), null);
        Assertions.assertEquals(wrongBarChart.getYAxisName(), null);
    }
    @Test
    public void testNullConstructorCatBarChart() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            BarChart wrongBar = new BarChart(null);
        });
    }

    @Test
    public void testNoRegisteredRasterizerBarChart() {
        Assertions.assertThrows(IllegalArgumentException.class,() -> {
            renderer.rasterize(new BarChart(container));
        });
    }

    @Test
    public void testRegisterUniformOnRenderingbase() {
        BarChart barChart = new BarChart(container);
        Assertions.assertDoesNotThrow(() -> {
            renderer.getRenderingBase().registerRasterizer(new FunctionalRasterizer<BarChart>(BarChart.class, new UniformTextureBarChartRasterizer()));
            renderer.rasterize(barChart);
        });
    }

    @Test
    public void printCatBarChart() {
        Assertions.assertDoesNotThrow(() -> {
            PrintDirector printerD = new PrintDirector(PrinterCapability.NORMALPRINTER, printer);
            CategoricalBarChart catChart = new CategoricalBarChart(container);
            catChart.setYAxisName("Y");
            catChart.setXAxisName("X");
            catChart.setTitle("Title");
            printerD.print(renderer.rasterize(catChart).getCurrentPage());
        });
    }
    @Test
    public void testRasterizerDoesNotThrowCatBarChart() {
        Assertions.assertDoesNotThrow(() -> {
            catBarChart.setTitle("Title");
            catBarChart.setXAxisName("X-axis");
            catBarChart.setYAxisName("Y-axis");
            renderer.rasterize(catBarChart);
        });
    }


    // SVG Exporter
    @Test
    public void testConstructorSvgExporterBoolMatrix() {
        Assertions.assertDoesNotThrow(() -> {
            CategoricalBarChart temp = new CategoricalBarChart(container);
            temp.setTitle("test");
            temp.setXAxisName("X");
            temp.setYAxisName("Y");
            SvgExporter<RasterCanvas> svgExporter = new BoolMatrixDataSvgExporter(renderer.rasterize(temp));
            svgExporter.render();
        });
    }

    @Test
    public void testConstructorSvgExporterBoolFloat() {
        Assertions.assertDoesNotThrow(() -> {
            PlotCanvas floatCanvas = new PlotCanvas(printer, representationParameters, format);
            SvgExporter<PlotCanvas> floatSvgExporter = new BoolFloatingPointDataSvgExporter(floatCanvas);
            floatSvgExporter.render();
            floatSvgExporter.dump("floatingPointData");
        });
    }

    // LiblouisTextRasterizer Test

    @Test
    public void testLibLoisMethods() {
        Assertions.assertDoesNotThrow(() -> {
            LiblouisBrailleTextRasterizer rast = new LiblouisBrailleTextRasterizer(printer);
            rast.calculateRequiredHeight("test",30, new SixDotBrailleRasterCanvas(printer, representationParameters, format),BrailleLanguage.Language.DE_KURZSCHRIFT);
            rast.getBrailleStringLength("test", BrailleLanguage.Language.DE_BASISSCHRIFT);
        });
    }

    // Image rasterizer test

    @Test
    public void testImageRasterizer() {
        Assertions.assertDoesNotThrow(() -> {
            Image image = new Image(getClass().getResource("/exampleimages/image.jpg"));
            renderer.getRenderingBase().registerRasterizer(new FunctionalRasterizer<Image>(Image.class, new ImageRasterizer(true,true,false, 80)));
            renderer.rasterize(image);
            renderer.getRenderingBase().registerRasterizer(new FunctionalRasterizer<Image>(Image.class, new ImageRasterizer(true,true,true, 80)));
            renderer.rasterize(image);
        });
    }
    @Test
    public void testScatterPlotRasterize() {
        Assertions.assertDoesNotThrow(() -> {
            scatterPlot.setTitle("Title");
            scatterPlot.setXAxisName("Y-Axis");
            scatterPlot.setXAxisName("X-Axis");
            renderer.rasterize(scatterPlot);
        });
    }

    @Test
    public void testPlotLineChart() {
        Assertions.assertDoesNotThrow(() -> {
            // GroupedBarChartPlotter groupedBarChartPlotter = new GroupedBarChartPlotter();
            PlotCanvas plotCanvas = rendererFloating.plot(lineChart);
            //groupedBarChartPlotter.plot();
        });
    }

    @Test
    public void testPlotScatter() {
        Assertions.assertDoesNotThrow(() -> {
            // GroupedBarChartPlotter groupedBarChartPlotter = new GroupedBarChartPlotter();
            PlotCanvas plotCanvas = rendererFloating.plot(scatterPlot);
            //groupedBarChartPlotter.plot();
        });
    }

    @Test
    public void testPlotCategoricalBarPlot() {
        Assertions.assertDoesNotThrow(() -> {
            // GroupedBarChartPlotter groupedBarChartPlotter = new GroupedBarChartPlotter();
            PlotCanvas plotCanvas = rendererFloating.plot(catBarPlot);
            //groupedBarChartPlotter.plot();
        });
    }

    @Test
    public void testPlotAccBarChart() {
        Assertions.assertDoesNotThrow(() -> {
            PlotCanvas plotCanvas = rendererBarAcc.plot(catBarPlot);
        });
    }

    // Print byte dump test

    @Test
    public void testByteDump() {
        Assertions.assertDoesNotThrow(() ->{
            CategoricalBarChart temp = new CategoricalBarChart(container);
            temp.setTitle("test");
            temp.setXAxisName("X");
            temp.setYAxisName("Y");
            PrintDirector printD = new PrintDirector(PrinterCapability.NORMALPRINTER, printer);
            printD.byteDump(renderer.rasterize(temp).getCurrentPage());
        });
    }

    @Test
    public void testFloatingDotBuilder() {
        Assertions.assertDoesNotThrow(() -> {
            CategoricalBarChart temp = new CategoricalBarChart(container);
            temp.setTitle("test");
            temp.setXAxisName("X");
            temp.setYAxisName("Y");
            PrintDirector printD = new PrintDirector(PrinterCapability.NORMALPRINTER, printer);
            printD.print(renderer.rasterize(temp).getCurrentPage());
        });
    }
}
