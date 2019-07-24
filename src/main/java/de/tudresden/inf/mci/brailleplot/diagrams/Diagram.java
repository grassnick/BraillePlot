package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.csvparser.PointListList;

/**
 * General representation of both scatter and line plots.
 * @author Richard Schmidt
 */
public class Diagram {
    public PointListList p;

    /**
     * Get the minimum x-value.
     * @return
     */
    public double getMinX() {
        return p.getMinX();
    }

    /**
     * Get the maximum x-value.
     * @return
     */
    public double getMaxX() {
        return p.getMaxX();
    }

    /**
     * Get the minimum y-value.
     * @return
     */
    public double getMinY() {
        return p.getMinY();
    }

    /**
     * Get the maximum y-value.
     * @return
     */
    public double getMaxY() {
        return p.getMaxY();
    }

    /**
     * Get a data set by index.
     * @param index
     * @return
     */
    public PointListList.PointList getDataSet(final int index) {
        return (PointListList.PointList) p.get(index);
    }

    /**
     * Get the name of a data set by index.
     * @param index
     * @return
     */
    public String getDataSetName(final int index) {
        return p.get(index).getName();
    }
}
