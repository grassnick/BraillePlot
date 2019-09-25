package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
import de.tudresden.inf.mci.brailleplot.rendering.Legend;

import java.util.Map;


/**
 * Class representing a plotter for legend plotting.
 * @author Richard Schmidt
 */
public class LegendPlotter implements Plotter<Legend> {

    private PlotCanvas mCanvas;
    private LiblouisBrailleTextPlotter mPlotter;

    private double height;
    private double width;
    private double stepHor;
    private double stepVer;

    // constants
    private static final int THREE = 3;
    private static final int TWENTY = 20;

    @Override
    public double plot(final Legend legend, final PlotCanvas canvas) {

        mCanvas = canvas;
        mPlotter = new LiblouisBrailleTextPlotter(mCanvas.getPrinter());
        FloatingPointData<Boolean> page = mCanvas.getNewPage();
        char[] symbolsY = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        height = mCanvas.getCellHeight();
        width = mCanvas.getCellWidth();
        stepHor = width + mCanvas.getCellDistHor();
        stepVer = height + 2 * mCanvas.getCellDistVer();

        char[] legende = "Legende:".toCharArray();
        double last = plotLine(legende, 0, 0);

        char[] titel = "Titel:".toCharArray();
        last = plotLine(titel, 0, last + stepVer);

        char[] titelName = legend.getTitle().toCharArray();
        last = plotLine(titelName, 0, last + stepVer);

        char[] xAxis = "X-Achse:".toCharArray();
        last = plotLine(xAxis, 0, last + stepVer);

        char[] xAxisDesc = ("in " + legend.getXUnit() + ", mal 10 hoch " + mCanvas.getXScaleFactor()).toCharArray();
        last = plotLine(xAxisDesc, 0, last + stepVer);

        char[] yAxis = "Y-Achse:".toCharArray();
        last = plotLine(yAxis, 0, last + stepVer);

        char[] yAxisDesc = ("in " + legend.getYUnit() + ", mal 10 hoch " + mCanvas.getYScaleFactor()).toCharArray();
        last = plotLine(yAxisDesc, 0, last + stepVer);

        if (mCanvas.getType() == THREE) {

            char[] yAxisNames = "Y-Achsenbeschriftung:".toCharArray();
            last = plotLine(yAxisNames, 0, last + stepVer);

            String[] names = legend.getYNames();

            for (int i = 0; i < names.length; i++) {
                char[] desc = (symbolsY[i] + ": " + names[i]).toCharArray();
                last = plotLine(desc, 0, last + stepVer);
            }
        }

        char[] daten = "Messreihen:".toCharArray();
        last = plotLine(daten, 0, last + stepVer);

        if (mCanvas.getType() == 0) {

            // scatter plot
            Map<String, Map<String, String>> map = legend.getSymbolExplanationGroups();
            Map<String, String> map2 = map.get("frames");
            int len = map2.size();
            for (int i = 0; i < len; i++) {
                String frame = map2.get(Integer.toString(i));
                ScatterPlotter plotter = new ScatterPlotter();
                plotter.drawCircle(0, last);
                char[] rahmen = ("  " + frame).toCharArray();
                last = plotLine(rahmen, TWENTY, last);
            }

        } else if (mCanvas.getType() == 1) {

            // line plot without frames
            // TODO
            int i;

        } else if (mCanvas.getType() == 2) {

            // line plot with frames
            // TODO
            int i;

        } else {

            // bar chart
            // TODO
            int i;

        }

        return 0;
    }

    private double plotLine(final char[] title, final double startX, final double startY) {

        int k = 0;
        double last = startY;

        loop:
        for (int i = 0; i < THREE; i++) {
            for (double j = mCanvas.getCellDistHor() + mCanvas.getDotDiameter() / 2 + startX; j < mCanvas.getPageWidth() - width - mCanvas.getCellDistHor(); j += stepHor) {
                if (k < title.length) {
                    last = mCanvas.getCellDistVer() + i * stepVer + startY;
                    Rectangle rect = new Rectangle(j, last, width, height);
                    BrailleText text = new BrailleText(Character.toString(title[k]), rect);
                    k++;
                    j = mPlotter.plot(text, mCanvas);
                } else {
                    break loop;
                }
            }
        }

        return last;
    }
}
