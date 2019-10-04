package de.tudresden.inf.mci.brailleplot;

import ch.qos.logback.classic.Level;
import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;

import de.tudresden.inf.mci.brailleplot.configparser.Representation;
import de.tudresden.inf.mci.brailleplot.csvparser.MalformedCsvException;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimpleCategoricalPointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.diagrams.LineChart;
import de.tudresden.inf.mci.brailleplot.layout.AbstractCanvas;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrintDirector;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrinterCapability;


import de.tudresden.inf.mci.brailleplot.commandline.CommandLineParser;
import de.tudresden.inf.mci.brailleplot.commandline.SettingType;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsReader;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsWriter;

import de.tudresden.inf.mci.brailleplot.csvparser.CsvOrientation;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvParser;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvType;
import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;

import de.tudresden.inf.mci.brailleplot.rendering.LiblouisBrailleTextRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.MasterRenderer;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolFloatingPointDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolMatrixDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.SvgExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Main class.
 * Set up the application and run it.
 * @author Georg Graßnick, Andrey Ruzhanskiy, Leonard Kupper
 * @version 2019.10.03
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
            if (CommandLineParser.checkForHelp(args)) {
                cliParser.printHelp(); // If requested, print help and exit
                return EXIT_SUCCESS;
            }
            SettingsWriter settings = cliParser.parse(args);
            SettingsReader settingsReader = settings;

            setLoggingLevel(Level.valueOf(settingsReader.getSetting(SettingType.LOG_LEVEL).orElse("Info")));

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
                format = configParser.getFormat("default"); // Default behaviour from default config (A4 portrait)
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
            CsvOrientation csvOrientation;
            if (settingsReader.isTrue(SettingType.VERTICAL_CSV).orElse(false)) {
                csvOrientation = CsvOrientation.VERTICAL;
            } else {
                csvOrientation = CsvOrientation.HORIZONTAL;
            }
            switch (settingsReader.getSetting(SettingType.DIAGRAM_TYPE).orElse("").toLowerCase()) {
                case "scatterplot":
                    PointListContainer<PointList> scatterPlotContainer = csvParser.parse(CsvType.DOTS, csvOrientation);
                    diagram = new ScatterPlot(scatterPlotContainer);
                    break;
                case "linechart":
                    PointListContainer<PointList> lineChartContainer = csvParser.parse(CsvType.DOTS, csvOrientation);
                    diagram = new LineChart(lineChartContainer);
                    break;
                case "barchart":
                    CategoricalPointListContainer<PointList> barChartContainer;
                    try { // first try to parse as regular bar chart and convert to single category bar cart.
                        barChartContainer = new SimpleCategoricalPointListContainerImpl(csvParser.parse(CsvType.X_ALIGNED, csvOrientation));
                    } catch (MalformedCsvException e) { // else parse as categorical bar chart
                        barChartContainer = csvParser.parse(CsvType.X_ALIGNED_CATEGORIES, csvOrientation);
                    }
                    diagram = new CategoricalBarChart(barChartContainer);
                    break;
                default: throw new IllegalStateException("Unknown diagram type: " + settingsReader.getSetting(SettingType.DIAGRAM_TYPE).orElse("<none>"));
            }
            diagram.setTitle(settingsReader.getSetting(SettingType.DIAGRAM_TITLE).orElse(""));
            diagram.setXAxisName(settingsReader.getSetting(SettingType.X_AXIS_LABEL).orElse(""));
            diagram.setYAxisName(settingsReader.getSetting(SettingType.Y_AXIS_LABEL).orElse(""));

            // Render diagram
            LiblouisBrailleTextRasterizer.initModule();
            MasterRenderer renderer = new MasterRenderer(printer, representationParameters, format);
            PrinterCapability mode = PrinterCapability.valueOf(printer.getProperty("mode").toString().toUpperCase());
            Iterator<? extends PrintableData> outputPages;
            SvgExporter<? extends AbstractCanvas> svgExporter;
            switch (mode) { // Decide on correct rendering mode to apply
                case NORMALPRINTER:
                    RasterCanvas rasterCanvas = renderer.rasterize(diagram);
                    svgExporter = new BoolMatrixDataSvgExporter(rasterCanvas);
                    outputPages = rasterCanvas.getPageIterator();
                    break;
                case INDEX_EVEREST_D_V4_FLOATINGDOT_PRINTER:
                    PlotCanvas plotCanvas = new PlotCanvas(printer, representationParameters, format); // TODO: call renderer.plot()
                    svgExporter = new BoolFloatingPointDataSvgExporter(plotCanvas);
                    outputPages = plotCanvas.getPageIterator();
                    break;
                default: throw new UnsupportedOperationException("Mode not supported: " + mode);
            }

            // Action switches
            boolean doPrint = !settingsReader.isTrue(SettingType.INHIBIT_PRINT).orElse(false);
            boolean doSvgExport = settingsReader.isPresent(SettingType.SVG_EXPORT);
            boolean doByteDump = settingsReader.isPresent(SettingType.BYTE_DUMP);

            // SVG exporting
            if (doSvgExport) {
                File svgBaseFile = new File(settingsReader.getSetting(SettingType.SVG_EXPORT).get());
                svgExporter.render();
                svgExporter.dump(svgBaseFile.getAbsolutePath());
            }

            // Printing and Byte Dumping
            PrintDirector printD = new PrintDirector(mode, printer);
            if (doPrint && !PrintDirector.isPrintServiceOn()) { // Check for running spooler or print service
                throw new Exception("Can't find any running print services on this system.");
            }
            File dumpBaseFile = null; // Setup dump base file if required
            if (doByteDump) {
                dumpBaseFile = new File(settingsReader.getSetting(SettingType.BYTE_DUMP).get());
            }
            int pageNumber = 0;
            while (outputPages.hasNext()) { // Iterate pages
                PrintableData page = outputPages.next();
                if (doByteDump) { // Byte dump
                    try (FileOutputStream outputStream = new FileOutputStream(dumpBaseFile.getAbsolutePath() + String.format("_%03d.bin", pageNumber))) {
                        outputStream.write(printD.byteDump(page));
                    } catch (IOException ex) {
                        // Inform user, but do not stop execution
                        mLogger.error("An error occured while creating byte dump", ex);
                        throw new RuntimeException();
                    }
                }
                if (doPrint) { // Print page
                    printD.print(page);
                }
                pageNumber++;
            }
        } catch (final Exception e) {
            terminateWithException(e);
        }

        runFinalizers();

        return EXIT_SUCCESS;
    }

    public static void setLoggingLevel(final Level level) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(level);
    }
}
