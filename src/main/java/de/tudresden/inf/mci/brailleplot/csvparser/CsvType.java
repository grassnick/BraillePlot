package de.tudresden.inf.mci.brailleplot.csvparser;

import com.beust.jcommander.IStringConverter;

/**
 * Determines what data is represented how by the CSV file. The values are
 * structural properties, whereas the {@link XType} held by every value
 * determines whether the x values are metric or categorial.
 */
public enum CsvType {
    DOTS(XType.METRIC), X_ALIGNED(XType.METRIC), X_ALIGNED_CATEGORIES(XType.CATEGORIAL);

    public final XType mXType;

    CsvType(final XType xType) {
        this.mXType = xType;
    }

    public static CsvType fromString(final String value) {
        switch (value.toLowerCase()) {
        case "x_aligned":
        case "xa":
            return CsvType.X_ALIGNED;
        case "x_aligned_categories":
        case "xac":
            return CsvType.X_ALIGNED_CATEGORIES;
        case "dots":
        case "d":
        default:
            return DOTS;
        }
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    /**
     * Converter class that converts strings to CsvType.
     */
    public static class CsvTypeConverter implements IStringConverter<CsvType> {

        public CsvTypeConverter() {
            super();
        }

        @Override
        public CsvType convert(final String value) {
            CsvType convertedValue = CsvType.fromString(value);
            return convertedValue;
        }

    }
}
