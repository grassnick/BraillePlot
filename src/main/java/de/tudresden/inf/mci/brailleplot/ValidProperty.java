package de.tudresden.inf.mci.brailleplot;

/**
 * ValidProperty.
 * @author Leonard Kupper
 * @version 04.06.19
 */
public abstract class ValidProperty {
    protected String mName;
    protected String mValue;

    public final String getName() {
        return mName;
    }

    public final String toString() {
        return mValue;
    }

    public final Integer toInt() { return Integer.valueOf(mValue); }

    public final Float toFloat() { return Float.valueOf(mValue); }

    public final Boolean toBool() { return Boolean.valueOf(mValue); }
}
