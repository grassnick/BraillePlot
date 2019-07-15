package de.tudresden.inf.mci.brailleplot.exporter;

/**
 * This enum provides means to differentiate between printers.
 * This should be extended if new printers are supported. If its unknown, the standard should be NormalPrinter. This
 * assumes that no special features are supported and hence, normal braille should be printed.
 * Because not every printer supports the same functionality or uses the same Protocol for the same functionality, the
 * Softwaredeveloper must
 * Currently used in PrintDirector.
 * @author Andrey Ruzhanskiy
 */

public enum PrinterConfiguration {
    NORMALPRINTER, INDEX_EVEREST_D_V4_FLOATINGDOT_PRINTER, INDEX_EVEREST_D_V4_GRAPHIC_PRINTER
}
