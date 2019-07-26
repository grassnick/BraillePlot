package de.tudresden.inf.mci.brailleplot.csvparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Gregor Harlan, Jens Bornschein Idea and supervising by Jens
 *         Bornschein jens.bornschein@tu-dresden.de Copyright by Technische
 *         Universität Dresden / MCI 2014
 *
 */
public class PointListList extends ArrayList<PointListList.PointList> {

    private static final long serialVersionUID = 6902232865786868851L;
    protected Double mMaxX = Double.NEGATIVE_INFINITY;
    protected Double mMaxY = Double.NEGATIVE_INFINITY;
    protected Double mMinX = Double.POSITIVE_INFINITY;
    protected Double mMinY = Double.POSITIVE_INFINITY;

    /**
     * Getter for XType.
     *
     * @return XType
     */
    public XType getXType() {
        return XType.METRIC;
    }

    public PointListList() {
        this("");
    }

    public PointListList(final String pointLists) {

        if (pointLists == null || pointLists.isEmpty()) {
            return;
        }

        // TODO: load from file

        // pointLists = pointLists.replaceAll("[^\\d.,^\\s+,^\\{^\\}^-]", "");
        String[] lists = pointLists.split("\\}");
        for (String l : lists) {
            PointList pl = new PointList(l);
            if (!pl.isEmpty()) {
                this.add(pl);
            }
        }
    }

    @Override
    public boolean add(final PointList pl) {
        boolean success = super.add(pl);
        updateMinMax();
        return success;
    }

    /**
     * Adds a list of points to the PointListList.
     *
     * @param points List(Point)
     * @return true if success, false if not
     */
    public boolean add(final List<Point> points) {
        PointList pl = new PointList(points);
        return add(pl);
    }

    /**
     * Updates mMaxX, mMaxY, mMinX, mMinY.
     */
    public void updateMinMax() {
        for (PointList checkPl : this) {
            mMaxX = Math.max(getMaxX(), checkPl.getMaxX());
            mMaxY = Math.max(getMaxY(), checkPl.getMaxY());
            mMinX = Math.min(getMinX(), checkPl.getMinX());
            mMinY = Math.min(getMinY(), checkPl.getMinY());
        }
    }

    /**
     * Getter for mMaxX.
     * @return double mMaxX
     */
    public double getMaxX() {
        return mMaxX;
    }

    /**
     * Getter for mMaxY.
     * @return double mMaxY
     */
    public double getMaxY() {
        return mMaxY;
    }

    /**
     * Getter for mMinX.
     * @return double mMinX
     */
    public double getMinX() {
        return mMinX;
    }

    /**
     * Getter for mMinY.
     * @return double mMinY
     */
    public double getMinY() {
        return mMinY;
    }

    /**
     * Checks if min- and max-values are valid.
     * @return true if yes, false if no
     */
    public boolean hasValidMinMaxValues() {
        return mMaxX > mMinX && mMaxY > mMinY;
    }

    /**
     * Converts a string value to a corresponding PointListList.
     */
    public static class Converter {

        /**
         * Converts a String value into the corresponding PointListList.
         * @param value String
         * @return PointListList
         */
        public PointListList convert(final String value) {
            return new PointListList(value);
        }
    }

    /**
     * List of Points including max values.
     *
     * @author Jens Bornschein
     *
     */
    public static class PointList extends ArrayList<Point> {

        private static final long serialVersionUID = -2318768874799315111L;
        private Double mMaxX = Double.NEGATIVE_INFINITY;
        private Double mMaxY = Double.NEGATIVE_INFINITY;
        private Double mMinX = Double.POSITIVE_INFINITY;
        private Double mMinY = Double.POSITIVE_INFINITY;
        private String mName = "";

        public PointList(final List<Point> points) {
            if (points != null && !points.isEmpty()) {
                for (Point p : points) {
                    this.insertSorted(p);
                }
            }
        }

        public PointList(final String points) {
            if (points == null || points.isEmpty()) {
                return;
            }

            String[] pl = points.split("::");

            if (pl != null && pl.length > 0) {

                String pts;
                if (pl.length > 1) {
                    setName(pl[0].trim());
                    pts = pl[1].replaceAll("[^\\d.,^\\s+,^-]", "");
                } else {
                    pts = pl[0].replaceAll("[^\\d.,^\\s+,^-]", "");
                }
                String[] s = pts.split("\\s+");

                for (String string : s) {
                    if (string != null && !string.isEmpty()) {
                        Point p = (new Point.Converter()).convert(string);
                        this.insertSorted(p);
                    }
                }
            }
        }

        public PointList() {
            this("");
        }

        /**
         * Sorted insertion of a Point into the PointList.
         *
         * @param p Point
         * @return true if success, false if not
         */
        public boolean insertSorted(final Point p) {
            mMaxX = Math.max(getMaxX(), p.getX());
            mMaxY = Math.max(getMaxY(), p.getY());
            mMinX = Math.min(getMinX(), p.getX());
            mMinY = Math.min(getMinY(), p.getY());
            boolean returnVal = super.add(p);

            Comparable<Point> cmp = (Comparable<Point>) p;
            for (int i = size() - 1; i > 0 && cmp.compareTo(get(i - 1)) < 0; i--) {
                Collections.swap(this, i, i - 1);
            }
            return returnVal;
        }

        /**
         * Wrapper for insertSorted.
         *
         * @param index int
         * @param element Point
         */
        @Deprecated
        public void add(final int index, final Point element) {
//          throw new UnsupportedOperationException("Only insertions via insertSorted are allowed");
            this.insertSorted(element);
        }

        /**
         * Wrapper for insertSorted.
         *
         * @param e Point
         * @return true if success, false if not
         */
        @Deprecated
        public boolean add(final Point e) {
//          throw new UnsupportedOperationException("Only insertions via insertSorted are allowed");
            return this.insertSorted(e);
        }

        /**
         * Getter for mMaxX.
         * @return double mMaxX
         */
        public double getMaxX() {
            return mMaxX;
        }

        /**
         * Getter for mMaxY.
         * @return double mMaxY
         */
        public double getMaxY() {
            return mMaxY;
        }

        /**
         * Getter for mMinX.
         * @return double mMinX
         */
        public double getMinX() {
            return mMinX;
        }

        /**
         * Getter for mMinY.
         * @return double mMinY
         */
        public double getMinY() {
            return mMinY;
        }

        /**
         * Getter for mName.
         * @return double mName
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
         * Gets the first maximum of the data set.
         * TODO implement multiple maxima with a proper string representation
         * @return first maximum point
         */
        public Point getFirstMaximum() {
            if (this.isEmpty()) {
                return null;
            }

            Point maxPoint = get(0);

            for (Point p : this) {
                if (maxPoint.getY() < p.getY()) {
                    maxPoint = p;
                }
            }

            return maxPoint;
        }

        /**
         * Gets the first minimum of the data set.
         * TODO implement multiple minimum with a proper string representation
         * @return first minimum point
         */
        public Point getFirstMinimum() {
            if (this.isEmpty()) {
                return null;
            }

            Point minPoint = get(0);

            for (Point p : this) {
                if (minPoint.getY() > p.getY()) {
                    minPoint = p;
                }
            }

            return minPoint;
        }

        /**
         * Converts a string value to the corresponding PointList.
         */
        public class Converter {

            /**
             * Converts a String value into the corresponding PointList.
             * @param value String
             * @return PointList
             */
            public PointList convert(final String value) {
                return new PointList(value.trim());
            }
        }
    }
}
