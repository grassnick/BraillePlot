package de.tudresden.inf.mci.brailleplot;

import java.util.HashSet;
import java.util.List;

/**
 * Configurable.
 * @author Leonard Kupper
 * @version 04.06.19
 */
public abstract class Configurable {

    protected List<ValidProperty> mProperties;

    public final HashSet<String> getPropertyNames() {
        HashSet<String> propertyNames = new HashSet<String>();
        for (ValidProperty property : mProperties) {
            propertyNames.add(property.getName());
        }
        return propertyNames;
    }

    public final ValidProperty getProperty(final String propertyName) {
        for (ValidProperty property : mProperties) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        throw new RuntimeException("Property does not exist: " + propertyName);
    }

    /*
    protected final String setProperty(final String propertyName, final String propertyValue) {
        return mProperties.put(propertyName, propertyValue);
    }
     */

}
