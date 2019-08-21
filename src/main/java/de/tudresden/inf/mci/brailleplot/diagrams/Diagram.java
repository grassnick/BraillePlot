package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;

import java.util.Objects;

/**
 * General representation of both scatter and line plots with basic data functions. Classes LinePlot and ScatterPlot extend this class.
 * Implements Renderable.
 *
 * @author Richard Schmidt
 */
public abstract class Diagram implements Renderable {
    private PointListContainer<PointList> mData;

    public Diagram(final PointListContainer<PointList> data) {
        Objects.requireNonNull(data);
        mData = data;
    }
    /**
     * Getter for the minimum x-value.
     * @return double minimum x-value
     */
    public double getMinX() {
        return mData.getMinX();
    }

    /**
     * Getter for the maximum x-value.
     * @return double maximum x-value
     */
    public double getMaxX() {
        return mData.getMaxX();
    }

    /**
     * Getter for the minimum y-value.
     * @return double minimum y-value
     */
    public double getMinY() {
        return mData.getMinY();
    }

    /**
     * Getter for the maximum y-value.
     * @return double maximum y-value
     */
    public double getMaxY() {
        return mData.getMaxY();
    }


    /**
     * Getter for the name of a data set by index.
     * @param index int
     * @return name of the data set as a string
     */
    public String getDataSetName(final int index) {
        return mData.toString();
    }
}
