package de.tudresden.inf.mci.brailleplot;

import java.util.HashSet;
import java.util.List;

/**
 * Configurable.
 * @author Leonard Kupper
 * @version 06.06.19
 */
public abstract class Configurable {

    private Configurable mFallback;
    protected List<ValidProperty> mProperties;

    public final HashSet<String> getPropertyNames() {
        HashSet<String> propertyNames = new HashSet<>();
        for (ValidProperty property : mProperties) {
            propertyNames.add(property.getName());
        }
        return propertyNames;
    }

    public final ValidProperty getProperty(final String propertyName) {
        // look for property
        for (ValidProperty property : mProperties) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        // use fallback if possible
        if (mFallback != null) {
            return mFallback.getProperty(propertyName);
        }
        throw new RuntimeException("Property does not exist: " + propertyName);
    }

    protected final void setFallback(final Configurable fallback) {
        mFallback = fallback;
    }

}
