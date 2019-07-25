package de.tudresden.inf.mci.brailleplot.configparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representation of a printing formats properties.
 * @author Leonard Kupper
 * @version 2019.07.18
 */
public final class Format extends Configurable {

    private String mFormatName = "";

    /**
     * Constructor.
     * @param properties A {@link List} of {@link FormatProperty} objects.
     */
    public Format(final List<FormatProperty> properties) {
        mProperties = new ArrayList<>();
        mProperties.addAll(properties);
    }

    /**
     * Constructor.
     * @param properties A {@link List} of {@link FormatProperty} objects.
     * @param formatName The mName of the format. (e.g. 'A4')
     */
    public Format(final List<FormatProperty> properties, final String formatName) {
        mFormatName = Objects.requireNonNull(formatName);
        mProperties = new ArrayList<>();
        mProperties.addAll(properties);
    }

    @Override
    public String toString() {
        return "format configuration (" + mFormatName + ")";
    }
}
