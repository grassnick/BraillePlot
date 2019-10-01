package de.tudresden.inf.mci.brailleplot.configparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties that describe parameters for the representation of charts.
 * @author Leonard Kupper
 * @version 2019.09.25
 */
public final class Representation extends Configurable {

    /**
     * Constructor.
     *
     * @param properties A {@link List} of {@link RepresentationProperty} objects.
     */
    public Representation(final List<RepresentationProperty> properties) {
        mProperties = new ArrayList<>();
        mProperties.addAll(properties);
    }

    @Override
    public String toString() {
        return "representation configuration";
    }
}
