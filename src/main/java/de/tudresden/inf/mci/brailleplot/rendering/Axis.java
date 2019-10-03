package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.layout.Rectangle;

import java.util.Map;
import java.util.Objects;

/**
 * The representation of a visible axis with a line, tickmarks and labels.
 * @author Leonard Kupper
 * @version 2019.08.29
 */
public class Axis implements Renderable {

    private Type mType;
    private double mOriginX;
    private double mOriginY;
    private double mStepWidth;
    private double mTickSize;
    private Rectangle mBoundary;
    private Map<Integer, String> mLabels;

    /**
     * Constructor. Creates an instance of a new axis. Distances and sizes are stored as double and are used in a generic manner.
     * The interpretation of the values must be done by the rasterizer or plotter.
     * @param type Just changes the orientation of the axis. Can be either {@link Axis.Type#X_AXIS} or {@link Axis.Type#Y_AXIS}.
     * @param originX The x coordinate of the position where the axis line and the tickmark and label corresponding to the value '0' is placed.
     * @param originY The y coordinate of the position where the axis line and the tickmark and label corresponding to the value '0' is placed.
     * @param stepWidth The distance between two tickmarks on the axis in dots.
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

    /**
     * Get the type of the axis.
     * @return The axis type as {@link Axis.Type}.
     */
    public Type getType() {
        return mType;
    }

    /**
     * Set the type of the axis.
     * @param type The axis type as {@link Axis.Type}.
     */
    public void setType(final Type type) {
        mType = Objects.requireNonNull(type);
    }

    /**
     * Get the x position of the coordinate origin. The position is not to be mistaken with the coordinate. It
     * determines where on the canvas the axis should be positioned, not which x value is positioned at the y-axis.
     * @return The x position of the axis on canvas.
     */
    public double getOriginX() {
        return mOriginX;
    }

    /**
     * Set the x position of the coordinate origin. The position is not to be mistaken with the coordinate. It
     * determines where on the canvas the axis should be positioned, not which x value is positioned at the y-axis.
     * @param originX The x position of the axis on canvas.
     */
    public void setOriginX(final double originX) {
        mOriginX = originX;
    }

    /**
     * Get the y position of the coordinate origin. The position is not to be mistaken with the coordinate. It
     * determines where on the canvas the axis should be positioned, not which y value is positioned at the x-axis.
     * @return The y position of the axis on canvas.
     */
    public double getOriginY() {
        return mOriginY;
    }

    /**
     * Set the y position of the coordinate origin. The position is not to be mistaken with the coordinate. It
     * determines where on the canvas the axis should be positioned, not which y value is positioned at the x-axis.
     * @param originY The y position of the axis on canvas.
     */
    public void setOriginY(final double originY) {
        mOriginY = originY;
    }

    /**
     * Get the distance between neighboring axis tickmarks.
     * @return The tickmark distance.
     */
    public double getStepWidth() {
        return mStepWidth;
    }

    /**
     * Set the distance between neighboring axis tickmarks.
     * @param stepWidth The tickmark distance.
     */
    public void setStepWidth(final double stepWidth) {
        if (stepWidth <= 0) {
            throw new IllegalArgumentException("Axis step width can't be negative or zero.");
        }
        mStepWidth = stepWidth;
    }

    /**
     * Get the length of the axis tickmark lines. The values sign determines the tickmark orientation. A value of zero
     * indicates that no visible tickmarks are set.
     * @return The tickmark line length.
     */
    public double getTickSize() {
        return mTickSize;
    }

    /**
     * Set the length of the axis tickmark lines. The values sign determines the tickmark orientation. A value of zero
     * indicates that no visible tickmarks are set.
     * @param tickSize The tickmark line length.
     */
    public void setTickSize(final double tickSize) {
        mTickSize = tickSize;
    }

    /**
     * Get the labels that are drawn next to the axis tickmarks as {@link Map}. The key determines the position of the
     * label (positive values = labels toward positive value range, 0 = at coordinate origin, negative values = labels
     * toward negative value range). The value is a String representing the label text.
     * Not every position must be supplied with a label.
     * @return A {@link Map} containing all labels.
     */
    public Map<Integer, String> getLabels() {
        return mLabels;
    }
    /**
     * Set the labels that are drawn next to the axis tickmarks as {@link Map}. The key determines the position of the
     * label (positive values = labels toward positive value range, 0 = at coordinate origin, negative values = labels
     * toward negative value range). The value is a String representing the label text.
     * Not every position must be supplied with a label.
     * @param labels A {@link Map} containing all labels.
     */
    public void setLabels(final Map<Integer, String> labels) {
        mLabels = Objects.requireNonNull(labels);
    }

    /**
     * Check whether any labels are set. This should be done prior to trying accessing the labels via {@link #getLabels()}.
     * @return True if labels are set, else False.
     */
    public boolean hasLabels() {
        return !Objects.isNull(mLabels);
    }

    /**
     * Get the area of the canvas on which the axis is to be drawn.
     * @return A {@link Rectangle} representing the area.
     */
    public Rectangle getBoundary() {
        return Objects.requireNonNull(mBoundary);
    }

    /**
     * Set the area of the canvas on which the axis is to be drawn.
     * Please note that this only limits the length of the axis in its respective orientation.
     * @param boundary A {@link Rectangle} representing the area.
     */
    public void setBoundary(final Rectangle boundary) {
        mBoundary = Objects.requireNonNull(boundary);
    }

    /**
     * Check whether a boundary is set.
     * @return True if boundary is set, else False.
     */
    public boolean hasBoundary() {
        return !Objects.isNull(mBoundary);
    }

    /**
     * Representation of the axis type / orientation.
     */
    enum Type {
        X_AXIS, Y_AXIS;
    }
}
