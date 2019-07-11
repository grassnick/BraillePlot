package de.tudresden.inf.mci.brailleplot.rendering;

import java.util.Objects;

import static java.lang.Math.round;

public class Rectangle {

    private double mX, mY, mW, mH;

    public Rectangle(double x, double y, double w, double h) {
        setX(x);
        setY(y);
        setWidth(w);
        setHeight(h);
    }

    public Rectangle(Rectangle rect) {
        setX(rect.getX());
        setY(rect.getY());
        setWidth(rect.getWidth());
        setHeight(rect.getHeight());
    }
    
    public Rectangle removeFromTop(double height) {
        Rectangle removedPartition = fromTop(height);
        double newY = (getY() + height);
        double newHeight = (getHeight() - height);
        setY(newY);
        setHeight(newHeight);
        return removedPartition;
    }

    public Rectangle removeFromBottom(double height) {
        Rectangle removedPartition = fromBottom(height);
        double newHeight = (getHeight() - height);
        setHeight(newHeight);
        return removedPartition;
    }

    public Rectangle removeFromLeft(double width) {
        Rectangle removedPartition = fromLeft(width);
        double newX = (getX() + width);
        double newWidth = (getWidth() - width);
        setX(newX);
        setWidth(newWidth);
        return removedPartition;
    }

    public Rectangle removeFromRight(double width) {
        Rectangle removedPartition = fromRight(width);
        double newWidth = (getWidth() - width);
        setWidth(newWidth);
        return removedPartition;
    }

    // Methods to getText a mRectangle partition

    public Rectangle fromTop(double height) {
        checkHeight(height);
        return new Rectangle(getX(), getY(), getWidth(), height);
    }
    public Rectangle fromLeft(double width) {
        checkWidth(width);
        return new Rectangle(getX(), getY(), width, getHeight());
    }
    public Rectangle fromBottom(double height) {
        checkHeight(height);
        double newY = (getY() + (getHeight() - height));
        return new Rectangle(getX(), newY, getWidth(), height);
    }
    public Rectangle fromRight(double width) {
        checkWidth(width);
        double newX = (getX() + (getWidth() - width));
        return new Rectangle(newX, getY(), width, getHeight());
    }

    // Help methods for validity check of requested partition

    private void checkHeight(double h) {
        if (h > getHeight()) {
            throw new IllegalArgumentException("The new rectangles height cannot be greater than this rectangles height." +
                    "(" + h + ">" + getHeight() + ")");
        }
    }
    private void checkWidth(double w) {
        if (w > getWidth()) {
            throw new IllegalArgumentException("The new rectangles width cannot be greater than this rectangles width." +
                    "(" + w + ">" + getWidth() + ")");
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
        return mX + mW - 1;
    }
    public double getBottom() {
        return mY + mH - 1;
    }

    public void setX(double x) {
        mX = x;
    }

    public void setY(double y) {
        mY = y;
    }

    public void setWidth(double width) {
        if (width < 0) {
            throw new IllegalArgumentException("The width can't be negative.");
        }
        mW = width;
    }

    public void setHeight(double height) {
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

    @Override
    public String toString() {
        return "x:" + getX() + ", y:" + getY() + ", w:" + getWidth() + ", h:" + getHeight();
    }

    // Wrapper to make it easy to read integer values from rectangle.

    public IntWrapper intWrapper() {
        return new IntWrapper(this);
    }

    public class IntWrapper {

        private Rectangle mRectangle;

        public IntWrapper(Rectangle rectangle) {
            this.mRectangle = Objects.requireNonNull(rectangle);
        }


        private int wrapInt(double value) {
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

        public int getRight() {
            return wrapInt(mRectangle.getRight());
        }
        public int getBottom() {
            return wrapInt(mRectangle.getBottom());
        }

        public Rectangle getRectangle() {
            return mRectangle;
        }

        @Override
        public String toString() {
            return "x:" + getX() + ", y:" + getY() + ", w:" + getWidth() + ", h:" + getHeight();
        }
    }

}
