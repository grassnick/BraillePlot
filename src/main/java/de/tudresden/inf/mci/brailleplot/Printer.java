package de.tudresden.inf.mci.brailleplot;

import java.util.ArrayList;
import java.util.List;

/**
 * Printer.
 * @author Leonard Kupper
 * @version 04.06.19
 */
public final class Printer extends Configurable {

    public Printer(final List<PrinterProperty> properties) {
        mProperties = new ArrayList<>();
        mProperties.addAll(properties);
    }
}
