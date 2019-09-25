package de.tudresden.inf.mci.brailleplot.commandline;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Performs command line parsing and creates a {@link Settings} object.
 * @author Georg Graßnick
 * @version 2019.05.31
 */
public class CommandLineParser {

    private Options mOptions;

    public CommandLineParser() {
        setupOptions();
    }

    private void setupOptions() {
        mOptions = new Options();
        mOptions.addOption("h", SettingType.DISPLAY_HELP.toString(), false, "Print help and exit")
                .addOption("c", SettingType.CSV_LOCATION.toString(), true, "Path to CSV")
                .addOption("s", SettingType.SEMANTIC_MAPPING.toString(), true, "Literal for semantic mapping")
                .addOption("p", SettingType.PRINTER_CONFIG_PATH.toString(), true, "Path to printer configuration file")
                .addOption("t", SettingType.TITLE.toString(), true, "Title of the diagram")
                .addOption("xn", SettingType.X_NAME.toString(), true, "Name of the x-axis")
                .addOption("xu", SettingType.X_UNIT.toString(), true, "Unit of the x-axis")
                .addOption("yn", SettingType.Y_NAME.toString(), true, "Name of the y-axis")
                .addOption("yu", SettingType.Y_UNIT.toString(), true, "Unit of the y-axis");

    }

    /**
     * Parse command line parameters.
     * @param args The arguments from the commandline.
     * @return A {@link Settings} object that represents the values from the command line parameters.
     * @throws ParsingException On any underlying error.
     */
    public final Settings parse(final String[] args) throws ParsingException {

        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine;
        try {
            cmdLine = parser.parse(mOptions, args);
        } catch (ParseException pe) {
            throw new ParsingException("Could not parse command line", pe);
        }
        return new Settings(cmdLine);
    }

    /**
     * Print usage information to the command line.
     */
    public final void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        String headerForOptions = "Convert csv into braille";
        String footerForOptions = "Report Issues to Leonard Kupper";

        formatter.printHelp("braillegraphics", headerForOptions, mOptions, footerForOptions, true);
    }
}
