package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;

import java.util.Objects;

/**
 * General representation of a diagram with basic data functions. {@link BarChart}, {@link LinePlot} and {@link ScatterPlot} extend this class.
 * Implements {@link Renderable}.
 *
 * @author Richard Schmidt
 */
public abstract class Diagram implements Renderable {
    PointListContainer<PointList> mData;

    Diagram(final PointListContainer<PointList> data) {
        Objects.requireNonNull(data);
        mData = data;
    }
    /**
     * Getter for the minimum x-value.
     * @return double minimum x-value.
     */
    public double getMinX() {
        return mData.getMinX();
    }

    /**
     * Getter for the maximum x-value.
     * @return double maximum x-value.
     */
    public double getMaxX() {
        return mData.getMaxX();
    }

    /**
     * Getter for the minimum y-value.
     * @return double minimum y-value.
     */
    public double getMinY() {
        return mData.getMinY();
    }

    /**
     * Getter for the maximum y-value.
     * @return double maximum y-value.
     */
    public double getMaxY() {
        return mData.getMaxY();
    }

    /**
     * Getter for a list with x-y-Pairs: x is the index (always just counts from 0 up), y is the value.
     * @return PointList with the corresponding data set.
     */
    public PointListContainer<PointList> getDataSet() {
        return mData;
    }

}
