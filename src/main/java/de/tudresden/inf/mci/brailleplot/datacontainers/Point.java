package de.tudresden.inf.mci.brailleplot.datacontainers;

import de.tudresden.inf.mci.brailleplot.csvparser.Constants;
import org.w3c.dom.Element;


/**
 * A point in a coordinate system specified by an mX and mY coordinate. Can also
 * have a mName and an SVG mSymbol. Provides helper methods, e.g. for calculating
 * the distance between two points.
 *
 * @author Gregor Harlan Idea and supervising by Jens Bornschein
 *         jens.bornschein@tu-dresden.de Copyright by Technische Universität
 *         Dresden / MCI 2014
 *
 */
public class Point implements Comparable<Point> {

    protected double mX;
    protected double mY;
    protected String mName;
    protected Element mSymbol;

    /**
     * Copy constructor.
     *
     * @param otherPoint
     *            the point to copy
     */
    public Point(final Point otherPoint) {
        this.setX(otherPoint.getX());
        this.setY(otherPoint.getY());
        this.setName(otherPoint.getName());

        Element symbol;
        if (otherPoint.getSymbol() != null) {
            symbol = (Element) otherPoint.getSymbol().cloneNode(true);
        } else {
            symbol = null;
        }
        this.setSymbol(symbol);
    }

    /**
     * Represents a two dimensional Point in the plot.
     *
     * @param x
     *            | mX (horizontal) position of the point
     * @param y
     *            | mY (vertical) position of the point
     */
    public Point(final double x, final double y) {
        this(x, y, "", null);
    }

    /**
     * Represents a two dimensional Point in the plot.
     *
     * @param x
     *            | mX (horizontal) position of the point
     * @param y
     *            | mY (vertical) position of the point
     * @param name
     *            | the mName of the point
     */
    public Point(final double x, final double y, final String name) {
        this(x, y, name, null);
    }

    /**
     * Represents a two dimensional Point in the plot.
     *
     * @param x
     *            | mX (horizontal) position of the point
     * @param y
     *            | mY (vertical) position of the point
     * @param symbol
     *            | the mSymbol to use for the point
     */
    public Point(final double x, final double y, final Element symbol) {
        this(x, y, "", symbol);
    }

    /**
     * Represents a two dimensional Point in the plot.
     *
     * @param x
     *            | mX (horizontal) position of the point
     * @param y
     *            | mY (vertical) position of the point
     * @param name
     *            | the mName of the point
     * @param symbol
     *            | the mSymbol to use for the point
     */
    public Point(final double x, final double y, final String name, final Element symbol) {
        this.setX(x);
        this.setY(y);
        this.setName(name);
        this.setSymbol(symbol);
    }

    /**
     * Move the point.
     *
     * @param dx
     *            | movement in mX (horizontal) direction
     * @param dy
     *            | movement in mY (vertical) direction
     */
    public void translate(final double dx, final double dy) {
        setX(getX() + dx);
        setY(getY() + dy);
    }

    /**
     * computes the two dimensional euclidean distance of two points.
     *
     * @param other
     *            | second point
     * @return the two dimensional euclidean distance between this and the other
     *         point
     */
    public double distance(final Point other) {
        return Math.sqrt(Math.pow(other.getX() - getX(), 2) + Math.pow(other.getY() - getY(), 2));
    }

    /**
     * Converts a string value to the corresponding point object.
     *
     */
    public static class Converter {
        /**
         * Convert a formatted string to a point. The format is:
         * {@code [<mX>][,<mY>]} Omitted values will default to 0.
         *
         * @param value
         *            | formatted string
         * @return converted Range
         */
        public Point convert(final String value) {
            String[] s = value.split(",");
             double x;
             double y;

            if (s.length > 0) {
                x = Double.parseDouble(s[0]);
            } else {
                x = 0;
            }

            if (s.length > 1) {
                y = Double.parseDouble(s[1]);
            } else {
                y = 0;
            }
            return new Point(x, y);
        }
    }

    /**
     * Compares with mX priority. Returns -1 if p2 is null.
     *
     * @param p2
     *            | other point
     * @return int
     */
    @Override
    public int compareTo(final Point p2) {
        if (p2 != null) {
            if (Math.abs(p2.getX() - getX()) < Constants.EPSILON) {
                if (getY() < p2.getY()) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                if (getX() < p2.getX()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        return -1;
    }

    /**
     * Compare the mY values of two points. Returns -1 if p2 is null.
     *
     * @param p2
     *            | other point
     * @return int
     */
    public int compareToY(final Point p2) {
        if (p2 != null) {
            if (getY() < p2.getY()) {
                return -1;
            } else {
                return 1;
            }
        }
        return -1;
    }

    /**
     * Compare the mX values of two points. Returns -1 if p2 is null.
     *
     * @param p2
     *            | other point
     * @return int
     */
    public int compareToX(final Point p2) {
        if (p2 != null) {
            if (getX() < p2.getX()) {
                return -1;
            } else {
                return 1;
            }
        }
        return -1;
    }

    /**
     * Getter for mX.
     * @return double mX
     */
    public double getX() {
        return mX;
    }

    /**
     * Setter for mX.
     * @param x double
     */
    public void setX(final double x) {
        this.mX = x;
    }

    /**
     * Getter for mY.
     * @return double mY
     */
    public double getY() {
        return mY;
    }

    /**
     * Setter for mY.
     * @param y double
     */
    public void setY(final double y) {
        this.mY = y;
    }

    /**
     * Getter for mName.
     * @return String mName
     */
    public String getName() {
        return mName;
    }

    /**
     * Setter for mName.
     * @param name String
     */
    public void setName(final String name) {
        this.mName = name;
    }

    /**
     * Getter for mSymbol.
     * @return Element mSymbol
     */
    public Element getSymbol() {
        return mSymbol;
    }

    /**
     * Setter for mSymbol.
     * @param symbol Element
     */
    public void setSymbol(final Element symbol) {
        this.mSymbol = symbol;
    }

}
