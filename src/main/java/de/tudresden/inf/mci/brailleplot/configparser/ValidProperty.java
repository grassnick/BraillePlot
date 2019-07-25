package de.tudresden.inf.mci.brailleplot.configparser;

/**
 * Representation of a property consisting of a legal property mName and value.
 * @author Leonard Kupper
 * @version 2019.06.04
 */
public abstract class ValidProperty {
    String mName;
    String mValue;

    ValidProperty(final String name, final String value) {
        if ((name == null) || (value == null)) {
            throw new NullPointerException("Name and value of ValidProperty cannot be null.");
        }
        mName = name;
        mValue = value;
    }

    /**
     * Get the mName of the property.
     * @return A {@link String} containing the property mName.
     */
    public final String getName() {
        return mName;
    }

    /**
     * Get the value of the property as {@link String}.
     * @return The property value.
     */
    public final String toString() {
        return mValue;
    }

    /**
     * Get the value of the property as {@link Integer}.
     * If the actual value cannot be converted to an Integer, a {@link NumberFormatException} will occur.
     * @return The property value.
     */
    public final Integer toInt() {
        return Integer.valueOf(mValue);
    }

    /**
     * Get the value of the property as {@link Double}.
     * If the actual value cannot be converted to a Double, a {@link NumberFormatException} will occur.
     * @return The property value.
     */
    public final Double toDouble() {
        return Double.valueOf(mValue);
    }

    /**
     * Get the value of the property as {@link Boolean}.
     * @return The property value.
     */
    public final Boolean toBool() {
        return Boolean.valueOf(mValue);
    }
}
