package de.tudresden.inf.mci.brailleplot.commandline;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;
import java.util.Objects;


/**
 * Performs command line parsing and creates a {@link Settings} object.
 * @author Georg Graßnick, Andrey Ruzhanskiy, Leonard Kupper
 * @version 2019.10.03
 */
public class CommandLineParser {

    private Options mOptions;

    public static Option helpOption = Option.builder("h")
            .longOpt("help")
            .required(false)
            .hasArg(false)
            .desc("Print help and exit")
            .build();

    public CommandLineParser() {
        setupOptions();
    }

    private void setupOptions() {
        mOptions = new Options();
        mOptions.addOption(helpOption)
                .addRequiredOption("c", SettingType.CSV_LOCATION.toString(), true, "Path to CSV")
                .addRequiredOption("p", SettingType.PRINTER_CONFIG_PATH.toString(), true, "Path to printer configuration file")
                .addRequiredOption("t", SettingType.DIAGRAM_TITLE.toString(), true, "Title of the diagram")
                .addRequiredOption("d", SettingType.DIAGRAM_TYPE.toString(), true, "Type of the diagram. Possible Values: [ScatterPlot, LineChart, BarChart]")
                .addOption("x", SettingType.X_AXIS_LABEL.toString(), true, "Label of X-axis including unit")
                .addOption("y", SettingType.Y_AXIS_LABEL.toString(), true, "Label of Y-axis including unit")
                .addOption("f", SettingType.FORMAT.toString(), true, "Name of predefined format from configuration (A4 portrait if not specified)")
                .addOption("v", SettingType.VERTICAL_CSV.toString(), false, "Parse CSV in vertical instead of horizontal orientation (Only applicable for BarChart)")
                .addOption("i", SettingType.INHIBIT_PRINT.toString(), false, "Inhibit the printing process")
                .addOption("s", SettingType.SVG_EXPORT.toString(), true, "Base file path for export of svg file(s) (Omit '.svg' suffix)")
                .addOption("b", SettingType.BYTE_DUMP.toString(), true, "Base file path for print data byte dump file(s) (Omit '.bin' suffix)")
                .addOption("l", SettingType.LOG_LEVEL.toString(), true, "Logging output level. Possible Values: [All, Trace, Debug, Info, Warn, Error, Off] Defaults to 'Info'");
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

    public static boolean checkForHelp(final String[] args)  {

        boolean hasHelp = false;
        Options options = new Options();
        options.addOption(helpOption);
        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            // Will occur if any other option than "help" is encountered
            // For this case we can safely ignore it.
        }
        if ((Objects.nonNull(cmd) && cmd.hasOption(helpOption.getOpt()))
                || (args.length == 0)
                || (Arrays.asList(args).contains("-" + helpOption.getOpt())
                || (Arrays.asList(args).contains("--" + helpOption.getLongOpt())))) {
            hasHelp = true;
        }
        return hasHelp;
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
