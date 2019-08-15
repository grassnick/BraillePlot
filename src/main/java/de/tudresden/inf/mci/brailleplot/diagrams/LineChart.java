package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;

import java.util.Objects;

public class LineChart implements Renderable {

    private PointListContainer<PointList> mData;

    public LineChart(final PointListContainer<PointList> data) {
        Objects.requireNonNull(data);
        mData = data;
    }

    public PointListContainer<PointList> getData() {
        return mData;
    }

    public int getNumberOfLines() {
        return mData.getSize();
    }

    public double getMinY() {
        return mData.getMinY();
    }

    public double getMaxY() {
        return mData.getMaxY();
    }

    public double getMinX() {
        return mData.getMinX();
    }

    public double getMaxX() {
        return mData.getMaxX();
    }

}
