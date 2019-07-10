package de.tudresden.inf.mci.brailleplot.rendering;

import java.util.Map;
import java.util.Objects;

/**
 * The representation of a visible axis with a line, tickmarks and labels.
 *  @version 2019.07.09
 *  @author Leonard Kupper
 */
public class Axis implements Renderable {

    private Type mType;
    private double mOriginX;
    private double mOriginY;
    private double mStepWidth;
    private double mTickSize;
    private Rectangle mBoundary;
    private Map<Integer, String> mLabels;

    private boolean mSetTicks;

    /**
     * Constructor. Creates an instance of a new axis. Distances and sizes are stored as double and are used in a generic manner.
     * The interpretation of the values must be done by the rasterizer or plotter.
     * @param type Just changes the orientation of the axis. Can be either {@link Axis.Type#X_AXIS} or {@link Axis.Type#Y_AXIS}.
     * @param originX The x coordinate of the position where the axis line and the tickmark and label corresponding to the value '0' is placed.
     * @param originY The y coordinate of the position where the axis line and the tickmark and label corresponding to the value '0' is placed.
     * @param stepWidth The distance between two tickmarks on the axis.
     * @param tickSize The size and orientation of the tickmarks. The absolute value controls the length, the sign controls on which side they are displayed.
     */
    public Axis(final Type type, final double originX, final double originY, final double stepWidth,
                final double tickSize) {
        setType(type);
        setOriginX(originX);
        setOriginY(originY);
        setStepWidth(stepWidth);
        setTickSize(tickSize);
    }

    public Type getType() {
        return mType;
    }
    public void setType(final Type type) {
        mType = Objects.requireNonNull(type);
    }

    public double getOriginX() {
        return mOriginX;
    }
    public void setOriginX(final double originX) {
        mOriginX = originX;
    }

    public double getOriginY() {
        return mOriginY;
    }
    public void setOriginY(final double originY) {
        mOriginY = originY;
    }

    public double getStepWidth() {
        return mStepWidth;
    }
    public void setStepWidth(final double stepWidth) {
        if (stepWidth <= 0) {
            throw new IllegalArgumentException("Axis step width can't be negative or zero.");
        }
        mStepWidth = stepWidth;
    }

    public double getTickSize() {
        return mTickSize;
    }
    public void setTickSize(final double tickSize) {
        mTickSize = tickSize;
    }

    public Map<Integer, String> getLabels() {
        return mLabels;
    }
    public void setLabels(Map<Integer, String> labels) {
        mLabels = Objects.requireNonNull(labels);
    }
    public boolean hasLabels() {
        return !Objects.isNull(mLabels);
    }

    public Rectangle getBoundary() {
        return Objects.requireNonNull(mBoundary);
    }
    public void setBoundary(Rectangle boundary) {
        mBoundary = Objects.requireNonNull(boundary);
    }
    public boolean hasBoundary() {
        return !Objects.isNull(mBoundary);
    }

    enum Type {
        X_AXIS, Y_AXIS;
    }
}
