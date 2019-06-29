package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

public interface RasterizingAlgorithm {
    MatrixData rasterize(DiagramStub data, Raster raster) throws InsufficientRenderingAreaException;
}
