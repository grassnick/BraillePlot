package de.tudresden.inf.mci.brailleplot;

import org.apache.commons.cli.*;

import java.io.IOException;


/**
 * Main class.
 * Set up the application and run it.
 */
public final class App {

    public String getGreeting() {
        return "Hello world.";
    }

    public static void parseCLI(final String[] args) throws ParseException {

        // Creating Options object
        Options options = new Options();
        options.addOption("c", "csv", true, "Path to CSV")
                .addOption("s", "semMap", true, "Literal for semantic mapping")
                .addOption("p", "printer", true, "Printerconfig");
        HelpFormatter formatter = new HelpFormatter();
        String headerForOptions = "Convert csv into braille";
        String footerForOptions = "Report Issues to Leonard Kupper";
        formatter.printHelp("braillegraphics", headerForOptions, options, footerForOptions, true);

        //Create a parser
        CommandLineParser parser = new DefaultParser();

        //parse the options
        CommandLine cmd = parser.parse(options, args);
        System.out.println(cmd.getOptionValue("c"));

    }

    public static void main(final String[] args) throws IOException {
        System.out.println(new App().getGreeting());
        try {
            parseCLI(args);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
