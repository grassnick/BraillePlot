package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

/**
 * Representation of a target onto which can be drawn. It wraps a {@link PrintableData} instance and specifies the size of the drawing area (in mm).
 * @author Leonard Kupper
 * @version 2019.07.12
 */
public abstract class AbstractCanvas {

    Printer mPrinter;
    Format mFormat;
    double mMillimeterWidth;
    double mMillimeterHeight;

    double mMarginTop;
    double mMarginBottom;
    double mMarginLeft;
    double mMarginRight;

    List<PrintableData> mPageContainer;

    AbstractCanvas(final Printer printer, final Format format) {
        mPrinter = printer;
        mFormat = format;
        mPageContainer = new ArrayList<>();

        readConfig();
    }

    private void readConfig() {

        double indentTop = mPrinter.getProperty("indent.top").toDouble();
        double indentLeft = mPrinter.getProperty("indent.left").toDouble();
        double indentBottom = mPrinter.getProperty("indent.bottom").toDouble();
        double indentRight = mPrinter.getProperty("indent.right").toDouble();

        // Page margins in mm. The indentation is subtracted, we have no control over it, so the canvas just has to
        // create the remaining 'virtual' margins by omitting some cells from the printing area rectangle.
        // However, even this 'virtual' margin can never be negative! We can't add printing space where there is none.
        mMarginTop = max(mFormat.getProperty("margin.top").toInt() - indentTop, 0);
        mMarginLeft = max(mFormat.getProperty("margin.left").toInt() - indentLeft, 0);
        mMarginBottom = max(mFormat.getProperty("margin.bottom").toInt() - indentBottom, 0);
        mMarginRight = max(mFormat.getProperty("margin.right").toInt() - indentRight, 0);

        // How big is the full page area in mm?
        // The page size can't be negative too of course.
        mMillimeterWidth = max(mFormat.getProperty("page.width").toInt() - (indentLeft + indentRight), 0);
        mMillimeterHeight = max(mFormat.getProperty("page.height").toInt() - (indentTop + indentBottom), 0);

    }

    /**
     * This method is supposed to return the full width of the canvas.
     * @return The width of the canvas in millimeters.
     */
    public double getAbsoluteWidth() {
        return mMillimeterWidth - (mMarginLeft + mMarginRight);
    }

    /**
     * This method is supposed to return the full height of the canvas.
     * @return The height of the canvas in millimeters.
     */
    public double getAbsoluteHeight() {
        return mMillimeterHeight - (mMarginTop + mMarginBottom);
    }

}
