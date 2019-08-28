package de.tudresden.inf.mci.brailleplot.layout;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Representation of a target onto which can be drawn. It wraps a {@link PrintableData} instance and specifies the size of the drawing area (in mm).
 * @param <T> The type of the managed {@link PrintableData}
 * @author Leonard Kupper, Georg Graßnick
 * @version 2019.08.26
 */
public abstract class AbstractCanvas<T extends PrintableData> {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    Printer mPrinter;
    Format mFormat;

    Rectangle mPrintableArea;

    private double mDotDiameter;
    private double mConstraintLeft;
    private double mConstraintTop;

    List<T> mPageContainer;

    AbstractCanvas(final Printer printer, final Format format) throws InsufficientRenderingAreaException {

        mLogger.trace("Creating new canvas");

        mPrinter = printer;
        mFormat = format;
        mPageContainer = new ArrayList<>();

        readConfig();

    }

    private void readConfig() throws InsufficientRenderingAreaException {

        mLogger.trace("Reading general printer and format configuration for printing area calculation");

        // New approach using a box model:

        // Create a page box
        double pageWidth = mFormat.getProperty("page.width").toDouble();
        double pageHeight = mFormat.getProperty("page.height").toDouble();
        Rectangle pageBox = new Rectangle(0, 0, pageWidth, pageHeight);
        mLogger.trace("Determined page box: {}", pageBox);

        // Create a margin box
        mLogger.trace("Cropping edges by defined margins:");
        double marginTop = mFormat.getProperty("margin.top").toDouble();
        double marginLeft = mFormat.getProperty("margin.left").toDouble();
        double marginBottom = mFormat.getProperty("margin.bottom").toDouble();
        double marginRight = mFormat.getProperty("margin.right").toDouble();
        Rectangle marginBox = new Rectangle(pageBox);
        try {
            marginBox.removeFromTop(marginTop);
            marginBox.removeFromLeft(marginLeft);
            marginBox.removeFromBottom(marginBottom);
            marginBox.removeFromRight(marginRight);
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("The sum of the defined margins is bigger than the page size.", e);
        }
        mLogger.trace("Determined margin box: {}", marginBox);

        // Create a constraint box
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
        Rectangle constraintBox = new Rectangle(constraintLeft, constraintTop, constraintWidth, constraintHeight);
        mLogger.trace("Determined constraint box: {}", constraintBox);

        mPrintableArea = calculatePrintingArea(marginBox, constraintBox);
        mLogger.info("The calculated available printing area equals: {}", mPrintableArea);

        mDotDiameter = mPrinter.getProperty("raster.dotDiameter").toDouble();

        // Constraints
        mConstraintLeft =  mPrinter.getProperty("constraint.left").toDouble();
        mConstraintTop =  mPrinter.getProperty("constraint.top").toDouble();

    }

    /**
     * A universal help function to calculate the printable area from original page size, desired minimum margins
     * and the given area constraints of the printer.
     * @param marginBox A rectangle representing the page with cropped edges representing the margins.
     * @param constraintBox A rectangle representing the printer constraint as [x = constraint x, y = constraint y,
     *                      w = constraint width, h = constraint height]
     * @return A rectangle representing the valid printing area.
     */
    final Rectangle calculatePrintingArea(final Rectangle marginBox, final Rectangle constraintBox) {
        return marginBox.intersectedWith(constraintBox).translatedBy(-1 * constraintBox.getX(), -1 * constraintBox.getY());
    }

    /**
     * This method is supposed to return the full width of the canvas.
     * @return The width of the canvas in millimeters.
     */
    public double getPrintableWidth() {
        return mPrintableArea.getWidth();
    }

    /**
     * This method is supposed to return the full height of the canvas.
     * @return The height of the canvas in millimeters.
     */
    public double getPrintableHeight() {
        return mPrintableArea.getHeight();
    }

    public final double getPageWidth() {
        return mFormat.getProperty("page.width").toDouble();
    }

    public final double getPageHeight() {
        return mFormat.getProperty("page.height").toDouble();
    }

    /**
     * Get the diameter of a dot in mm.
     * @return The diameter of a dot in mm.
     */
    public final double getDotDiameter() {
        return mDotDiameter;
    }

    public final double getConstraintLeft() {
        return mConstraintLeft;
    }

    public final double getConstraintTop() {
        return mConstraintTop;
    }

    public abstract double getFullConstraintLeft();
    public abstract double getFullConstraintTop();

    /**
     * Get the number of pages in the canvas.
     * @return The number of pages.
     */
    public int getPageCount() {
        return mPageContainer.size();
    }

    /**
     * Get an Iterator for the PrintableData instances representing the canvas pages.
     * @return A {@link ListIterator}&lt;{@link PrintableData}&gt;.
     */
    public ListIterator<T> getPageIterator() {
        return mPageContainer.listIterator();
    }

    public final T getCurrentPage() {
        if (mPageContainer.size() < 1) {
            return getNewPage();
        }
        return mPageContainer.get(mPageContainer.size() - 1);
    }

    public abstract T getNewPage();

}
