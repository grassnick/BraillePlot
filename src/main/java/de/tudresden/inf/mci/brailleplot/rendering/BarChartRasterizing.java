package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

/**
 * BarChartRasterizing. A container class for bar chart rasterizing algorithm(s) to put into the FunctionalRasterizer interface.
 * @author Leonard Kupper
 * @version 2019.07.01
 */
public final class BarChartRasterizing {

    private BarChartRasterizing() {
        // This is a helper class. Private constructor prevents it from being instantiated.
    }

    public static MatrixData uniformTextureRasterizing(final BarChart diagram, final Raster raster) {
        System.out.println("I am a bar chart rasterizing algorithm");
        return null;
    }

}