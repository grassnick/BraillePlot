package de.tudresden.inf.mci.brailleplot.csvparser;

import java.text.MessageFormat;

/**
 * Class with methods needed vor SVG.
 */
public final class SvgTools {
    private SvgTools() {
    }

    /**
     * Format a number for svg usage according to the constant DECIMAL_FORMAT.
     *
     * @param value
     * @return
     */
    public static String format2svg(final double value) {
        return Constants.DECIMAL_FORMAT.format(value);
    }

    /**
     * Formats an additional Name of an object. Checks if the mName is set. If
     * mName is set, the mName is packed into brackets and prepend with an
     * whitespace
     *
     * @param name
     *            | optional mName of an object or NULL
     * @return empty string or the mName of the object packed into brackets and
     *         prepend with a whitespace e.g. ' (optional mName)'
     */
    public static String formatName(final String name) {
        if ((name == null || name.isEmpty())) {
            return "";
        } else {
            return " (" + name + ")";
        }
    }

    /**
     * Try to translate a key in the localized version defined in the
     * PropertyResourceBundle file.
     *
     * @param key
     *            | PropertyResourceBundle key
     * @param arguments
     *            | arguments that should fill the placeholder in the returned
     *            PropertyResourceBundle value
     * @return a localized string for the given PropertyResourceBundle key,
     *         filled with the set arguments
     */
    public static String translate(final String key, final Object... arguments) {
        return MessageFormat.format(Constants.BUNDLE.getString(key), arguments);
    }

    /**
     * Try to translate a key in the localized version defined in the
     * PropertyResourceBundle file. This function is optimized for differing
     * sentences depending on the amount of results.
     *
     * @param key
     *            | PropertyResourceBundle key
     * @param arguments
     *            | arguments that should fill the placeholder in the returned
     *            PropertyResourceBundle value. The last argument gives the
     *            count and decide which value will be returned.
     * @return a localized string for the given amount depending
     *         PropertyResourceBundle key, filled with the set arguments
     */
    public static String translateN(final String key, final Object... arguments) {
        int last = (int) arguments[arguments.length - 1];
        String suffix;
        if (last == 0) {
            suffix = "_0";
        } else if (last == 1) {
            suffix = "_1";
        } else {
            suffix = "_n";
        }
        return translate(key + suffix, arguments);
    }

    /**
     * Formats a Point that it is optimized for textual output and packed into
     * the caption with brackets. E.g. E(mX | mY)
     *
     * @param cs
     *            the coordinate system
     * @param point
     *            The point that should be transformed into a textual
     *            representation
     * @param cap
     *            The caption string without brackets
     * @return formated string for the point with '/' as delimiter if now
     *         caption is set, otherwise packed in the caption with brackets and
     *         the '|' as delimiter
     */
    public static String formatForText(final CoordinateSystem cs, final Point point, final String cap) {
        String p = cs.formatX(point.getX()) + " | " + cs.formatY(point.getY());
        String capTrimmed = cap.trim();
        if ((capTrimmed != null && !capTrimmed.isEmpty())) {
            return capTrimmed + "(" + p + ")";
        } else {
            return p;
        }
    }

    /**
     * Try to translate the function index into a continuous literal starting
     * with the char 'f'. If the given index is not valid it returns the mName as
     * a combination of 'f' + the given number.
     *
     * @param f
     *            | the index if the function
     * @return a literal representation to the given function index e.g. 'f',
     *         'g', 'h' or 'f1000'.
     */
    public static String getFunctionName(final int f) {
        if (f < 0 || f > (Constants.FN_LIST.size() - 1)) {
            return "f" + f;
        }
        return Constants.FN_LIST.get(f);
    }

    public static String getPointName(final int p) {
        if (p < 0 || p > (Constants.PN_LIST.size() - 1)) {
            return "P" + p;
        }
        return Constants.PN_LIST.get(p);
    }
}
