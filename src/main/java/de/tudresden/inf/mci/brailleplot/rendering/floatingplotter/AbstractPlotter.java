package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static tec.units.ri.unit.Units.METRE;


/**
 * Abstract parent class for all plotting algorithms. Provides methods for axis drawing and point adding.
 * @author Richard Schmidt
 * @version 28.08.2019
 */
abstract class AbstractPlotter {

    ScatterPlot mDiagram;
    PlotCanvas mCanvas;
    FloatingPointData<Boolean> mData;

    double mResolution;
    double mPageWidth;
    double mPageHeight;

    // parameters to be identified by trial
    double mStepSize;
    int mNumberXTics;
    int mNumberYTics;

    // constants
    private static final double WMULT = 3;
    private static final double HMULT = 2;
    private static final double ARROWS1 = 0.5;
    private static final double ARROWS2 = 1;
    private static final double ARROWS3 = 0.3;
    private static final double ARROWS4 = 0.6;
    private static final double TICK1 = 0.5;
    private static final double TICK2 = 1;
    private static final double TICK3 = 1.5;
    private static final double TICK4 = 2;


    void drawAxes() {
        // margins for axes
        double leftMargin = WMULT * mCanvas.getCellWidth() + WMULT * mCanvas.getCellDistHor();
        double bottomMargin = mPageHeight - (HMULT * mCanvas.getCellHeight() + HMULT * mCanvas.getCellDistVer());

        // x-axis:
        double lastValue = leftMargin;
        for (double i = leftMargin; i <= mPageWidth; i += mStepSize) {
            addPoint(i, bottomMargin);
            lastValue = i;
        }

        // arrows on x-axis
        addPoint(lastValue - ARROWS1, bottomMargin + ARROWS3);
        addPoint(lastValue - ARROWS2, bottomMargin + ARROWS4);
        addPoint(lastValue - ARROWS1, bottomMargin - ARROWS3);
        addPoint(lastValue - ARROWS2, bottomMargin - ARROWS4);

        // tick marks on x-axis
        double xTickStep = (lastValue - leftMargin) / mNumberXTics;
        for (int i = 1; i < mNumberXTics + 1; i++) {
            addPoint(leftMargin + i * xTickStep, bottomMargin + TICK1);
            addPoint(leftMargin + i * xTickStep, bottomMargin + TICK2);
            addPoint(leftMargin + i * xTickStep, bottomMargin + TICK3);
            addPoint(leftMargin + i * xTickStep, bottomMargin + TICK4);
            addPoint(leftMargin + i * xTickStep, bottomMargin - TICK1);
            addPoint(leftMargin + i * xTickStep, bottomMargin - TICK2);
            addPoint(leftMargin + i * xTickStep, bottomMargin - TICK3);
            addPoint(leftMargin + i * xTickStep, bottomMargin - TICK4);
        }

        // y-axis:
        lastValue = bottomMargin;
        for (double i = bottomMargin; i >= 0; i -= mStepSize) {
            addPoint(leftMargin, i);
            lastValue = i;
        }

        // arrows on y-axis
        addPoint(leftMargin - ARROWS1, lastValue + ARROWS3);
        addPoint(leftMargin - ARROWS2, lastValue + ARROWS4);
        addPoint(leftMargin + ARROWS1, lastValue + ARROWS3);
        addPoint(leftMargin + ARROWS2, lastValue + ARROWS4);

        // tick marks on y-axis
        double yTickStep = (bottomMargin - lastValue) / mNumberYTics;
        for (int i = 1; i < mNumberYTics + 1; i++) {
            addPoint(leftMargin + TICK1, bottomMargin + i * yTickStep);
            addPoint(leftMargin + TICK2, bottomMargin + i * yTickStep);
            addPoint(leftMargin + TICK3, bottomMargin + i * yTickStep);
            addPoint(leftMargin + TICK4, bottomMargin + i * yTickStep);
            addPoint(leftMargin - TICK1, bottomMargin + i * yTickStep);
            addPoint(leftMargin - TICK2, bottomMargin + i * yTickStep);
            addPoint(leftMargin - TICK3, bottomMargin + i * yTickStep);
            addPoint(leftMargin - TICK4, bottomMargin + i * yTickStep);
        }
    }

    void addPoint(final double x, final double y) {
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true));
    }
}
