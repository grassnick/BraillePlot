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

    public void dummyConfigurationReader() {
        String workingDir = System.getProperty("user.dir");

        ConfigurationReader printerConfig = new ConfigurationReader(
                workingDir + "/dummyPrinterConfig.properties"
        );
    }

    public static void main(final String[] args) {
        //System.out.println(new App().getGreeting());
        new App().dummyConfigurationReader();
    }

}
