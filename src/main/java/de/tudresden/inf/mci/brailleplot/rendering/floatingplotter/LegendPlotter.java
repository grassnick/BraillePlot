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
    private static final double TWOFIVE = 2.5;
    private static final double FOURFIVE = 4.5;
    private static final int THREE = 3;
    private static final int FIVE = 5;
    private static final int TEN = 10;
    private static final int FIFTEEN = 15;
    private static final int TWENTY = 20;
    private static final int TWENTYFIVE = 25;
    private static final int THIRTY = 30;
    private static final int THIRTYFIVE = 35;
    private static final int FIFTY = 50;
    private static final int FIFTYFIVE = 55;
    private static final int SIXTYFIVE = 65;
    private static final int SEVENTYFIVE = 75;

    /**
     * Plots a {@link Legend} instance onto a {@link PlotCanvas}.
     * @param legend An instance of {@link  Legend} representing the legend.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @throws InsufficientRenderingAreaException If too little space is available on the {@link PlotCanvas} or
     * if there are more data series than frames, line styles or textures.
     */
    @SuppressWarnings("MethodLength")
    @Override
    public double plot(final Legend legend, final PlotCanvas canvas) throws InsufficientRenderingAreaException {
        mCanvas = canvas;
        mCanvas.getNewPage();
        mTextPlotter = new LiblouisBrailleTextPlotter(mCanvas.getPrinter());
        mHeight = mCanvas.getCellHeight();
        mWidth = mCanvas.getCellWidth();
        mStepHor = mWidth + mCanvas.getCellDistHor();
        mStepVer = mHeight + mCanvas.getCellDistVer();
        double mSpace = mCanvas.getCellDistVer();
        char[] symbols = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        char[] legende = "Legende:".toCharArray();
        double last = plotLine(legende, mCanvas.getCellDistHor(), 0);
        char[] titel = "Titel:".toCharArray();
        last = plotLine(titel, mCanvas.getCellDistHor(), last + mStepVer + mSpace);
        char[] titelName = ("dummy").toCharArray();
        last = plotLine(titelName, mCanvas.getCellDistHor(), last + mStepVer);
        char[] xAxis = "X-Achse:".toCharArray();
        last = plotLine(xAxis, mCanvas.getCellDistHor(), last + mStepVer + mSpace);

        char[] xAxisDesc;
        if (mCanvas.getXScaleFactor() == 1) {
            xAxisDesc = ("dummy" + " in " + "dummy").toCharArray();
        } else {
            xAxisDesc = ("dummy" + " in " + "dummy" + ", mal 10 hoch " + mCanvas.getXScaleFactor()).toCharArray();
        }
        last = plotLine(xAxisDesc, mCanvas.getCellDistHor(), last + mStepVer);

        if (mCanvas.getAxesDerivation()) {
            char[] xAxisNames = "X-Achsenbeschriftung:".toCharArray();
            last = plotLine(xAxisNames, mCanvas.getCellDistHor(), last + mStepVer + mSpace);

            Map<String, Map<String, String>> map = legend.getSymbolExplanationGroups();
            Map<String, String> map2 = map.get("x-axis");

            for (int i = 0; i < map2.size(); i++) {
                char[] desc = (symbols[i] + ": " + map2.get(Integer.toString(i))).toCharArray();
                last = plotLine(desc, mCanvas.getCellDistHor(), last + mStepVer);
            }
        }

        char[] yAxis = "Y-Achse:".toCharArray();
        last = plotLine(yAxis, mCanvas.getCellDistHor(), last + mStepVer + mSpace);

        char[] yAxisDesc;
        if (mCanvas.getYScaleFactor() == 1) {
            yAxisDesc = ("dummy" + " in " + "dummy").toCharArray();
        } else {
            yAxisDesc = ("dummy" + " in " + "dummy" + ", mal 10 hoch " + mCanvas.getYScaleFactor()).toCharArray();
        }
        last = plotLine(yAxisDesc, mCanvas.getCellDistHor(), last + mStepVer);

        if (mCanvas.getAxesDerivation() || legend.getType() == THREE) {
            char[] yAxisNames = "Y-Achsenbeschriftung:".toCharArray();
            last = plotLine(yAxisNames, mCanvas.getCellDistHor(), last + mStepVer + mSpace);

            Map<String, Map<String, String>> map = legend.getSymbolExplanationGroups();
            Map<String, String> map2 = map.get("y-axis");

            for (int i = 0; i < map2.size(); i++) {
                char[] desc = (symbols[i] + ": " + map2.get(Integer.toString(i))).toCharArray();
                last = plotLine(desc, mCanvas.getCellDistHor(), last + mStepVer);
            }
        }

        char[] daten = "Messreihen:".toCharArray();
        last = plotLine(daten, mCanvas.getCellDistHor(), last + mStepVer + mSpace);

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

                splotter.addPoint(TEN, last + mStepVer + THREE * mHeight);
                if (i == 0) {
                    splotter.drawDot(TEN, last + mStepVer + THREE * mHeight);
                } else if (i == 1) {
                    splotter.drawX(TEN, last + mStepVer + THREE * mHeight);
                } else if (i == 2) {
                    splotter.drawCircle(TEN, last + mStepVer + THREE * mHeight);
                }

                char[] rahmen = ("  " + frame).toCharArray();
                last = plotLine(rahmen, FIFTEEN, last + mStepVer + TWOFIVE * mHeight);
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
                splotter.drawLines(FIVE, THIRTYFIVE, last + mStepVer + THREE * mHeight, last + mStepVer + THREE * mHeight, i);

                char[] rahmen = ("  " + frame).toCharArray();
                last = plotLine(rahmen, THIRTY, last + mStepVer + TWOFIVE * mHeight);
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

                splotter.addPoint(TEN, last + mStepVer + THREE * mHeight);
                if (i == 0) {
                    splotter.drawDot(TEN, last + mStepVer + THREE * mHeight);
                } else if (i == 1) {
                    splotter.drawX(TEN, last + mStepVer + THREE * mHeight);
                } else if (i == 2) {
                    splotter.drawCircle(TEN, last + mStepVer + THREE * mHeight);
                }

                splotter.drawLines(TWENTYFIVE, FIFTYFIVE, last + mStepVer + THREE * mHeight, last + mStepVer + THREE * mHeight, i);

                char[] rahmen = ("  " + frame).toCharArray();
                last = plotLine(rahmen, FIFTY, last + mStepVer + TWOFIVE * mHeight);
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

                boolean newPage = splotter.plotAndFillRectangle(last + FOURFIVE * mStepVer, FIVE, SIXTYFIVE, i, true);

                if (newPage) {
                    last = 0;
                }

                char[] rahmen = frame.toCharArray();
                double current;

                if (newPage) {
                    current = last + TWENTY - mCanvas.getDotDistVer();
                } else {
                    current = last + FOURFIVE * mStepVer - FIFTEEN - mCanvas.getDotDistVer();
                }

                last = plotLine(rahmen, SEVENTYFIVE, current);
                if (last < current + FIFTEEN + mCanvas.getDotDistVer()) {
                    last += FIFTEEN + mCanvas.getDotDistVer();
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

        int i = 0;
        loop:
        while (true) {
            for (double j = startX; j < mCanvas.getPageWidth() - THREE * (mCanvas.getCellWidth() + mCanvas.getCellDistHor()) + mCanvas.getCellDistHor(); j += mStepHor) {
                if (k < title.length) {
                    last = i * mStepVer + starterY;

                    int m = 0;

                    for (int l = k; l < title.length; l++) {
                        if (Character.toString(title[l]).equals(" ")) {
                            break;
                        }
                        m++;
                    }

                    if (j > mCanvas.getPageWidth() - (m + 1) * (mCanvas.getCellWidth() + mCanvas.getCellDistHor())) {
                        j = startX;
                        last += mStepVer;
                    }

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

            if (k < title.length) {
                if (Character.toString(title[k]).equals(" ")) {
                    k++;
                }
            }

            i++;
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
