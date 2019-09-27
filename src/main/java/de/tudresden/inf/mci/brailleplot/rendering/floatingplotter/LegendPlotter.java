package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
import de.tudresden.inf.mci.brailleplot.rendering.Legend;

import java.util.Map;
import java.util.Objects;


/**
 * Class representing a plotter for legend plotting.
 * @author Richard Schmidt
 */
public class LegendPlotter implements Plotter<Legend> {

    private PlotCanvas mCanvas;
    private LiblouisBrailleTextPlotter mTextPlotter;
    private AbstractPlotter mPlotter;

    private double mHeight;
    private double mWidth;
    private double mStepHor;
    private double mStepVer;

    // constants
    private static final double ONEFOUR = 1.4;
    private static final double ONESEVEN = 1.7;
    private static final double TWOFOUR = 2.4;
    private static final int THREE = 3;
    private static final int FIVE = 5;
    private static final int TEN = 10;
    private static final int TWENTY = 20;
    private static final int TWENTYFIVE = 25;
    private static final int THIRTYFIVE = 35;
    private static final int FIFTYFIVE = 55;
    private static final int SIXTYFIVE = 65;

    /**
     * Plots a {@link Legend} instance onto a {@link PlotCanvas}.
     * @param legend An instance of {@link  Legend} representing the legend.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas} or
     * if there are more data series than frames, line styles or textures.
     */
    @Override
    public double plot(final Legend legend, final PlotCanvas canvas) throws InsufficientRenderingAreaException {
        mCanvas = canvas;
        mCanvas.getNewPage();
        mTextPlotter = new LiblouisBrailleTextPlotter(mCanvas.getPrinter());
        char[] symbolsY = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        mHeight = mCanvas.getCellHeight();
        mWidth = mCanvas.getCellWidth();
        mStepHor = mWidth + mCanvas.getCellDistHor();
        mStepVer = mHeight + ONESEVEN * mCanvas.getCellDistVer();

        char[] legende = "Legende:".toCharArray();
        double last = plotLine(legende, 0, 0);
        char[] titel = "Titel:".toCharArray();
        last = plotLine(titel, 0, last + mStepVer);
        char[] titelName = legend.getTitle().toCharArray();
        last = plotLine(titelName, 0, last + mStepVer);
        char[] xAxis = "X-Achse:".toCharArray();
        last = plotLine(xAxis, 0, last + mStepVer);

        char[] xAxisDesc;
        if (mCanvas.getXScaleFactor() == 1) {
            xAxisDesc = (legend.getXName() + " in " + legend.getXUnit()).toCharArray();
        } else {
            xAxisDesc = (legend.getXName() + " in " + legend.getXUnit() + ", mal 10 hoch " + mCanvas.getXScaleFactor()).toCharArray();
        }
        last = plotLine(xAxisDesc, 0, last + mStepVer);

        char[] yAxis = "Y-Achse:".toCharArray();
        last = plotLine(yAxis, 0, last + mStepVer);

        char[] yAxisDesc;
        if (mCanvas.getYScaleFactor() == 1) {
            yAxisDesc = (legend.getYName() + " in " + legend.getYUnit()).toCharArray();
        } else {
            yAxisDesc = (legend.getYName() + " in " + legend.getYUnit() + ", mal 10 hoch " + mCanvas.getYScaleFactor()).toCharArray();
        }
        last = plotLine(yAxisDesc, 0, last + mStepVer);

        if (legend.getType() == THREE) {
            // bar charts
            char[] yAxisNames = "Y-Achsenbeschriftung:".toCharArray();
            last = plotLine(yAxisNames, 0, last + mStepVer);

            String[] names = legend.getDesc();

            for (int i = 0; i < names.length; i++) {
                char[] desc = (symbolsY[i] + ": " + names[i]).toCharArray();
                last = plotLine(desc, 0, last + mStepVer);
            }
        }
        char[] daten = "Messreihen:".toCharArray();
        last = plotLine(daten, 0, last + mStepVer);

        if (legend.getType() == 0) {
            // scatter plot
            Map<String, Map<String, String>> map = legend.getSymbolExplanationGroups();
            Map<String, String> map2 = map.get("frames");
            int len = map2.size();
            for (int i = 0; i < len; i++) {
                String frame = map2.get(Integer.toString(i));
                ScatterPlotter splotter = (ScatterPlotter) mPlotter;
                splotter.setCanvas(mCanvas);
                splotter.setData();

                if (i == 0) {
                    splotter.drawDot(TEN, last + mStepVer + mHeight);
                } else if (i == 1) {
                    splotter.drawX(TEN, last + mStepVer + mHeight);
                    splotter.addPoint(TEN, last + mStepVer + mHeight);
                } else if (i == 2) {
                    splotter.drawCircle(TEN, last + mStepVer + mHeight);
                }

                char[] rahmen = ("  " + frame).toCharArray();
                last = plotLine(rahmen, TWENTY, last + mStepVer);
            }
        } else if (legend.getType() == 1) {
            // line plot without frames
            Map<String, Map<String, String>> map = legend.getSymbolExplanationGroups();
            Map<String, String> map2 = map.get("lines");
            int len = map2.size();
            for (int i = 0; i < len; i++) {
                String frame = map2.get(Integer.toString(i));
                LinePlotter splotter = (LinePlotter) mPlotter;
                splotter.setCanvas(mCanvas);
                splotter.setData();
                splotter.drawLines(FIVE, THIRTYFIVE, last + mStepVer + mHeight, last + mStepVer + mHeight, i);

                char[] rahmen = ("  " + frame).toCharArray();
                last = plotLine(rahmen, TWENTY, last + mStepVer);
            }
        } else if (legend.getType() == 2) {
            // line plot with frames
            Map<String, Map<String, String>> map = legend.getSymbolExplanationGroups();
            Map<String, String> map2 = map.get("frames");
            int len = map2.size();
            for (int i = 0; i < len; i++) {
                String frame = map2.get(Integer.toString(i));
                LinePlotter splotter = (LinePlotter) mPlotter;
                splotter.setCanvas(mCanvas);
                splotter.setData();

                if (i == 0) {
                    splotter.drawDot(TEN, last + mStepVer + mHeight);
                } else if (i == 1) {
                    splotter.drawX(TEN, last + mStepVer + mHeight);
                } else if (i == 2) {
                    splotter.drawCircle(TEN, last + mStepVer + mHeight);
                }

                splotter.drawLines(TWENTYFIVE, FIFTYFIVE, last + mStepVer + mHeight, last + mStepVer + mHeight, i);

                char[] rahmen = ("  " + frame).toCharArray();
                last = plotLine(rahmen, 2 * TWENTY, last + mStepVer);
            }
        } else if (legend.getType() == THREE) {
            // bar chart
            Map<String, Map<String, String>> map = legend.getSymbolExplanationGroups();
            Map<String, String> map2 = map.get("textures");
            int len = map2.size();
            for (int i = 0; i < len; i++) {
                String frame = map2.get(Integer.toString(i));
                AbstractBarChartPlotter splotter = (AbstractBarChartPlotter) mPlotter;
                splotter.setCanvas(mCanvas);
                splotter.setData();

                boolean newPage;
                if (i == 0) {
                    newPage = splotter.plotAndFillRectangle(last + 2 * mStepVer + mHeight + mCanvas.getCellDistVer(), SIXTYFIVE, i, true);
                } else {
                    newPage = splotter.plotAndFillRectangle(last + TWOFOUR * mStepVer + mHeight + mCanvas.getCellDistVer(), SIXTYFIVE, i, true);
                }

                if (newPage) {
                    last = 0;
                }

                char[] rahmen = ("  " + frame).toCharArray();
                if (i == 0 || newPage) {
                    last = plotLine(rahmen, FIFTYFIVE, last + TEN) + mCanvas.getCellDistVer();
                } else {
                    last = plotLine(rahmen, FIFTYFIVE, last + ONEFOUR * mStepVer + ONESEVEN * mCanvas.getCellDistVer()) + mCanvas.getCellDistVer();
                }
            }
        }
        return 0;
    }

    /**
     * Plots a string in Braille characters.
     * @param title Title as String[].
     * @param startX Absolute x-coordinate of the starting point.
     * @param startY Absolute y-coordinate of the starting point.
     * @return Last y-coordinate.
     */
    private double plotLine(final char[] title, final double startX, final double startY) {

        int k = 0;
        double starterY = startY;
        double last = starterY;

        if (last > mCanvas.getPageHeight() - mStepVer) {
            mCanvas.getNewPage();
            last = 0;
            starterY = 0;
        }

        loop:
        for (int i = 0; i < THREE; i++) {
            for (double j = mCanvas.getCellDistHor() + mCanvas.getDotDiameter() / 2 + startX; j < mCanvas.getPageWidth() - THREE * (mCanvas.getCellWidth() + mCanvas.getCellDistHor()) + mCanvas.getCellDistHor(); j += mStepHor) {
                if (k < title.length) {
                    last = mCanvas.getCellDistVer() + i * mStepVer + starterY;

                    if (last > mCanvas.getPageHeight() - mStepVer) {
                        mCanvas.getNewPage();
                        last = 0;
                        starterY = 0;
                    }

                    Rectangle rect = new Rectangle(j, last, mWidth, mHeight);
                    BrailleText text = new BrailleText(Character.toString(title[k]), rect);
                    k++;
                    j = mTextPlotter.plot(text, mCanvas);
                } else {
                    break loop;
                }
            }
        }

        return last;
    }

    /**
     * Setter for mPlotter.
     * @param plotter Plotter to be set.
     */
    void setPlotter(final AbstractPlotter plotter) {
        mPlotter = Objects.requireNonNull(plotter);
    }
}
