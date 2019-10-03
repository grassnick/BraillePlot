package de.tudresden.inf.mci.brailleplot;

import de.tudresden.inf.mci.brailleplot.commandline.CommandLineParser;
import de.tudresden.inf.mci.brailleplot.commandline.SettingType;
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
import de.tudresden.inf.mci.brailleplot.datacontainers.SimpleCategoricalPointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.GroupedBarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.LinePlot;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.diagrams.StackedBarChart;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrintDirector;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrinterCapability;
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
import java.nio.file.Path;
import java.util.ListIterator;
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
            JavaPropertiesConfigurationParser configParser;
            URL defaultConfig = getClass().getClassLoader().getResource("config/default.properties");
            if (!settingsReader.isPresent(SettingType.PRINTER_CONFIG_PATH)) { // TODO: exception if missing this argument, until then use default location for test runs
                URL configUrl = getClass().getResource("/config/index_everest_d_v4.properties");
                configParser = new JavaPropertiesConfigurationParser(configUrl, defaultConfig);
                mLogger.warn("ATTENTION! Using default specific config from resources. Please remove default config behavior before packaging the jar.");
            } else {
                Path configPath = Path.of(settingsReader.getSetting(SettingType.PRINTER_CONFIG_PATH).get());
                configParser = new JavaPropertiesConfigurationParser(configPath, defaultConfig);
            }

            Printer indexV4Printer = configParser.getPrinter();
            Format a4Format = configParser.getFormat("A4");
            Representation representationParameters = configParser.getRepresentation();

            // Parse csv data
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream csvStream = classloader.getResourceAsStream("examples/csv/0_bar_chart_categorical_vertical.csv");
            Reader csvReader = new BufferedReader(new InputStreamReader(csvStream));

            CsvParser csvParser = new CsvParser(csvReader, ',', '\"');
            CategoricalPointListContainer<PointList> container = csvParser.parse(CsvType.X_ALIGNED_CATEGORIES, CsvOrientation.VERTICAL);
            mLogger.debug("Internal data representation:\n {}", container.toString());
            CategoricalBarChart barChart = new CategoricalBarChart(container);
            barChart.setTitle(settingsReader.getSetting(SettingType.DIAGRAM_TITLE).orElse(""));
            barChart.setXAxisName(settingsReader.getSetting(SettingType.X_AXIS_LABEL).orElse(""));
            barChart.setYAxisName(settingsReader.getSetting(SettingType.Y_AXIS_LABEL).orElse(""));

            // Render diagram
            LiblouisBrailleTextRasterizer.initModule();
            MasterRenderer renderer = new MasterRenderer(indexV4Printer, representationParameters, a4Format);
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
            CsvParser csvParser2 = new CsvParser(csvReader2, ',', '\"');
            SimpleCategoricalPointListContainerImpl container3 = csvParser2.parse(CsvType.X_ALIGNED_CATEGORIES, CsvOrientation.VERTICAL);
            mLogger.debug("Internal data representation:\n {}", container.toString());

            // FloatingPointData SVG exporting example
            PlotCanvas floatCanvas = new PlotCanvas(indexV4Printer, representationParameters, a4Format);
            FloatingPointData<Boolean> points = floatCanvas.getNewPage();

            final int blockX = 230;
            final int blockY = 400;
            /*for (int y = 0; y < blockY; y += 2) {
                for (int x = 0; x < blockX; x += 2) {
            final int blockX = 210;
            final int blockY = 297;
            for (double y = 0; y < blockY; y += 1.5) {
                for (double x = 0; x < blockX; x += 1.5) {
                    Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true);
                    points.addPointIfNotExisting(point);
                }
            }*/

            ScatterPlot scatterplot = new ScatterPlot(container2);
            ScatterPlotter plotter = new ScatterPlotter();
            // plotter.plot(scatterplot, floatCanvas);


            LinePlot lineplot = new LinePlot(container2);
            LinePlotter plotter2 = new LinePlotter();
            // plotter2.plot(lineplot, floatCanvas);

            StackedBarChart sbar = new StackedBarChart(container3);
            StackedBarChartPlotter plotter3 = new StackedBarChartPlotter();
            plotter3.plot(sbar, floatCanvas);

            GroupedBarChart gbar = new GroupedBarChart(container3);
            GroupedBarChartPlotter plotter4 = new GroupedBarChartPlotter();
            // plotter4.plot(gbar, floatCanvas);

            SvgExporter<PlotCanvas> floatSvgExporter = new BoolFloatingPointDataSvgExporter(floatCanvas);
            floatSvgExporter.render();
            floatSvgExporter.dump("floatingPointData");
            // RasterCanvas m2canvas = renderer.rasterize(new BrailleText(text2, textArea));




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

            if (false) {
                return EXIT_SUCCESS;
            }

            // Last Step: Printing
            @SuppressWarnings("checkstyle:MagicNumber")
            String printerConfigUpperCase = indexV4Printer.getProperty("mode").toString().toUpperCase();
            PrintDirector printD = new PrintDirector(PrinterCapability.INDEX_EVEREST_D_V4_FLOATINGDOT_PRINTER, indexV4Printer);
            ListIterator<FloatingPointData<Boolean>> canvasIt = floatCanvas.getPageIterator();

            /*
            canvasIt.forEachRemaining((page) -> {
                Thread printingThread = new Thread(() -> {
                    mLogger.debug("Started printing thread");
                    printD.print(page);
                    mLogger.debug("Print call returned");
                });
                printingThread.start();
                while(printingThread.isAlive()) {

                }
                mLogger.debug(printingThread.getName() + " has finished.");
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

             */


            /*PrintDirector printD = new PrintDirector(PrinterCapability.valueOf(printerConfigUpperCase), indexV4Printer);
            printD.print(mat);
            FileOutputStream textDumpOutput = new FileOutputStream("dump.txt");
            textDumpOutput.write(printD.byteDump(mat));*/


        } catch (final Exception e) {
            terminateWithException(e);
        }

        runFinalizers();

        return EXIT_SUCCESS;
    }
}
