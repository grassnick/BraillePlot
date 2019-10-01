package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;

import java.util.Objects;

/**
 * General representation of a diagram with basic data functions. {@link BarChart}, {@link LinePlot} and {@link ScatterPlot} extend this class.
 * Implements {@link Renderable}.
 * @author Leonard Kupper, Richard Schmidt
 */
public abstract class Diagram implements Renderable {

    PointListContainer<PointList> mData;
    private String mTitle;
    private String mXAxisName;
    private String mYAxisName;
    private String mXAxisUnit;
    private String mYAxisUnit;

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

    public final String getTitle() {
        return mTitle;
    }

    public final void setTitle(final String title) {
        this.mTitle = Objects.requireNonNull(title);
    }

    public final String getXAxisName() {
        return mXAxisName;
    }

    public final void setXAxisName(final String xAxisName) {
        this.mXAxisName = Objects.requireNonNull(xAxisName);
    }

    public final String getYAxisName() {
        return mYAxisName;
    }

    public final void setYAxisName(final String yAxisName) {
        this.mYAxisName = Objects.requireNonNull(yAxisName);
    }

    public final String getXAxisUnit() {
        return mXAxisUnit;
    }

    public final void setXAxisUnit(final String xAxisUnit) {
        this.mXAxisUnit = Objects.requireNonNull(xAxisUnit);
    }

    public final String getYAxisUnit() {
        return mYAxisUnit;
    }

    public final void setYAxisUnit(final String yAxisUnit) {
        this.mYAxisUnit = Objects.requireNonNull(yAxisUnit);
    }

}
