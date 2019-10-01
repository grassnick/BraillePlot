package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
import de.tudresden.inf.mci.brailleplot.rendering.Legend;
import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;

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
    private static final double SYMDESCSCALE = 2.5;
    private static final double NEWPAGESCALE = 4.5;
    private static final int SYMBOLSCALE = 3;
    private static final int MARGINSCALE = 3;
    private static final int COMPARE3 = 3;
    private static final int STARTLINES = 5;
    private static final int STARTNEWPAGE = 5;
    private static final int STARTPOINT = 10;
    private static final int STARTDOTDESC = 15;
    private static final int CURRENTPAGESCALE = 15;
    private static final int NEWPAGESCALE2 = 20;
    private static final int STARTFRAMESLINES = 25;
    private static final int STARTLINEDESC = 30;
    private static final int ENDLINES = 35;
    private static final int STARTLINESFRAMESDESC = 50;
    private static final int ENDFRAMESLINES = 55;
    private static final int ENDNEWPAGE = 65;
    private static final int STARTTEXTUREDESC = 75;

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
        double last = plotLine(legende, mCanvas.getCellDistHor(), 0, false);
        char[] titel = "Titel:".toCharArray();
        last = plotLine(titel, mCanvas.getCellDistHor(), last + mStepVer + mSpace, false);
        char[] titelName = ("dummy").toCharArray();
        // char[] titleName = mPlotter.mDiagram.getTitle().toCharArray();
        last = plotLine(titelName, mCanvas.getCellDistHor(), last + mStepVer, false);
        char[] xAxis = "X-Achse:".toCharArray();
        last = plotLine(xAxis, mCanvas.getCellDistHor(), last + mStepVer + mSpace, false);

        char[] xAxisDesc;
        if (mCanvas.getXScaleFactor() == 1) {
            xAxisDesc = ("dummy" + " in " + "dummy").toCharArray();
            // xAxisDesc = mPlotter.mDiagram.getXAxisName().toCharArray();
        } else {
            xAxisDesc = ("dummy" + " in " + "dummy" + ", mal 10 hoch " + mCanvas.getXScaleFactor()).toCharArray();
            // xAxisDesc = (mPlotter.mDiagram.getXAxisName() + ", mal 10 hoch " + mCanvas.getXScaleFactor()).toCharArray();
        }
        last = plotLine(xAxisDesc, mCanvas.getCellDistHor(), last + mStepVer, false);

        if (mCanvas.getAxesDerivation()) {
            char[] xAxisNames = "X-Achsenbeschriftung:".toCharArray();
            last = plotLine(xAxisNames, mCanvas.getCellDistHor(), last + mStepVer + mSpace, false);

            Map<String, Map<String, String>> map = legend.getSymbolExplanationGroups();
            Map<String, String> map2 = map.get("x-axis");

            for (int i = 0; i < map2.size(); i++) {
                char[] desc = (symbols[i] + ": " + map2.get(Integer.toString(i))).toCharArray();
                last = plotLine(desc, mCanvas.getCellDistHor(), last + mStepVer, true);
            }
        }

        char[] yAxis = "Y-Achse:".toCharArray();
        last = plotLine(yAxis, mCanvas.getCellDistHor(), last + mStepVer + mSpace, false);

        char[] yAxisDesc;
        if (mCanvas.getYScaleFactor() == 1) {
            yAxisDesc = ("dummy" + " in " + "dummy").toCharArray();
            // yAxisDesc = mPlotter.mDiagram.getYAxisName().toCharArray();
        } else {
            yAxisDesc = ("dummy" + " in " + "dummy" + ", mal 10 hoch " + mCanvas.getYScaleFactor()).toCharArray();
            // yAxisDesc = (mPlotter.mDiagram.getYAxisName() + ", mal 10 hoch " + mCanvas.getYScaleFactor()).toCharArray();
        }
        last = plotLine(yAxisDesc, mCanvas.getCellDistHor(), last + mStepVer, false);

        if (mCanvas.getAxesDerivation() || legend.getType() == COMPARE3) {
            char[] yAxisNames = "Y-Achsenbeschriftung:".toCharArray();
            last = plotLine(yAxisNames, mCanvas.getCellDistHor(), last + mStepVer + mSpace, false);

            Map<String, Map<String, String>> map = legend.getSymbolExplanationGroups();
            Map<String, String> map2 = map.get("y-axis");

            for (int i = 0; i < map2.size(); i++) {
                char[] desc = (symbols[i] + ": " + map2.get(Integer.toString(i))).toCharArray();
                last = plotLine(desc, mCanvas.getCellDistHor(), last + mStepVer, true);
            }
        }

        char[] daten = "Messreihen:".toCharArray();
        last = plotLine(daten, mCanvas.getCellDistHor(), last + mStepVer + mSpace, false);

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

                splotter.addPoint(STARTPOINT, last + mStepVer + SYMBOLSCALE * mHeight);
                if (i == 0) {
                    splotter.drawDot(STARTPOINT, last + mStepVer + SYMBOLSCALE * mHeight);
                } else if (i == 1) {
                    splotter.drawX(STARTPOINT, last + mStepVer + SYMBOLSCALE * mHeight);
                } else if (i == 2) {
                    splotter.drawCircle(STARTPOINT, last + mStepVer + SYMBOLSCALE * mHeight);
                }

                char[] rahmen = ("  " + frame).toCharArray();
                last = plotLine(rahmen, STARTDOTDESC, last + mStepVer + SYMDESCSCALE * mHeight, false);
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
                splotter.drawLines(STARTLINES, ENDLINES, last + mStepVer + SYMBOLSCALE * mHeight, last + mStepVer + SYMBOLSCALE * mHeight, i);

                char[] rahmen = ("  " + frame).toCharArray();
                last = plotLine(rahmen, STARTLINEDESC, last + mStepVer + SYMDESCSCALE * mHeight, false);
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

                splotter.addPoint(STARTPOINT, last + mStepVer + SYMBOLSCALE * mHeight);
                if (i == 0) {
                    splotter.drawDot(STARTPOINT, last + mStepVer + SYMBOLSCALE * mHeight);
                } else if (i == 1) {
                    splotter.drawX(STARTPOINT, last + mStepVer + SYMBOLSCALE * mHeight);
                } else if (i == 2) {
                    splotter.drawCircle(STARTPOINT, last + mStepVer + SYMBOLSCALE * mHeight);
                }

                splotter.drawLines(STARTFRAMESLINES, ENDFRAMESLINES, last + mStepVer + SYMBOLSCALE * mHeight, last + mStepVer + SYMBOLSCALE * mHeight, i);

                char[] rahmen = ("  " + frame).toCharArray();
                last = plotLine(rahmen, STARTLINESFRAMESDESC, last + mStepVer + SYMDESCSCALE * mHeight, false);
            }
        } else if (legend.getType() == COMPARE3) {
            // bar chart
            Map<String, Map<String, String>> map = legend.getSymbolExplanationGroups();
            Map<String, String> map2 = map.get("textures");
            int len = map2.size();
            for (int i = 0; i < len; i++) {
                String frame = map2.get(Integer.toString(i));
                AbstractBarChartPlotter splotter = (AbstractBarChartPlotter) mPlotter;
                splotter.setCanvas(mCanvas);
                splotter.setData();

                boolean newPage = splotter.plotAndFillRectangle(last + NEWPAGESCALE * mStepVer, STARTNEWPAGE, ENDNEWPAGE, i, true);

                if (newPage) {
                    last = 0;
                }

                char[] rahmen = frame.toCharArray();
                double current;

                if (newPage) {
                    current = last + NEWPAGESCALE2 - mCanvas.getDotDistVer();
                } else {
                    current = last + NEWPAGESCALE * mStepVer - CURRENTPAGESCALE - mCanvas.getDotDistVer();
                }

                last = plotLine(rahmen, STARTTEXTUREDESC, current, false);
                if (last < current + CURRENTPAGESCALE + mCanvas.getDotDistVer()) {
                    last += CURRENTPAGESCALE + mCanvas.getDotDistVer();
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
     * @param basicLang True for basic language.
     * @return Last y-coordinate.
     * @throws InsufficientRenderingAreaException If a translation error occurs.
     */
    private double plotLine(final char[] title, final double startX, final double startY, final boolean basicLang) throws InsufficientRenderingAreaException {

        BrailleLanguage.Language lang = BrailleLanguage.Language.DE_KURZSCHRIFT;
        if (basicLang) {
            lang = BrailleLanguage.Language.DE_BASISSCHRIFT;
        }

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
            last = i * mStepVer + starterY;
            for (double j = startX; j < mCanvas.getPageWidth() - MARGINSCALE * (mCanvas.getCellWidth() + mCanvas.getCellDistHor()) + mCanvas.getCellDistHor(); j += mStepHor) {
                if (k < title.length) {

                    // check if line break in necessary
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

                    // check if new page is necessary
                    if (last > mCanvas.getPageHeight() - mStepVer) {
                        mCanvas.getNewPage();
                        last = 0;
                        starterY = 0;
                    }

                    Rectangle rect = new Rectangle(j, last, mWidth, mHeight);
                    BrailleText text = new BrailleText(Character.toString(title[k]), rect, lang);
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
