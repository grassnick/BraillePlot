package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

public class BarChartRasterizing implements RasterizingAlgorithm {
    @Override
    public MatrixData rasterize(DiagramStub data, Raster raster) {
        if (data instanceof BarChart) {
            BarChart barChart = (BarChart) data;
        } else {
            throw new IllegalArgumentException("Wrong diagram type: " + data.getClass().getCanonicalName());
        }
        return null;
    }
}