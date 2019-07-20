package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Representation of a target onto which can be drawn. It wraps a {@link PrintableData} instance and specifies the size of the drawing area (in mm).
 * @author Leonard Kupper
 * @version 2019.07.20
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

        double constraintTop = mPrinter.getProperty("constraint.top").toDouble();
        double constraintLeft = mPrinter.getProperty("constraint.left").toDouble();
        double constraintHeight, constraintWidth;
        if (mPrinter.getPropertyNames().contains("constraint.height")) {
            constraintHeight = mPrinter.getProperty("constraint.height").toDouble();
        } else {
            constraintHeight = Integer.MAX_VALUE;
        }
        if (mPrinter.getPropertyNames().contains("constraint.width")) {
            constraintWidth = mPrinter.getProperty("constraint.width").toDouble();
        } else {
            constraintWidth = Integer.MAX_VALUE;
        }
        int pageWidth = mFormat.getProperty("page.width").toInt();
        int pageHeight = mFormat.getProperty("page.height").toInt();

        // Page margins in mm. The printing area constraint is subtracted, we have no control over it, so the canvas just
        // has to create the remaining 'virtual' margins by omitting some cells from the printing area rectangle.
        // However, even this 'virtual' margin can never be negative! We can't add printing space where there is none.
        mMarginTop = max(0, mFormat.getProperty("margin.top").toInt() - constraintTop);
        mMarginLeft = max(0, mFormat.getProperty("margin.left").toInt() - constraintLeft);
        mMarginBottom = max(0, mFormat.getProperty("margin.bottom").toInt() - max(0, pageHeight - (constraintTop + constraintHeight)));
        mMarginRight = max(0, mFormat.getProperty("margin.right").toInt() - max(0, pageWidth - (constraintLeft + constraintWidth)));

        // How big is the technically accessible area of the page in mm?
        // These sizes can't be negative too of course.
        mMillimeterWidth = max(0, min(pageWidth - constraintLeft, constraintWidth));
        mMillimeterHeight = max(0, min(pageHeight - constraintTop, constraintHeight));

    }

    /**
     * This method is supposed to return the full width of the canvas.
     * @return The width of the canvas in millimeters.
     */
    public double getPrintableWidth() {
        return mMillimeterWidth - (mMarginLeft + mMarginRight);
    }

    /**
     * This method is supposed to return the full height of the canvas.
     * @return The height of the canvas in millimeters.
     */
    public double getPrintableHeight() {
        return mMillimeterHeight - (mMarginTop + mMarginBottom);
    }

    /**
     * Get the number of pages in the canvas.
     * @return The number of pages.
     */
    public int getPageCount() {
        return mPageContainer.size();
    }

    /**
     * Get an Iterator for the PrintableData instances representing the canvas pages. The single instances should be
     * casted to the regarding concrete type depending on the canvas implementation.
     * @return A {@link ListIterator}&lt;{@link PrintableData}&gt;.
     */
    public ListIterator<PrintableData> getPageIterator() {
        return mPageContainer.listIterator();
    }

}
