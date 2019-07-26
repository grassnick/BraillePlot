package de.tudresden.inf.mci.brailleplot.configparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a printers configuration properties.
 * @author Leonard Kupper
 * @version 2019.07.18
 */
public final class Printer extends Configurable {

    /**
     * Constructor.
     *
     * @param properties A {@link List} of {@link PrinterProperty} objects.
     */
    public Printer(final List<PrinterProperty> properties) {
        mProperties = new ArrayList<>();
        mProperties.addAll(properties);
    }

    @Override
    public String toString() {
        return "printer configuration";
    }
}
