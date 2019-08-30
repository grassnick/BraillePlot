package de.tudresden.inf.mci.brailleplot.configparser;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents an entity with a set of valid properties, that can optionally be inherited and extended from a given
 * default fallback Configurable.
 * @author Leonard Kupper
 * @version 2019.06.26
 */
abstract class Configurable {

    private Configurable mFallback;
    /**
     * A list of the properties specific to this entity.
     */
    List<ValidProperty> mProperties;

    Configurable() {

    };

    /**
     * Get the names of all available properties.
     * @return A {@link HashSet}&lt;{@link java.lang.String}&gt; containing the property names.
     */
    public final HashSet<String> getPropertyNames() {
        HashSet<String> propertyNames = new HashSet<>();
        for (ValidProperty property : mProperties) {
            propertyNames.add(property.getName());
        }
        if (mFallback != null) {
            HashSet<String> fallbackPropertyNames = mFallback.getPropertyNames();
            propertyNames.addAll(fallbackPropertyNames);
        }
        return propertyNames;
    }

    /**
     * Get the property for the given property name.
     * @param propertyName The name of the property.
     * @return A {@link ValidProperty} object that represents the property.
     * @throws NoSuchElementException If no property has the specified name.
     */
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
        throw new NoSuchElementException("Property does not exist: " + propertyName);
    }

    /**
     * Set the default/fallback {@link Configurable} that will be used if a property cannot be found.
     * @param fallback A {@link Configurable} object.
     */
    protected final void setFallback(final Configurable fallback) {
        mFallback = fallback;
    }

    public final void override(final ValidProperty overridingProperty) {
        String propertyName = overridingProperty.getName();
        for (int i = 0; i < mProperties.size(); i++) {
            if (mProperties.get(i).getName().equals(propertyName)) {
                mProperties.add(i, overridingProperty);
                mProperties.remove(i + 1); // the old property
            }
        }
    }

}
