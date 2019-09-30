package de.tudresden.inf.mci.brailleplot;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;

import de.tudresden.inf.mci.brailleplot.configparser.Representation;
import de.tudresden.inf.mci.brailleplot.csvparser.MalformedCsvException;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimpleCategoricalPointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrintDirector;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrinterCapability;

import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;

import de.tudresden.inf.mci.brailleplot.commandline.CommandLineParser;
import de.tudresden.inf.mci.brailleplot.commandline.SettingType;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsReader;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsWriter;

import de.tudresden.inf.mci.brailleplot.csvparser.CsvOrientation;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvParser;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvType;
import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;

import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
import de.tudresden.inf.mci.brailleplot.rendering.FunctionalRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.LiblouisBrailleTextRasterizer;

import de.tudresden.inf.mci.brailleplot.rendering.MasterRenderer;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolFloatingPointDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolMatrixDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.SvgExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import static tec.units.ri.unit.Units.METRE;

/**
 * Main class.
 * Set up the application and run it.
 * @author Georg Graßnick, Andrey Ruzhanskiy
 * @version 2019.09.30
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

            // Set up Printer, Representation & Format Configurables
            Printer printer = configParser.getPrinter();
            Representation representationParameters = configParser.getRepresentation();
            Format format;
            if (!settingsReader.isPresent(SettingType.FORMAT)) {
                format = configParser.getFormat("A4"); // Default behaviour is A4 portrait
            } else {
                format = configParser.getFormat(settingsReader.getSetting(SettingType.FORMAT).get());
            }

            // Parse csv data and create diagram
            InputStream csvStream;
            if (!settingsReader.isPresent(SettingType.CSV_LOCATION)) {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                csvStream = classloader.getResourceAsStream("examples/csv/0_bar_chart_categorical.csv");
                mLogger.warn("ATTENTION! Using example csv. Please remove this behavior before packaging the jar.");
            } else {
                csvStream = new FileInputStream(settingsReader.getSetting(SettingType.CSV_LOCATION).get());
            }
            Reader csvReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(csvStream)));
            CsvParser csvParser = new CsvParser(csvReader, ',', '\"');
            Diagram diagram;
            CategoricalPointListContainer<PointList> container;
            CsvOrientation csvOrientation;
            if (settingsReader.isTrue(SettingType.VERTICAL).orElse(false)) {
                csvOrientation = CsvOrientation.VERTICAL;
            } else {
                csvOrientation = CsvOrientation.HORIZONTAL;
            }
            switch (settingsReader.getSetting(SettingType.DIAGRAM_TYPE).orElse("")) {
                case "ScatterPlot":
                    container = csvParser.parse(CsvType.DOTS, csvOrientation);
                    throw new UnsupportedOperationException("Scatter Plots coming soon.");
                case "LineChart":
                    container = csvParser.parse(CsvType.DOTS, csvOrientation);
                    throw new UnsupportedOperationException("Line Charts coming soon.");
                case "BarChart":
                    try { // first try to parse as regualar bar chart and convert to single category bar cart.
                        container = new SimpleCategoricalPointListContainerImpl(csvParser.parse(CsvType.X_ALIGNED, csvOrientation));
                    } catch (MalformedCsvException e) { // else parse as categorical bar chart
                        container = csvParser.parse(CsvType.X_ALIGNED_CATEGORIES, csvOrientation);
                    }
                    diagram = new CategoricalBarChart(container);
                    break;
                default: throw new IllegalStateException("Unknown diagram type: " + settingsReader.getSetting(SettingType.DIAGRAM_TYPE).orElse("<none>"));
            }
            diagram.setTitle(settingsReader.getSetting(SettingType.DIAGRAM_TITLE).orElse(""));
            diagram.setXAxisName(settingsReader.getSetting(SettingType.X_AXIS_LABEL).orElse(""));
            diagram.setYAxisName(settingsReader.getSetting(SettingType.Y_AXIS_LABEL).orElse(""));

            // Render diagram
            LiblouisBrailleTextRasterizer.initModule();
            MasterRenderer renderer = new MasterRenderer(printer, representationParameters, format);
            RasterCanvas canvas = renderer.rasterize(diagram);

            // SVG exporting
            SvgExporter<RasterCanvas> svgExporter = new BoolMatrixDataSvgExporter(canvas);
            svgExporter.render();
            svgExporter.dump("boolMat");

            // FloatingPointData SVG exporting example
            PlotCanvas floatCanvas = new PlotCanvas(printer, representationParameters, format);
            FloatingPointData<Boolean> points = floatCanvas.getNewPage();

            final int blockX = 230;
            final int blockY = 400;
            for (int y = 0; y < blockY; y += 2) {
                for (int x = 0; x < blockX; x += 2) {
                    Point2DValued<Quantity<Length>, Boolean> point = new Point2DValued<>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true);
                    points.addPoint(point);
                }
            }

            SvgExporter<PlotCanvas> floatSvgExporter = new BoolFloatingPointDataSvgExporter(floatCanvas);
            floatSvgExporter.render();
            floatSvgExporter.dump("floatingPointData");
            LiblouisBrailleTextRasterizer textRasterizer = new LiblouisBrailleTextRasterizer(printer);
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
            String printerConfigUpperCase = printer.getProperty("mode").toString().toUpperCase();
            PrintDirector printD = new PrintDirector(PrinterCapability.valueOf(printerConfigUpperCase), printer);
            printD.print(mat);

        } catch (final Exception e) {
            terminateWithException(e);
        }

        runFinalizers();

        return EXIT_SUCCESS;
    }
}
