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
    SEMANTIC_MAPPING("semantic-mapping"),
    TITLE("title"),
    X_NAME("x-axis-name"),
    X_UNIT("x-axis-unit"),
    Y_NAME("y-axis-name"),
    Y_UNIT("y-axis-unit");


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
            case "title":
                return TITLE;
            case "x-axis-name":
                return X_NAME;
            case "x-axis-unit":
                return X_UNIT;
            case "y-axis-name":
                return Y_NAME;
            case "y-axis-unit":
                return Y_UNIT;
            default:
                throw new IllegalArgumentException("Setting not available");
        }
    }

    public String toString() {
        return mName;
    }
}
