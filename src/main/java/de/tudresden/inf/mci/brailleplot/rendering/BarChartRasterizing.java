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

        double hRatio = canvas.getHorizontalCellCount() / diagram.getNumberOfCategories();
        double vRatio = canvas.getVerticalCellCount() / diagram.getNumberOfCategories();

        System.out.println(diagram.getValue(1));


        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                canvas.getMatrixData().setValue(y, x, true);
            }
        }

    }

}