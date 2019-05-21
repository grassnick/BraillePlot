package de.tudresden.inf.mci.brailleplot;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;


public class App {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void parseCLI(){
        Options options = new Options();
        options.addOption("c", "csv", true, "Path to CSV")
                .addOption("s", "semMap", true, "Literal for semantic mapping")
                .addOption("p", "printer", true, "Printerconfig");
        HelpFormatter formatter = new HelpFormatter();
        String headerForOptions = "Convert csv into braille";
        String footerForOptions = "Report Issues to Leonard Kupper";
        formatter.printHelp("braillegraphics",headerForOptions,options,footerForOptions,true);
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
        parseCLI();

    }
}
