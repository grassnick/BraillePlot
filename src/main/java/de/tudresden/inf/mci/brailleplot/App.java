package de.tudresden.inf.mci.brailleplot;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;




import de.tudresden.inf.mci.brailleplot.csvparser.CsvOrientation;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvParser;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvType;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.LineChart;


import de.tudresden.inf.mci.brailleplot.configparser.Representation;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;




import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;

import de.tudresden.inf.mci.brailleplot.printerbackend.PrintDirector;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrinterCapability;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;

import de.tudresden.inf.mci.brailleplot.commandline.CommandLineParser;
import de.tudresden.inf.mci.brailleplot.commandline.SettingType;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsReader;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsWriter;

import de.tudresden.inf.mci.brailleplot.rendering.LiblouisBrailleTextRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.MasterRenderer;

import de.tudresden.inf.mci.brailleplot.svgexporter.BoolFloatingPointDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolMatrixDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.SvgExporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.Iterator;

import java.net.URL;
import java.nio.file.Path;
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

            Printer indexV4Printer = configParser.getPrinter();
            Format a4Format = configParser.getFormat("A4");
            Representation representationParameters = configParser.getRepresentation();


            // Parse csv data
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream csvStream = classloader.getResourceAsStream("examples/csv/2_line_plot.csv");
            Reader csvReader = new BufferedReader(new InputStreamReader(csvStream));


            CsvParser csvParser = new CsvParser(csvReader, ',', '\"');
            PointListContainer<PointList> container = csvParser.parse(CsvType.DOTS, CsvOrientation.HORIZONTAL);
            mLogger.debug("Internal data representation:\n {}", container.toString());

            LineChart lineChart = new LineChart(container);
            lineChart.setTitle(settingsReader.getSetting(SettingType.DIAGRAM_TITLE).orElse(""));
            lineChart.setXAxisName(settingsReader.getSetting(SettingType.X_AXIS_LABEL).orElse(""));
            lineChart.setYAxisName(settingsReader.getSetting(SettingType.Y_AXIS_LABEL).orElse(""));

            /*
            CategoricalBarChart barChart = new CategoricalBarChart(new SimpleCategoricalPointListContainerImpl(container));
            barChart.setTitle(settingsReader.getSetting(SettingType.DIAGRAM_TITLE).orElse(""));
            barChart.setXAxisName(settingsReader.getSetting(SettingType.X_AXIS_LABEL).orElse(""));
            barChart.setYAxisName(settingsReader.getSetting(SettingType.Y_AXIS_LABEL).orElse(""));
             */
            LiblouisBrailleTextRasterizer.initModule();

            MasterRenderer renderer = new MasterRenderer(indexV4Printer, representationParameters, a4Format);
            RasterCanvas canvas = renderer.rasterize(lineChart);
            Iterator<MatrixData<Boolean>> iter = canvas.getPageIterator();
            SimpleMatrixDataImpl<Boolean> mat = (SimpleMatrixDataImpl<Boolean>) canvas.getCurrentPage();
            while (iter.hasNext()) {
                SimpleMatrixDataImpl<Boolean> temp = (SimpleMatrixDataImpl<Boolean>) iter.next();
                mLogger.debug("Render preview:\n" + temp.toBoolString());
            }



            // Render diagram
            //MasterRenderer renderer = new MasterRenderer(indexV4Printer, a4Format);
            //RasterCanvas canvas = renderer.rasterize(barChart);
            // SVG exporting

            SvgExporter<RasterCanvas> svgExporter = new BoolMatrixDataSvgExporter(canvas);
            svgExporter.render();
            svgExporter.dump("boolMat");

            // FloatingPointData SVG exporting example
            PlotCanvas floatCanvas = new PlotCanvas(indexV4Printer, representationParameters, a4Format);
            FloatingPointData<Boolean> points = floatCanvas.getNewPage();

            SvgExporter<PlotCanvas> floatSvgExporter = new BoolFloatingPointDataSvgExporter(floatCanvas);
            floatSvgExporter.render();
            floatSvgExporter.dump("floatingPointData");
           // LiblouisBrailleTextRasterizer textRasterizer = new LiblouisBrailleTextRasterizer(indexV4Printer);
            //renderer.getRenderingBase().registerRasterizer(new FunctionalRasterizer<BrailleText>(BrailleText.class, textRasterizer));
           // RasterCanvas refCanvas = renderer.rasterize(new BrailleText(" ", new Rectangle(0, 0, 0, 0)));
           // RasterCanvas m2canvas = renderer.rasterize(new BrailleText(text2, textArea));
            //SimpleMatrixDataImpl<Boolean> mat = (SimpleMatrixDataImpl<Boolean>) canvas.getCurrentPage();
            //mLogger.debug("Render preview:\n" + mat.toBoolString());



            // Check if some SpoolerService/Printservice exists
            if (!PrintDirector.isPrintServiceOn()) {
                throw new Exception("Can't find any Printservices on this System.");
            }


            // Rasterize


            // Last Step: Printing
            @SuppressWarnings("checkstyle:MagicNumber")
            PrintDirector printD = new PrintDirector(PrinterCapability.valueOf(indexV4Printer.getProperty("mode").toString().toUpperCase()), indexV4Printer);
            Iterator<MatrixData<Boolean>> itera = canvas.getPageIterator();
            itera.next();
            while (itera.hasNext()) {
                MatrixData<Boolean> page = itera.next();
                printD.print(page);
            }

        } catch (final Exception e) {
            terminateWithException(e);
        }

        runFinalizers();

        return EXIT_SUCCESS;
    }
}
