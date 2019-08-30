package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;

import java.util.Objects;

/**
 * General representation of both scatter and line plots with basic data functions. Classes LinePlot and ScatterPlot extend this class.
 * Implements Renderable.
 *
 * @author Richard Schmidt, Georg Graßnick
 * @version 2019.08.26
 */
public abstract class Diagram implements Renderable {
    private final PointListContainer<PointList> mContainer;

    protected Diagram(final PointListContainer<PointList> container) {
        Objects.requireNonNull(container);
        mContainer = container;
    }
    /**
     * Getter for the underlying data structure.
     * @return The {@link PointListContainer}{@literal <}{@link PointList}{@literal >} holding the data.
     */
    public PointListContainer<PointList> getDataSet() {
        return  mContainer;
    }

}
