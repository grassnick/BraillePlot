package de.tudresden.inf.mci.brailleplot.rendering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static java.lang.Math.round;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * Represents a rectangle that can be continuously divided into partitions. Can be used for doing chart layout and as
 * boundary of renderable objects.
 * @author Leonard Kupper
 * @version 2019.07.12
 */
public class Rectangle {

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    private double mX, mY, mW, mH;

    /**
     * Constructor. Creates a rectangle with given position and size.
     * @param x The x coordinate of the upper left corner.
     * @param y The y coordinate of the upper left corner.
     * @param w The width of the rectangle, meaning its rightward expanse.
     * @param h The height of the rectangle, meaning its downward expanse.
     */
    public Rectangle(final double x, final double y, final double w, final double h) {
        setX(x);
        setY(y);
        setWidth(w);
        setHeight(h);
        mLogger.trace("Created new rectangle: {}", this);
    }

    /**
     * Copy constructor. Creates a copy of a rectangle.
     * @param rect The rectangle to be copied.
     */
    public Rectangle(final Rectangle rect) {
        setX(rect.getX());
        setY(rect.getY());
        setWidth(rect.getWidth());
        setHeight(rect.getHeight());
        mLogger.trace("Copied rectangle: {}", this);
    }

    /**
     * Removes a partition from the rectangles top and returns it.
     * @param height The height of the partition that will be removed.
     * @return A rectangle representing the cropped partition.
     * @throws OutOfSpaceException If the requested partition is greater than the underlying rectangle itself.
     */
    public Rectangle removeFromTop(final double height) throws OutOfSpaceException {
        Rectangle removedPartition = fromTop(height);
        double newY = (getY() + height);
        double newHeight = (getHeight() - height);
        setY(newY);
        setHeight(newHeight);
        return removedPartition;
    }

    /**
     * Removes a partition from the rectangles bottom and returns it.
     * @param height The height of the partition that will be removed.
     * @return A rectangle representing the cropped partition.
     * @throws OutOfSpaceException If the requested partition is greater than the underlying rectangle itself.
     */
    public Rectangle removeFromBottom(final double height) throws OutOfSpaceException {
        Rectangle removedPartition = fromBottom(height);
        double newHeight = (getHeight() - height);
        setHeight(newHeight);
        return removedPartition;
    }

    /**
     * Removes a partition from the rectangles left side and returns it.
     * @param width The width of the partition that will be removed.
     * @return A rectangle representing the cropped partition.
     * @throws OutOfSpaceException If the requested partition is greater than the underlying rectangle itself.
     */
    public Rectangle removeFromLeft(final double width) throws OutOfSpaceException {
        Rectangle removedPartition = fromLeft(width);
        double newX = (getX() + width);
        double newWidth = (getWidth() - width);
        setX(newX);
        setWidth(newWidth);
        return removedPartition;
    }

    /**
     * Removes a partition from the rectangles right side and returns it.
     * @param width The width of the partition that will be removed.
     * @return A rectangle representing the cropped partition.
     * @throws OutOfSpaceException If the requested partition is greater than the underlying rectangle itself.
     */
    public Rectangle removeFromRight(final double width) throws OutOfSpaceException {
        Rectangle removedPartition = fromRight(width);
        double newWidth = (getWidth() - width);
        setWidth(newWidth);
        return removedPartition;
    }

    // Methods to get a rectangle partition

    /**
     * Gets a partition from the rectangles top without removing it from the original instance.
     * @param height The height of the selected partition.
     * @return A rectangle representing the selected partition.
     * @throws OutOfSpaceException If the requested partition is greater than the underlying rectangle itself.
     */
    public Rectangle fromTop(final double height) throws OutOfSpaceException {
        mLogger.trace("Getting partition of height {} from top of {}", height, this);
        checkHeight(height);
        return new Rectangle(getX(), getY(), getWidth(), height);
    }

    /**
     * Gets a partition from the rectangles left side without removing it from the original instance.
     * @param width The width of the selected partition.
     * @return A rectangle representing the selected partition.
     * @throws OutOfSpaceException If the requested partition is greater than the underlying rectangle itself.
     */
    public Rectangle fromLeft(final double width) throws OutOfSpaceException {
        mLogger.trace("Getting partition of width {} from left side of {}", width, this);
        checkWidth(width);
        return new Rectangle(getX(), getY(), width, getHeight());
    }

    /**
     * Gets a partition from the rectangles bottom without removing it from the original instance.
     * @param height The height of the selected partition.
     * @return A rectangle representing the selected partition.
     * @throws OutOfSpaceException If the requested partition is greater than the underlying rectangle itself.
     */
    public Rectangle fromBottom(final double height) throws OutOfSpaceException {
        mLogger.trace("Getting partition of height {} from bottom of {}", height, this);
        checkHeight(height);
        double newY = (getY() + (getHeight() - height));
        return new Rectangle(getX(), newY, getWidth(), height);
    }

    /**
     * Gets a partition from the rectangles right side without removing it from the original instance.
     * @param width The width of the selected partition.
     * @return A rectangle representing the selected partition.
     * @throws OutOfSpaceException If the requested partition is greater than the underlying rectangle itself.
     */
    public Rectangle fromRight(final double width) throws OutOfSpaceException {
        mLogger.trace("Getting partition of width {} from right side of {}", width, this);
        checkWidth(width);
        double newX = (getX() + (getWidth() - width));
        return new Rectangle(newX, getY(), width, getHeight());
    }

    // Help methods for validity check of requested partition

    private void checkHeight(final double h) throws OutOfSpaceException {
        if (h > getHeight()) {
            throw new OutOfSpaceException("The rectangle partition height cannot be greater than its parent rectangle height."
                    + "(" + h + ">" + getHeight() + ")");
        }
    }
    private void checkWidth(final double w) throws OutOfSpaceException {
        if (w > getWidth()) {
            throw new OutOfSpaceException("The rectangle partition width cannot be greater than its parent rectangle width."
                    + "(" + w + ">" + getWidth() + ")");
        }
    }

    // Getters for edge positions and size

    /**
     * Gets the rectangles x position.
     * @return The x coordinate of the upper left corner.
     */
    public double getX() {
        return mX;
    }

    /**
     * Gets the rectangles y position.
     * @return The y coordinate of the upper left corner.
     */
    public double getY() {
        return mY;
    }

    /**
     * Gets the rectangles width.
     * @return The distance between the rectangles left and right edge.
     */
    public double getWidth() {
        return mW;
    }

    /**
     * Gets the rectangles height.
     * @return The distance between the rectangles top and bottom edge.
     */
    public double getHeight() {
        return mH;
    }

    /**
     * Gets the rectangles right edges position.
     * @return The x coordinate of the lower right corner.
     */
    public double getRight() {
        return mX + mW;
    }

    /**
     * Gets the rectangles bottom edges position.
     * @return The y coordinate of the lower right corner.
     */
    public double getBottom() {
        return mY + mH;
    }

    /**
     * Sets a new x position for the rectangle.
     * @param x The new x coordinate of the upper left corner.
     */
    public void setX(final double x) {
        mX = x;
    }

    /**
     * Sets a new y position for the rectangle.
     * @param y The new y coordinate of the upper left corner.
     */
    public void setY(final double y) {
        mY = y;
    }

    /**
     * Sets a new width for the rectangle.
     * @param width The new width value.
     */
    public void setWidth(final double width) {
        if (width < 0) {
            throw new IllegalArgumentException("The width can't be negative.");
        }
        mW = width;
    }

    /**
     * Sets a new height for the rectangle.
     * @param height The new height value.
     */
    public void setHeight(final double height) {
        if (height < 0) {
            throw new IllegalArgumentException("The height can't be negative.");
        }
        mH = height;
    }

    /**
     * Returns a scaled version of the original rectangle.
     * @param xScale The x-axis scale factor
     * @param yScale The y-axis scale factor
     * @return New rectangle with scaled position and size.
     */
    public Rectangle scaledBy(final double xScale, final double yScale) {
        mLogger.trace("Scaling rectangle {} by {}x{}", this, xScale, yScale);
        return new Rectangle(mX * xScale, mY * yScale, mW * xScale, mH * yScale);
    }

    /**
     * Returns a new rectangle representing the intersection of this rectangle with another rectangle.
     * @param otherRectangle The other rectangle to intersect with this.
     * @return New rectangle representing the intersection.
     */
    public Rectangle intersectedWith(final Rectangle otherRectangle) {
        mLogger.trace("Intersecting rectangles: {} & {}", this, otherRectangle);
        double itsctX = max(getX(), otherRectangle.getX());
        double itsctY = max(getY(), otherRectangle.getY());
        double itsctB = min(getBottom(), otherRectangle.getBottom());
        double itsctR = min(getRight(), otherRectangle.getRight());
        return new Rectangle(itsctX, itsctY, max(0, itsctR - itsctX), max(0, itsctB - itsctY));
    }

    /**
     * Returns a translated copy of this rectangle.
     * @param alongX The distance to move the copy along x axis.
     * @param alongY The distance to move the copy along y axis.
     * @return A new rectangle representing a translated copy of this rectangle.
     */
    public Rectangle translatedBy(final double alongX, final double alongY) {
        mLogger.trace("Translating rectangle {} along {},{}", this, alongX, alongY);
        return new Rectangle(getX() + alongX, getY() + alongY, getWidth(), getHeight());
    }

    @Override
    public String toString() {
        return "x:" + getX() + ", y:" + getY() + ", w:" + getWidth() + ", h:" + getHeight();
    }

    // Wrapper to make it easy to read integer values from rectangle.

    /**
     * Retrieves a proxy object as integer coordinate representation of the rectangle. This is meant as a shortcut
     * for otherwise frequent int-casting when using rectangles on a integer based coordinate system.
     * @return An instance of {@link IntWrapper} proxying this rectangle.
     */
    public IntWrapper intWrapper() {
        return new IntWrapper(this);
    }

    /**
     * Wrapper of rectangle for integer coordinates.
     * @author Leonard Kupper
     * @version 2019.07.22
     */
    public final class IntWrapper {

        private Rectangle mRectangle;

        /**
         * Constructor. Creates a wrapper as proxy object for an integer coordinate representation of the given rectangle.
         * @param rectangle The rectangle to be wrapped.
         */
        public IntWrapper(final Rectangle rectangle) {
            this.mRectangle = Objects.requireNonNull(rectangle);
        }


        private int wrapInt(final double value) {
            return Math.toIntExact(round(value));
        }

        /**
         * Gets the rectangles x position.
         * @return The x coordinate of the upper left corner.
         */
        public int getX() {
            return wrapInt(mRectangle.getX());
        }

        /**
         * Gets the rectangles y position.
         * @return The y coordinate of the upper left corner.
         */
        public int getY() {
            return wrapInt(mRectangle.getY());
        }

        /**
         * Gets the rectangles width.
         * @return The distance between the rectangles left and right edge.
         */
        public int getWidth() {
            return wrapInt(mRectangle.getWidth());
        }

        /**
         * Gets the rectangles height.
         * @return The distance between the rectangles top and bottom edge.
         */
        public int getHeight() {
            return wrapInt(mRectangle.getHeight());
        }

        /**
         * Gets the rectangles right edges position (<b>Important:</b> The IntWrapper treats the rectangle as
         * representation of a 'whole' area composed of single countable units (e.g. dots or cells) so this method will
         * return the position of the rightmost contained coordinate, which is x+width-1)
         * @return The x coordinate of the inner contained right edge.
         */
        public int getRight() {
            return wrapInt(mRectangle.getRight()) - 1;
        }

        /**
         * Gets the rectangles bottom edges position (<b>Important:</b> The IntWrapper treats the rectangle as
         * representation of a 'whole' area composed of single countable units (e.g. dots or cells) so this method will
         * return the position of the bottommost contained coordinate, which is y+height-1)
         * @return The y coordinate of the inner contained bottom edge.
         */
        public int getBottom() {
            return wrapInt(mRectangle.getBottom()) - 1;
        }

        /**
         * Get the original rectangle, wrapped by this IntWrapper.
         * @return The wrapped instance of {@link Rectangle}.
         */
        public Rectangle getRectangle() {
            return mRectangle;
        }

        @Override
        public String toString() {
            return "x:" + getX() + ", y:" + getY() + ", w:" + getWidth() + ", h:" + getHeight();
        }
    }

    /**
     * Exception that indicates that an operation on the rectangle has failed because there was too few space available.
     * Typically this is caused by trying to get a partition of the rectangle which is bigger than its parent rectangle itself.
     * @author Leonard Kupper
     * @version 2019.07.12
     */
    public class OutOfSpaceException extends Exception {

        public OutOfSpaceException() { }

        public OutOfSpaceException(final String message) {
            super(message);
        }

        public OutOfSpaceException(final Throwable cause) {
            super(cause);
        }

        public OutOfSpaceException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

}
