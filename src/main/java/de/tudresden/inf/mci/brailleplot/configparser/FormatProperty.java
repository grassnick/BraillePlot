package de.tudresden.inf.mci.brailleplot.configparser;

/**
 * Representation of a property which is specific to a printing format.
 * @author Leonard Kupper
 * @version 2019.06.04
 */
public final class FormatProperty extends ValidProperty {
    private String mFormat;

    /**
     * Constructor.
     *
     * @param format The name of the format.
     * @param name The name of the property.
     * @param value The value of the property.
     */
    FormatProperty(final String format, final String name, final String value) {
        super(name, value);
        mFormat = format;
    }

    /**
     * Get the name of the format.
     * @return A {@link String} containing the format name.
     */
    public String getFormat() {
        return mFormat;
    }
}
