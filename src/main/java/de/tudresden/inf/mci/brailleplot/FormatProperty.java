package de.tudresden.inf.mci.brailleplot;

/**
 * FormatProperty.
 * @author Leonard Kupper
 * @version 04.06.19
 */
public final class FormatProperty extends ValidProperty {
    private String mFormat;

    public FormatProperty(final String format, final String name, final String value) {
        mFormat = format;
        mName = name;
        mValue = value;
    }

    public String getFormat() {
        return mFormat;
    }
}
