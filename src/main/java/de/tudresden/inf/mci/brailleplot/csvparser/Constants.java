package de.tudresden.inf.mci.brailleplot.csvparser;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Class for constants representation.
 * @author SVGPlott-Team
 * @version 2019.07.29
 */
public final class Constants {
    public static final Locale LOCALE = new Locale("de");
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(LOCALE);

    private Constants() {
    }

    /**
     * Getter for SVG decimal format.
     * @return DecimalFormat
     */
    private static DecimalFormat getSvgDecimalFormat() {
        DecimalFormat decimalFormat = new DecimalFormat("0.###");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(dfs);
        return decimalFormat;
    }
}
