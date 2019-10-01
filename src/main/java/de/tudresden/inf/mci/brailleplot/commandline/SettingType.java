package de.tudresden.inf.mci.brailleplot.commandline;

/**
 * Represents all possible parsed options parsed from the command line.
 * @author Georg Graßnick, Andrey Ruzhanskiy, Leonard Kupper
 * @version 2019.10.01
 */
public enum SettingType {

    DISPLAY_HELP("help"),
    CSV_LOCATION("csv-path"),
    PRINTER_CONFIG_PATH("printer-config-path"),
    SEMANTIC_MAPPING("semantic-mapping"),
    DIAGRAM_TITLE("title"),
    X_AXIS_LABEL("xLabel"),
    Y_AXIS_LABEL("yLabel"),
    DIAGRAM_TYPE("diagram-type"),
    FORMAT("format"),
    VERTICAL_CSV("vertical-csv"),
    INHIBIT_PRINT("inhibit-print"),
    SVG_EXPORT("svg-export"),
    BYTE_DUMP("byte-dump");


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
                return DIAGRAM_TITLE;
            case "xLabel":
                return X_AXIS_LABEL;
            case "yLabel":
                return Y_AXIS_LABEL;
            case "diagram-type":
                return DIAGRAM_TYPE;
            case "format":
                return FORMAT;
            case "vertical-csv":
                return VERTICAL_CSV;
            case "inhibit-print":
                return INHIBIT_PRINT;
            case "svg-export":
                return SVG_EXPORT;
            default:
                throw new IllegalArgumentException("Setting not available");
        }
    }

    public String toString() {
        return mName;
    }
}
