package de.tudresden.inf.mci.brailleplot.csvparser;

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
    public static class CsvOrientationConverter {

        public CsvOrientationConverter() {
            super();
        }

        /**
         * Converts a String value into the corresponding CsvOrientation.
         * @param value String
         * @return CsvOrientation
         */
        public CsvOrientation convert(final String value) {
            CsvOrientation convertedValue = CsvOrientation.fromString(value);
            return convertedValue;
        }

    }
}
