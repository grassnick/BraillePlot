package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.csvparser.PointListList;

/**
 * General representation of both scatter and line plots with basic data functions. Classes LinePlot and ScatterPlot extend this class.
 * @author Richard Schmidt
 */
public class Diagram /*implements Renderable*/ {
    public PointListList mP;

    /**
     * Get the minimum x-value.
     * @return
     */
    public double getMinX() {
        return mP.getMinX();
    }

    /**
     * Get the maximum x-value.
     * @return
     */
    public double getMaxX() {
        return mP.getMaxX();
    }

    /**
     * Get the minimum y-value.
     * @return
     */
    public double getMinY() {
        return mP.getMinY();
    }

    /**
     * Get the maximum y-value.
     * @return
     */
    public double getMaxY() {
        return mP.getMaxY();
    }

    /**
     * Get a data set by index.
     * @param index
     * @return
     */
    public PointListList.PointList getDataSet(final int index) {
        return (PointListList.PointList) mP.get(index);
    }

    /**
     * Get the name of a data set by index.
     * @param index
     * @return
     */
    public String getDataSetName(final int index) {
        return mP.get(index).getName();
    }
}
