package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;

public class BarChartPlotter implements Plotter<CategoricalBarChart> {

    @Override
    public double plot(final CategoricalBarChart diagram, final PlotCanvas canvas) throws InsufficientRenderingAreaException {
        if (canvas.getBarAcc()) {
            StackedBarChartPlotter stackedPlotter = new StackedBarChartPlotter();
            stackedPlotter.plot(diagram, canvas);
        } else {
            GroupedBarChartPlotter groupedPlotter = new GroupedBarChartPlotter();
            groupedPlotter.plot(diagram, canvas);
        }

        return 0;

    }

}
