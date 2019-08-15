package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointListList;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;

/**
 * General representation of both scatter and line plots with basic data functions. Classes LinePlot and ScatterPlot extend this class.
 * Implements Renderable.
 *
 * @author Richard Schmidt
 */
public class Diagram implements Renderable {
    public PointListList mP;

    /**
     * Getter for the minimum x-value.
     * @return double minimum x-value
     */
    public double getMinX() {
        return mP.getMinX();
    }

    /**
     * Getter for the maximum x-value.
     * @return double maximum x-value
     */
    public double getMaxX() {
        return mP.getMaxX();
    }

    /**
     * Getter for the minimum y-value.
     * @return double minimum y-value
     */
    public double getMinY() {
        return mP.getMinY();
    }

    /**
     * Getter for the maximum y-value.
     * @return double maximum y-value
     */
    public double getMaxY() {
        return mP.getMaxY();
    }

    /**
     * Getter for a data set by index.
     * @param index int
     * @return PointList with the corresponding data set
     */
    public PointListList.PointList getDataSet(final int index) {
        return (PointListList.PointList) mP.get(index);
    }

    /**
     * Getter for the name of a data set by index.
     * @param index int
     * @return name of the data set as a string
     */
    public String getDataSetName(final int index) {
        return mP.get(index).getName();
    }
}
