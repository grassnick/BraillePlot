package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

public class LinearMappingAxisRasterizer implements Rasterizer<Axis> {

    @Override
    public void rasterize(Axis data, AbstractRasterCanvas canvas) throws InsufficientRenderingAreaException {
        MatrixData<Boolean> image = canvas.getCurrentPage();
        System.out.println("I am the axis rasterizer");
    }
}
