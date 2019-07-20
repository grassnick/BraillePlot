package de.tudresden.inf.mci.brailleplot.rendering;

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

    private double mX, mY, mW, mH;

    public Rectangle(final double x, final double y, final double w, final double h) {
        setX(x);
        setY(y);
        setWidth(w);
        setHeight(h);
    }

    public Rectangle(final Rectangle rect) {
        setX(rect.getX());
        setY(rect.getY());
        setWidth(rect.getWidth());
        setHeight(rect.getHeight());
    }

    public Rectangle removeFromTop(final double height) throws OutOfSpaceException {
        Rectangle removedPartition = fromTop(height);
        double newY = (getY() + height);
        double newHeight = (getHeight() - height);
        setY(newY);
        setHeight(newHeight);
        return removedPartition;
    }

    public Rectangle removeFromBottom(final double height) throws OutOfSpaceException {
        Rectangle removedPartition = fromBottom(height);
        double newHeight = (getHeight() - height);
        setHeight(newHeight);
        return removedPartition;
    }

    public Rectangle removeFromLeft(final double width) throws OutOfSpaceException {
        Rectangle removedPartition = fromLeft(width);
        double newX = (getX() + width);
        double newWidth = (getWidth() - width);
        setX(newX);
        setWidth(newWidth);
        return removedPartition;
    }

    public Rectangle removeFromRight(final double width) throws OutOfSpaceException {
        Rectangle removedPartition = fromRight(width);
        double newWidth = (getWidth() - width);
        setWidth(newWidth);
        return removedPartition;
    }

    // Methods to getText a mRectangle partition

    public Rectangle fromTop(final double height) throws OutOfSpaceException {
        checkHeight(height);
        return new Rectangle(getX(), getY(), getWidth(), height);
    }
    public Rectangle fromLeft(final double width) throws OutOfSpaceException {
        checkWidth(width);
        return new Rectangle(getX(), getY(), width, getHeight());
    }
    public Rectangle fromBottom(final double height) throws OutOfSpaceException {
        checkHeight(height);
        double newY = (getY() + (getHeight() - height));
        return new Rectangle(getX(), newY, getWidth(), height);
    }
    public Rectangle fromRight(final double width) throws OutOfSpaceException {
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

    public double getX() {
        return mX;
    }

    public double getY() {
        return mY;
    }

    public double getWidth() {
        return mW;
    }

    public double getHeight() {
        return mH;
    }

    public double getRight() {
        return mX + mW;
    }
    public double getBottom() {
        return mY + mH;
    }

    public void setX(final double x) {
        mX = x;
    }

    public void setY(final double y) {
        mY = y;
    }

    public void setWidth(final double width) {
        if (width < 0) {
            throw new IllegalArgumentException("The width can't be negative.");
        }
        mW = width;
    }

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
        return new Rectangle(mX * xScale, mY * yScale, mW * xScale, mH * yScale);
    }

    /**
     * Returns a new rectangle representing the intersection of this rectangle with another rectangle.
     * @param otherRectangle The other rectangle to intersect with this.
     * @return New rectangle representing the intersection.
     */
    public Rectangle intersectedWith(final Rectangle otherRectangle) {
        double itsctX = max(getX(), otherRectangle.getX());
        double itsctY = max(getY(), otherRectangle.getY());
        double itsctB = min(getBottom(), otherRectangle.getBottom());
        double itsctR = min(getRight(), otherRectangle.getRight());
        return new Rectangle(itsctX, itsctY, max(0, itsctR - itsctX), max(0, itsctB - itsctY));
    }

    @Override
    public String toString() {
        return "x:" + getX() + ", y:" + getY() + ", w:" + getWidth() + ", h:" + getHeight();
    }

    // Wrapper to make it easy to read integer values from rectangle.

    public IntWrapper intWrapper() {
        return new IntWrapper(this);
    }

    public final class IntWrapper {

        private Rectangle mRectangle;

        public IntWrapper(final Rectangle rectangle) {
            this.mRectangle = Objects.requireNonNull(rectangle);
        }


        private int wrapInt(final double value) {
            return Math.toIntExact(round(value));
        }

        public int getX() {
            return wrapInt(mRectangle.getX());
        }

        public int getY() {
            return wrapInt(mRectangle.getY());
        }

        public int getWidth() {
            return wrapInt(mRectangle.getWidth());
        }

        public int getHeight() {
            return wrapInt(mRectangle.getHeight());
        }

        // QUESTION FOR REVIEW:
        // Does the -1 always make sense for integer coordinates?
        // The point is that on integer coords, if you have a rectangle at x,y of size 1x1, it 'ends' at x,y not x+1,y+1
        // The rectangle does not 'touch' the neighboring positions.
        public int getRight() {
            return wrapInt(mRectangle.getRight()) - 1;
        }
        public int getBottom() {
            return wrapInt(mRectangle.getBottom()) - 1;
        }

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
