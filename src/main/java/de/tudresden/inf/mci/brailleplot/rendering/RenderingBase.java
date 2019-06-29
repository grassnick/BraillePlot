package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

public class RenderingBase {

    private BarChartRasterizing mBarChartRasterizing;
    private Raster mRaster;

    public RenderingBase(BarChartRasterizing barChartRasterizing) {
        mBarChartRasterizing = barChartRasterizing;
    }

    // Rasterizing

    public MatrixData rasterize(DiagramStub data) {
        MatrixData result;
        if (data instanceof BarChart) {
            result = mBarChartRasterizing.rasterize(data, mRaster);
        } else {
            throw new IllegalArgumentException("No rasterizing algorithm available for: " + data.getClass().getCanonicalName());
        }
        return result;
    }

    // Getter & Setter

    public void setRaster(Raster raster) {
        mRaster = raster;
    }
    Raster getRaster() {
        return mRaster;
    }
}
