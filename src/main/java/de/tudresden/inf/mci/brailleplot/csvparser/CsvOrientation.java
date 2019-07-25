package de.tudresden.inf.mci.brailleplot.csvparser;

import com.beust.jcommander.IStringConverter;

/**
 * Enumeration of the two possible CSV orientations.
 */
public enum CsvOrientation {

    HORIZONTAL, VERTICAL;

    public static CsvOrientation fromString(final String code) {
        if (code.equals("vertical") || code.equals("v")) {
            return CsvOrientation.VERTICAL;
        } else {
            return CsvOrientation.HORIZONTAL;
        }
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    /**
     * Converter class that converts strings to CsvOrientation.
     */
    public static class CsvOrientationConverter implements IStringConverter<CsvOrientation> {

        public CsvOrientationConverter() {
            super();
        }

        @Override
        public CsvOrientation convert(final String value) {
            CsvOrientation convertedValue = CsvOrientation.fromString(value);
            return convertedValue;
        }

    }
}
