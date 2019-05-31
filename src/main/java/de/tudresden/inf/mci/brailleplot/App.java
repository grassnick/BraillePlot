package de.tudresden.inf.mci.brailleplot;

/**
 * Main class.
 * Set up the application and run it.
 * @author Leonard Kupper
 */
public final class App {

    public String getGreeting() {
        return "Hello world.";
    }

    public void dummyConfigurationParsing() {

        String workingDir = System.getProperty("user.dir");
        ConfigurationParser configParser = new JavaPropertiesConfigurationParser();
        configParser.setConfigFile(workingDir + "/dummyPrinterConfig.properties");
        configParser.parse();

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

    public static void main(final String[] args) {
        //System.out.println(new App().getGreeting());
        new App().dummyConfigurationParsing();
    }

}
