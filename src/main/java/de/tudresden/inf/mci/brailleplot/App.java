package de.tudresden.inf.mci.brailleplot;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Main class.
 * Set up the application and run it.
 * @author Georg Graßnick
 * @version 06.06.19
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

    private ConcurrentLinkedDeque<Runnable> mFinalizers;

    private App() {
        sInstance = this;
        mFinalizers = new ConcurrentLinkedDeque<>();
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
        getInstance().mFinalizers.add(r);
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
    public static void terminateWithException(final Exception e) {
        terminateWithException(e, "");
    }

    /**
     * Terminate the complete Application in case of an untreatable error.
     * @param e The Exception that led to the error.
     * @param message An additional message to print to stderr.
     */
    public static void terminateWithException(final Exception e, final String message) {
        if (!message.isEmpty()) {
            System.err.println(message);
        }
        e.printStackTrace();
        getInstance().runFinalizers();
        System.exit(EXIT_ERROR);
    }

    /**
     * Main loop of the application.
     * @param args Command line parameters.
     * @return 0 if Application exited successfully, 1 on error.
     */
    int run(final String[] args) {

        registerFinalizer(() -> {
            System.out.println("Application terminated.");
        });

        try {
            // Parse command line parameters


            // If requested, print help and exit

            // Parse configuration
            dummyConfigurationParsing();

            // Parse csv data

            // ...

        } catch (final Exception e) {
            terminateWithException(e);
        } finally {
            runFinalizers();
        }

        return EXIT_SUCCESS;
    }

    /**
     * Dummy method for parser execution.
     * Will be removed soon.
     */
    public static void dummyConfigurationParsing() {

        JavaPropertiesConfigurationValidator configValidator = new JavaPropertiesConfigurationValidator();
        String workingDir = System.getProperty("user.dir");
        String defaultConfigPath = workingDir + "/defaultConfig.properties";
        String concreteConfigPath = workingDir + "/dummyPrinterConfig.properties";

        // create parser and parse default config
        ConfigurationParser configParser = new JavaPropertiesConfigurationParser(defaultConfigPath, configValidator);
        Printer defaultPrinter = configParser.getPrinter();
        Format defaultFormat = configParser.getFormat("default");
        // parse concrete configuration with set defaults
        configParser = new JavaPropertiesConfigurationParser(
                concreteConfigPath,
                configValidator,
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

    }

}
