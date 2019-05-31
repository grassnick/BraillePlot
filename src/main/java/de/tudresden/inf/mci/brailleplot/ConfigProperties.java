package de.tudresden.inf.mci.brailleplot;

import java.util.Set;
import java.util.Map;

/**
 * ConfigProperties.
 */
public abstract class ConfigProperties {

    protected Map<String, String> mPropertyMap;

    public final Set<String> getPropertyNames() {
        return mPropertyMap.keySet();
    }

    public final String getProperty(final String propertyName) {
        return mPropertyMap.get(propertyName);
    }

    protected final String setProperty(final String propertyName, final String propertyValue) {
        return mPropertyMap.put(propertyName, propertyValue);
    }

}
