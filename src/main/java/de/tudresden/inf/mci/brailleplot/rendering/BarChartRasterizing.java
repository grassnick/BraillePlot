package de.tudresden.inf.mci.brailleplot.rendering;

/**
 * BarChartRasterizing. A container class for bar chart rasterizing algorithm(s) to put into the FunctionalRasterizer interface.
 * @author Leonard Kupper
 * @version 2019.07.01
 */
public final class BarChartRasterizing {

    private BarChartRasterizing() {
        // This is a helper class. Private constructor prevents it from being instantiated.
    }

    public static void uniformTextureRasterizing(final BarChart diagram, final AbstractRasterCanvas canvas) {

        System.out.println("I am a bar chart rasterizing algorithm");
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                canvas.getMatrixData().setValue(y, x, true);
            }
        }

    }

}