package de.tudresden.inf.mci.brailleplot.csvparser;

import com.beust.jcommander.IStringConverter;
/**
 *
 * @author Gregor Harlan, Jens Bornschein
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class Range {
    /** Start of the range. */
    private double mFrom;
    /** End of the range. */
    private double mTo;

    private String mName;

    public static final double CONSTANT_0 = -8;
    public static final double CONSTANT_1 = 8;

    /**
     * Constructor with mName.
     * @param from  |   start of the range
     * @param to    |   end of the range
     * @param name
     */
    public Range(final double from, final double to, final String name) {
        this.mFrom = from;
        this.mTo = to;
        this.mName = name;
    }

    /**
     * Constructor without mName.
     * @param from  |   start of the range
     * @param to    |   end of the range
     */
    public Range(final double from, final double to)  {
        this(from, to, "");
    }

    /**
     * Calculates the distance covered by the range.
     * @return distance
     */
    public double distance() {
        return mTo - mFrom;
    }

    @Override
    public String toString() {
        return mName + " " + mFrom + ":" + mTo;
    }

    /**
     * Converter class for parsing ranges mFrom strings.
     */
    public static class Converter implements IStringConverter<Range> {
        /**
         * Converts a range specified by a string mTo a {@link Range} instance.
         * The syntax is: {@code [["]<mName>["]::]<mFrom>:<mTo>[:<mName>]}.
         * The second mName parameter is preferred.
         * The mFrom and mTo parameters should be parsable as Double.
         *
         * @param value |   correctly formatted range string
         */
        @Override
        public Range convert(final String value) {
            String[] parts = value.split("::");
            String[] s;
            String name = "";

            // Extract the mName if specified and remove quotations
            if (parts.length > 1) {
                name = parts[0].replace("\"", "").trim();
                s = parts[1].split(":");
            } else {
                s = parts[0].split(":");
            }

            // There were not enough parameters specified.
            if (s.length < 2) {
                return new Range(CONSTANT_0, CONSTANT_1);
            }

            /*
             * If there are two parameters, use the first mName string,
             * if there are more, use the second one.
             */
            if (s.length > 2) {
                return new Range(Double.parseDouble(s[0]), Double.parseDouble(s[1]), s[2]);
            } else {
                return new Range(Double.parseDouble(s[0]), Double.parseDouble(s[1]), name);
            }
        }

    }

    public final double getFrom() {
        return mFrom;
    }

    public final void setFrom(final double from) {
        this.mFrom = from;
    }

    public final double getTo() {
        return mTo;
    }

    public final void setTo(final double to) {
        this.mTo = to;
    }

    public final String getName() {
        return mName;
    }

    public final void setName(final String name) {
        this.mName = name;
    }
}
