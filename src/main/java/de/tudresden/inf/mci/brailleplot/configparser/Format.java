package de.tudresden.inf.mci.brailleplot.configparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a printing formats properties.
 * @author Leonard Kupper
 * @version 2019.06.04
 */
public final class Format extends Configurable {

    /**
     * Constructor.
     * @param properties A {@link List} of {@link FormatProperty} objects.
     */
    public Format(final List<FormatProperty> properties) {
        mProperties = new ArrayList<>();
        mProperties.addAll(properties);
    }
}
