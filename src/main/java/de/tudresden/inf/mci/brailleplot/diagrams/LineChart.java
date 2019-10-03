package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;

import java.util.Objects;

/**
 * Representation of a line chart with basic data functions. Implements Renderable.
 * @author Andrey Ruzhanskiy
 * @version 2019.08.17
 */
public class LineChart extends Diagram {

    private PointListContainer<PointList> mData;

    /**
     * Constructor for a line chart.
     * @param data The container, which holds the information about the datapoints.
     */
    public LineChart(final PointListContainer<PointList> data) {
        super(data);
        Objects.requireNonNull(data);
        mData = data;
    }

    /**
     * Getter for the underlying Pointlistcontainer.
     * @return Pointlistcontainer.
     */
    public PointListContainer<PointList> getData() {
        return mData;
    }

    /**
     * Getter for the minimum y-value.
     * @return minimum y-value.
     */
    public double getMinY() {
        return mData.getMinY();
    }

    /**
     * Getter for the maximum y-value.
     * @return maximum y-value.
     */
    public double getMaxY() {
        return mData.getMaxY();
    }

    /**
     * Getter for the minimum x-value.
     * @return minimum x-value.
     */
    public double getMinX() {
        return mData.getMinX();
    }

    /**
     * Getter for the maximum x-value.
     * @return maximum x-value.
     */
    public double getMaxX() {
        return mData.getMaxX();
    }

}
