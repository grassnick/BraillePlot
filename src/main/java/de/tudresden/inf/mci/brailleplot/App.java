package de.tudresden.inf.mci.brailleplot;

import de.tudresden.inf.mci.brailleplot.configparser.ConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;

import de.tudresden.inf.mci.brailleplot.exporter.PrintDirector;
import de.tudresden.inf.mci.brailleplot.exporter.PrinterConfiguration;

import de.tudresden.inf.mci.brailleplot.commandline.CommandLineParser;
import de.tudresden.inf.mci.brailleplot.commandline.SettingType;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsReader;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsWriter;

import de.tudresden.inf.mci.brailleplot.rendering.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.rendering.Image;
import de.tudresden.inf.mci.brailleplot.rendering.MasterRenderer;
import diagrams.BarChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.CategorialPointListList;
import parser.CsvOrientation;
import parser.CsvParser;
import parser.CsvType;

import java.io.File;
import java.io.FileReader;
import java.util.Optional;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * Main class.
 * Set up the application and run it.
 * @author Georg Graßnick, Andrey Ruzhanskiy
 * @version 28.06.19
 */

public final class App {

    /**
     * Main method.
     * Instantiate application and execute it.
     * @param args Command line parameters.
     */
    public static void main(final String[] args) throws IOException {
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
    @SuppressWarnings("checkstyle:MagicNumber")
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

            // Parse csv data
            String csvPath = getClass().getClassLoader().getResource("0_bar_chart.csv").getFile();
            CsvType csvType = CsvType.X_ALIGNED_CATEGORIES;
            CsvOrientation csvOrientation = CsvOrientation.HORIZONTAL;
            CsvParser parser = new CsvParser(new FileReader(csvPath), ',', '"');
            CategorialPointListList points = (CategorialPointListList) parser.parse(csvType, csvOrientation);
            BarChart exampleBarChart = new BarChart(points);

            // ...

            // Config Parsing

            String usedPrinter = "index_everest_d_v4.properties";
            //String usedPrinter = "index_basic_d.properties";
            String defaultConfigFilePath = getClass().getClassLoader().getResource("default.properties").getFile();
            String configFilePath = getClass().getClassLoader().getResource(usedPrinter).getFile();
            ConfigurationParser configParser = new JavaPropertiesConfigurationParser(configFilePath, defaultConfigFilePath);
            Printer printerConfig = configParser.getPrinter();
            Format formatConfig = configParser.getFormat("wide");


            // Rasterizing
            MasterRenderer renderer = new MasterRenderer(printerConfig, formatConfig);
            //RasterCanvas canvas = renderer.rasterize(exampleBarChart);
            File imageFile = new File(getClass().getClassLoader().getResource("2_image_chart.png").getFile());
            Image image = new Image(imageFile);
            RasterCanvas canvas = renderer.rasterize(image);
            System.out.println(canvas.getCurrentPage());


            // Last Step: Printing


            if (PrintDirector.printerExists(printerConfig.getProperty("name").toString())) {
                System.out.println("Ja");
            } else {
                System.out.println("Nein");
            }


            PrintDirector printD = new PrintDirector(PrinterConfiguration.NORMALPRINTER);
            printD.print(printerConfig.getProperty("name").toString(), canvas.getCurrentPage());
            /*
            byte[] data = lt.buildDemo(1);
            lt.printString(data);
            */

        } catch (final Exception e) {
            terminateWithException(e);
        }

        runFinalizers();

        return EXIT_SUCCESS;
    }

/*
    public  void dummyConfigurationParsing() {

        String workingDir = System.getProperty("user.dir");
        String defaultConfigPath = workingDir + "/defaultConfig.properties";
        String concreteConfigPath = workingDir + "/dummyPrinterConfig.properties";

        // create parser and parse default config
        try {
            JavaPropertiesConfigurationParser configParser = new JavaPropertiesConfigurationParser(defaultConfigPath);
            Printer defaultPrinter = configParser.getPrinter();
            Format defaultFormat = configParser.getFormat("default");
            // parse concrete configuration with set defaults
            configParser = new JavaPropertiesConfigurationParser(
                    concreteConfigPath,
                    defaultPrinter,
                    defaultFormat
            );
            Printer printerConfig = configParser.getPrinter();
            for (String property : printerConfig.getPropertyNames()) {
                System.out.println("Property: " + property + "=" + printerConfig.getProperty(property));
            }

            for (String formatName : configParser.getFormatNames()) {
                System.out.println("Format: " + formatName);
                Format formatConfig = configParser.getFormat(formatName);
                for (String property : formatConfig.getPropertyNames()) {
                    System.out.println("Property: " + property + "=" + formatConfig.getProperty(property));
                }
            }
        } catch (ConfigurationValidationException e) {
            System.out.println(e.getMessage());
        } catch (ConfigurationParsingException e) {
            System.out.println(e.getMessage());
        }

    }
*/

}
