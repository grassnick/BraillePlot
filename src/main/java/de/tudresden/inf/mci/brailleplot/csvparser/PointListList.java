package de.tudresden.inf.mci.brailleplot.csvparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.beust.jcommander.IStringConverter;

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
     * May be extended.
     * @return
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

    public final boolean add(final List<Point> points) {
        PointList pl = new PointList(points);
        return add(pl);
    }

    /**
     * May be extended.
     */
    public void updateMinMax() {
        for (PointList checkPl : this) {
            mMaxX = Math.max(getMaxX(), checkPl.getMaxX());
            mMaxY = Math.max(getMaxY(), checkPl.getMaxY());
            mMinX = Math.min(getMinX(), checkPl.getMinX());
            mMinY = Math.min(getMinY(), checkPl.getMinY());
        }
    }

    public final double getMaxX() {
        return mMaxX;
    }

    public final double getMaxY() {
        return mMaxY;
    }

    public final double getMinX() {
        return mMinX;
    }

    public final double getMinY() {
        return mMinY;
    }

    public final boolean hasValidMinMaxValues() {
        return mMaxX > mMinX && mMaxY > mMinY;
    }

    /**
     * Converts a string value to a corresponding PointListList.
     */
    public static class Converter implements IStringConverter<PointListList> {
        @Override
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

        public final boolean insertSorted(final Point p) {
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


        @Deprecated
        public final void add(final int index, final Point element) {
//          throw new UnsupportedOperationException("Only insertions via insertSorted are allowed");
            this.insertSorted(element);
        }

        @Deprecated
        public final boolean add(final Point e) {
//          throw new UnsupportedOperationException("Only insertions via insertSorted are allowed");
            return this.insertSorted(e);
        }

        public final double getMaxX() {
            return mMaxX;
        }

        public final double getMaxY() {
            return mMaxY;
        }

        public final double getMinX() {
            return mMinX;
        }

        public final double getMinY() {
            return mMinY;
        }

        public final String getName() {
            return mName;
        }

        public final void setName(final String name) {
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
        public class Converter implements IStringConverter<PointList> {
            @Override
            public PointList convert(final String value) {
                return new PointList(value.trim());
            }
        }
    }
}
