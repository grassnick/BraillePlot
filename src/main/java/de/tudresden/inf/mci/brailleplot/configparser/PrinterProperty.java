package de.tudresden.inf.mci.brailleplot.configparser;

/**
 * Representation of a property which is specific to a printer.
 * @author Leonard Kupper
 * @version 2019.06.04
 */
public final class PrinterProperty extends ValidProperty {

    /**
     * Constructor.
     *
     * @param name The mName of the property.
     * @param value The value of the property.
     */
    public PrinterProperty(final String name, final String value) {
        super(name, value);
    }

}
