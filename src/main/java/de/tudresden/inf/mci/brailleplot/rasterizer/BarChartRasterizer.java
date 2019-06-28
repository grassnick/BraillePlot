package de.tudresden.inf.mci.brailleplot.rasterizer;

import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

public interface BarChartRasterizer<T extends Number> extends Rasterizer {

    MatrixData<T> rasterize(RepresentationStub diagram);

    FloatingPointData<T> plot(RepresentationStub diagram);

    /**
     * This is a temporary stub used by the bar chart rasterizer, because there is real representation class yet.
     */
    class RepresentationStub {

        public String test() {
            return "Hello";
        }
    }
}
