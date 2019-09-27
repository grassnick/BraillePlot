package de.tudresden.inf.mci.brailleplot;

import de.tudresden.inf.mci.brailleplot.commandline.CommandLineParser;
import de.tudresden.inf.mci.brailleplot.commandline.SettingType;
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
import de.tudresden.inf.mci.brailleplot.datacontainers.SimpleCategoricalPointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.LinePlot;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrintDirector;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrinterCapability;
import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
import de.tudresden.inf.mci.brailleplot.rendering.FunctionalRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.LiblouisBrailleTextRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.MasterRenderer;
import de.tudresden.inf.mci.brailleplot.rendering.floatingplotter.GroupedBarChartPlotter;
import de.tudresden.inf.mci.brailleplot.rendering.floatingplotter.LinePlotter;
import de.tudresden.inf.mci.brailleplot.rendering.floatingplotter.ScatterPlotter;
import de.tudresden.inf.mci.brailleplot.rendering.floatingplotter.StackedBarChartPlotter;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolFloatingPointDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolMatrixDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.SvgExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Main class.
 * Set up the application and run it.
 * @author Georg Graßnick, Andrey Ruzhanskiy
 * @version 2019.08.26
 */

public final class App {

    /**
     * Main method.
     * Instantiate application and execute it.
     * @param args Command line parameters.
     */
    public static void main(final String[] args) {
        App app = App.getInstance();
        System.exit(app.run(args));
    }

    private static App sInstance;
    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_ERROR = 1;

    private final Logger mLogger;

    private ConcurrentLinkedDeque<Runnable> mFinalizers;

    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;

    private App() {
        sInstance = this;
        mFinalizers = new ConcurrentLinkedDeque<>();
        mLogger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Returns the instance of the singleton class.
     * @return The only class instance.
     */
    public static App getInstance() {
        if (sInstance == null) {
            return new App();
        } else {
            return sInstance;
        }
    }

    /**
     * Registers a finalizer.
     * Currently, this is an experimental feature.
     * Finalizers are run before program termination, even after exceptions.
     * Finalizers are run in reverse order of their insertion.
     * A possible use would be waiting for a logger to finish flushing the logs to disk.
     * @param r The task to perform.
     */
    public static void registerFinalizer(final Runnable r) {
        getInstance().mFinalizers.addFirst(r);
    }

    private void runFinalizers() {
        for (Runnable r : mFinalizers) {
            r.run();
        }
    }

    /**
     * Terminate the complete Application in case of an untreatable error.
     * @param e The Exception that led to the error.
     */
    private void terminateWithException(final Exception e) {
        terminateWithException(e, "");
    }

    /**
     * Terminate the complete Application in case of an untreatable error.
     * @param e The Exception that led to the error.
     * @param message An additional message to print to stderr.
     */
    private void terminateWithException(final Exception e, final String message) {
        if (!message.isEmpty()) {
            mLogger.error(message);
        }
        mLogger.error("Application will now shut down due to an unrecoverable error", e);
        runFinalizers();
        System.exit(EXIT_ERROR);
    }

    @SuppressWarnings("MethodLength")
    /**
     * Main loop of the application.
     * @param args Command line parameters.
     * @return 0 if Application exited successfully, 1 on error.
     */
    int run(final String[] args) {

        // Has to be the first finalizer to be added, so that it is run last
        registerFinalizer(() -> {
            mLogger.info("Application terminated");
        });

        try {
            // Logging example
            mLogger.info("Application started");
            // Needed for Windows machines
            System.setProperty("jna.library.path", System.getProperty("user.dir") + "/third_party");
            // Parse command line parameters
            CommandLineParser cliParser = new CommandLineParser();
            SettingsWriter settings = cliParser.parse(args);
            SettingsReader settingsReader = settings;


            // If requested, print help and exit
            Optional<Boolean> printHelp = settingsReader.isTrue(SettingType.DISPLAY_HELP);
            if (printHelp.isPresent() && printHelp.get()) {
                cliParser.printHelp();
                return EXIT_SUCCESS;
            }

            // Config Parsing
            URL configPath;
            if (!settingsReader.isPresent(SettingType.PRINTER_CONFIG_PATH)) { // TODO: exception if missing this argument, until then use default location for test runs
                configPath = getClass().getResource("/config/index_everest_d_v4.properties");
                mLogger.warn("ATTENTION! Using default specific config from resources. Please remove default config behavior before packaging the jar.");
            } else {
                configPath = new URL(settingsReader.getSetting(SettingType.PRINTER_CONFIG_PATH).get());
            }

            JavaPropertiesConfigurationParser configParser = new JavaPropertiesConfigurationParser(configPath, getClass().getClassLoader().getResource("config/default.properties"));
            Printer indexV4Printer = configParser.getPrinter();
            Format a4Format = configParser.getFormat("A4");

            // Parse CLI parameters
            String[] axes = new String[FIVE];
            for (int i = 0; i < FIVE; i++) {
                axes[i] = "dummy";
            }
            if (settingsReader.isPresent((SettingType.TITLE))) {
                axes[0] = settingsReader.getSetting(SettingType.TITLE).get();
            }
            if (settingsReader.isPresent((SettingType.X_NAME))) {
                axes[1] = settingsReader.getSetting(SettingType.X_NAME).get();
            }
            if (settingsReader.isPresent((SettingType.X_UNIT))) {
                axes[2] = settingsReader.getSetting(SettingType.X_UNIT).get();
            }
            if (settingsReader.isPresent((SettingType.Y_NAME))) {
                axes[THREE] = settingsReader.getSetting(SettingType.Y_NAME).get();
            }
            if (settingsReader.isPresent((SettingType.Y_UNIT))) {
                axes[FOUR] = settingsReader.getSetting(SettingType.Y_UNIT).get();
            }

            // Parse csv data
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream csvStream = classloader.getResourceAsStream("examples/csv/0_bar_chart_categorical_vertical.csv");
            Reader csvReader = new BufferedReader(new InputStreamReader(csvStream));

            CsvParser csvParser = new CsvParser(csvReader, ',', '\"');
            CategoricalPointListContainer<PointList> container = csvParser.parse(CsvType.X_ALIGNED_CATEGORIES, CsvOrientation.VERTICAL);
            container.setAxes(axes);
            mLogger.debug("Internal data representation:\n {}", container.toString());
            CategoricalBarChart barChart = new CategoricalBarChart(container);
            barChart.setTitle("Beispieldiagramm");
            barChart.setXAxisName("Gewicht in kg");
            barChart.setYAxisName("Länge in m");

            // Render diagram
            MasterRenderer renderer = new MasterRenderer(indexV4Printer, a4Format);
            RasterCanvas canvas = renderer.rasterize(barChart);
            // SVG exporting
            SvgExporter<RasterCanvas> svgExporter = new BoolMatrixDataSvgExporter(canvas);
            svgExporter.render();
            svgExporter.dump("boolMat");

            // Plotting
            classloader = Thread.currentThread().getContextClassLoader();
            csvStream = classloader.getResourceAsStream("examples/csv/1_scatter_plot.csv");
            csvReader = new BufferedReader(new InputStreamReader(csvStream));
            InputStream csvStream2 = classloader.getResourceAsStream("examples/csv/0_bar_chart_categorical_max.csv");
            Reader csvReader2 = new BufferedReader(new InputStreamReader(csvStream2));

            csvParser = new CsvParser(csvReader, ',', '\"');
            PointListContainer<PointList> container2 = csvParser.parse(CsvType.DOTS, CsvOrientation.HORIZONTAL);
            container2.setAxes(axes);
            CsvParser csvParser2 = new CsvParser(csvReader2, ',', '\"');
            SimpleCategoricalPointListContainerImpl container3 = csvParser2.parse(CsvType.X_ALIGNED_CATEGORIES, CsvOrientation.VERTICAL);
            container3.setAxes(axes);
            mLogger.debug("Internal data representation:\n {}", container.toString());

            // FloatingPointData SVG exporting example
            PlotCanvas floatCanvas = new PlotCanvas(indexV4Printer, a4Format);
            FloatingPointData<Boolean> points = floatCanvas.getNewPage();

            final int blockX = 230;
            final int blockY = 400;
            /*for (int y = 0; y < blockY; y += 2) {
                for (int x = 0; x < blockX; x += 2) {
                    Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true);
                    points.addPoint(point);
                }
            }*/

            ScatterPlot scatterplot = new ScatterPlot(container2);
            ScatterPlotter plotter = new ScatterPlotter();
            // plotter.plot(scatterplot, floatCanvas);

            LinePlot lineplot = new LinePlot(container2);
            LinePlotter plotter2 = new LinePlotter();
            plotter2.plot(lineplot, floatCanvas);

            CategoricalBarChart bar = new CategoricalBarChart(container3);
            StackedBarChartPlotter plotter3 = new StackedBarChartPlotter();
            // plotter3.plot(bar, floatCanvas);

            GroupedBarChartPlotter plotter4 = new GroupedBarChartPlotter();
            // plotter4.plot(bar, floatCanvas);

            SvgExporter<PlotCanvas> floatSvgExporter = new BoolFloatingPointDataSvgExporter(floatCanvas);
            floatSvgExporter.render();
            floatSvgExporter.dump("floatingPointData");
            LiblouisBrailleTextRasterizer textRasterizer = new LiblouisBrailleTextRasterizer(indexV4Printer);
            renderer.getRenderingBase().registerRasterizer(new FunctionalRasterizer<BrailleText>(BrailleText.class, textRasterizer));
            RasterCanvas refCanvas = renderer.rasterize(new BrailleText(" ", new Rectangle(0, 0, 0, 0)));
            // RasterCanvas m2canvas = renderer.rasterize(new BrailleText(text2, textArea));
            SimpleMatrixDataImpl<Boolean> mat = (SimpleMatrixDataImpl<Boolean>) canvas.getCurrentPage();
            mLogger.debug("Render preview:\n" + mat.toBoolString());



            // Check if some SpoolerService/Printservice exists
            if (!PrintDirector.isPrintServiceOn()) {
                throw new Exception("Can't find any Printservices on this System.");
            }

            /*
             We do not want to actually print on each run.
            Until CLI parsing is fully integrated, you will have to disable this check by hand if you actually do
            want to print.
            Please do not commit changes to this.
            */
            if (true) {
                return EXIT_SUCCESS;
            }

            // Last Step: Printing
            @SuppressWarnings("checkstyle:MagicNumber")
            String printerConfigUpperCase = indexV4Printer.getProperty("mode").toString().toUpperCase();
            PrintDirector printD = new PrintDirector(PrinterCapability.valueOf(printerConfigUpperCase), indexV4Printer);
            /*printD.print(mat);*/



        } catch (final Exception e) {
            terminateWithException(e);
        }

        runFinalizers();

        return EXIT_SUCCESS;
    }
}
