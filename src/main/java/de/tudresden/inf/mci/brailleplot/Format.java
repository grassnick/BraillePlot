package de.tudresden.inf.mci.brailleplot;

import java.util.ArrayList;
import java.util.List;

/**
 * Format.
 * @author Leonard Kupper
 * @version 04.06.19
 */
public final class Format extends Configurable {

    public Format(final List<FormatProperty> properties) {
        mProperties = new ArrayList<>();
        mProperties.addAll(properties);
    }
}
