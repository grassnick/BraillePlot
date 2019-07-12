package de.tudresden.inf.mci.brailleplot.commandline;

/**
 * Represents all possible parsed options parsed from the command line.
 * @author Georg Graßnick
 * @version 2019.05.31
 */
public enum SettingType {

    DISPLAY_HELP("help"),
    CSV_LOCATION("csv-path"),
    PRINTER_CONFIG_PATH("printer-config-path"),
    SEMANTIC_MAPPING("semantic-mapping");
    private final String mName;

    SettingType(final String name) {
        this.mName = name;
    }

    static SettingType fromString(final String s) {
        switch (s) {
            case "help":
                return DISPLAY_HELP;
            case "csv-path":
                return  CSV_LOCATION;
            case "printer-config-path":
                return PRINTER_CONFIG_PATH;
            case "semantic-mapping":
                return SEMANTIC_MAPPING;
            default:
                throw new IllegalArgumentException("Setting not available");
        }
    }

    public String toString() {
        return mName;
    }
}
