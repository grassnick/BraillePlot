package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import static de.tudresden.inf.mci.brailleplot.rendering.Axis.Type.X_AXIS;
import static de.tudresden.inf.mci.brailleplot.rendering.Axis.Type.Y_AXIS;
import static java.lang.Integer.signum;

/**
 * A rasterizer for instances of {@link Axis} which is using a simple approach by linear mapping.
 * @author Leonard Kupper
 * @version 2019.08.29
 */

public class LinearMappingAxisRasterizer implements Rasterizer<Axis> {

    private LiblouisBrailleTextRasterizer mTextRasterizer;
    private RasterCanvas mCanvas;
    private MatrixData<Boolean> mPage;

    private Axis mAxis;
    private int mStepWidth, mTickSize, mOriginX, mOriginY;
    private boolean mHasLabels, mTickDetermination;
    private Rectangle mBound;

    /**
     * Rasterizes a {@link Axis} instance onto a {@link RasterCanvas}.
     * @param axis A instance of {@link Axis} representing the visual diagram axis.
     * @param canvas A instance of {@link RasterCanvas} representing the target for the rasterizer output.
     * @throws InsufficientRenderingAreaException If too few space is available on the {@link RasterCanvas}
     * to display the given axis.
     */
    @Override
    public void rasterize(final Axis axis, final RasterCanvas canvas) throws InsufficientRenderingAreaException {

        mTextRasterizer = new LiblouisBrailleTextRasterizer(canvas.getPrinter());
        mCanvas = canvas;
        mPage = mCanvas.getCurrentPage();

        mAxis = axis;
        mOriginX = (int) axis.getOriginX();
        mOriginY = (int) axis.getOriginY();
        mStepWidth = (int) axis.getStepWidth();
        mTickSize = (int) axis.getTickSize();
        mHasLabels = axis.hasLabels();
        if (axis.hasBoundary()) {
            mBound = axis.getBoundary();
        } else {
            mBound = mCanvas.getDotRectangle();
        }

        if (axis.getType() == X_AXIS) {
            drawXAxis();
        } else if (axis.getType() == Y_AXIS) {
            drawYAxis();
        } else {
            throw new IllegalArgumentException("Unknown axis type: " + axis.getType());
        }

    }

    private void drawXAxis() throws InsufficientRenderingAreaException {
        // draw axis line
        Rectangle axisLineDotArea = new Rectangle(mBound.getX(), mOriginY, mBound.getWidth(), 1);
        Rasterizer.rectangle(axisLineDotArea, mPage, true);
        // draw labels and ticks
        int positiveUnits = (int) Math.floor((axisLineDotArea.intWrapper().getRight() - mOriginX) / mStepWidth);
        int negativeUnits = (int) Math.floor((axisLineDotArea.intWrapper().getX() - mOriginX) / mStepWidth);
        for (int i = 0; i <= positiveUnits; i++) {
            tryToDrawLabel(i);
        }
        for (int i = -1; i >= negativeUnits; i--) {
            tryToDrawLabel(i);
        }
    }

    private void drawYAxis() throws InsufficientRenderingAreaException {
        // draw axis line
        Rectangle axisLineDotArea = new Rectangle(mOriginX, mBound.getY(), 1, mBound.getHeight());
        Rasterizer.rectangle(axisLineDotArea, mPage, true);
        // draw labels and ticks
        int positiveUnits = (int) Math.floor((mOriginY - axisLineDotArea.intWrapper().getY()) / mStepWidth);
        int negativeUnits = (int) Math.floor((mOriginY - axisLineDotArea.intWrapper().getBottom()) / mStepWidth);
        for (int i = 0; i <= positiveUnits; i++) {
            tryToDrawLabel(i);
        }
        for (int i = -1; i >= negativeUnits; i--) {
            tryToDrawLabel(i);
        }
    }

    /**
     * Tries to draw a label at given index and draws a tick when needed.
     * @param labelIndex The index of the label which the method will try to draw.
     * @return A boolean value determining whether a tick has been set. Ticks will be drawn whenever a label can be set
     *         at the respective positions or when no labels are defined.
     */
    private boolean tryToDrawLabel(final int labelIndex)
            throws InsufficientRenderingAreaException {

        Rectangle tickmarkDotArea;
        int dotX, dotY, labelCellX, labelCellY;
        dotX = mOriginX;
        dotY = mOriginY;

        // check preconditions
        if (mHasLabels && mAxis.getLabels().containsKey(labelIndex)) {
            // get label text
            String labelText = mAxis.getLabels().get(labelIndex);
            int stringLength = mTextRasterizer.getBrailleStringLength(labelText);

            // determine area to write the label text on
            int xPad, yPad;
            Rectangle labelCellArea;
            int labelOffset = signum(mTickSize); // is the label above or below the tickmark?
            if (mAxis.getType() == X_AXIS) {
                dotX += labelIndex * mStepWidth;
                labelCellX = mCanvas.getCellXFromDotX(dotX);
                labelCellY = mCanvas.getCellYFromDotY(dotY + mTickSize) + labelOffset;
                labelCellArea = new Rectangle(labelCellX - (stringLength / 2), labelCellY, stringLength, 1);
                xPad = 1;
                yPad = 0;
            } else if (mAxis.getType() == Y_AXIS) {
                dotY -= labelIndex * mStepWidth;
                labelCellX = mCanvas.getCellXFromDotX(dotX + (mTickSize + labelOffset)) + labelOffset;
                labelCellY = mCanvas.getCellYFromDotY(dotY);
                if (mTickSize < 0) {
                    // Calculate x position of first character in right aligned text
                    labelCellArea = new Rectangle(labelCellX - (stringLength - 1), labelCellY, stringLength, 1);
                } else {
                    labelCellArea = new Rectangle(labelCellX, labelCellY, stringLength, 1);
                }
                xPad = 0;
                yPad = 0;
            } else {
                return false;
            }

            // try to draw label (depending on available space)
            if (testCellsEmpty(labelCellArea, xPad, yPad)) {
                mTextRasterizer.rasterize(new BrailleText(labelText, mCanvas.toDotRectangle(labelCellArea)), mCanvas);
            } else {
                return false;
            }
        }

        // draw a tickmark
        if (mAxis.getType() == X_AXIS) {
            tickmarkDotArea = new Rectangle(dotX, dotY, 1, Math.abs(mTickSize) + 1);
            if (mTickSize < 0) {
                tickmarkDotArea = tickmarkDotArea.translatedBy(0, mTickSize);
            }
        } else if (mAxis.getType() == Y_AXIS) {
            tickmarkDotArea = new Rectangle(dotX, dotY, Math.abs(mTickSize) + 1, 1);
            if (mTickSize < 0) {
                tickmarkDotArea = tickmarkDotArea.translatedBy(mTickSize, 0);
            }
        } else {
            return false;
        }
        Rasterizer.rectangle(tickmarkDotArea, mPage, true);
        return true;
    }

    private boolean testCellsEmpty(final Rectangle cellArea, final int xPad, final int yPad) {
        Rectangle paddedCellArea = cellArea.translatedBy(-1 * xPad, -1 * yPad);
        paddedCellArea.setWidth(paddedCellArea.getWidth() + 2 * xPad);
        paddedCellArea.setHeight(paddedCellArea.getHeight() + 2 * yPad);
        Rectangle testDotArea = mCanvas.toDotRectangle(paddedCellArea);
        for (int x = testDotArea.intWrapper().getX(); x <= testDotArea.intWrapper().getRight(); x++) {
            for (int y = testDotArea.intWrapper().getY(); y <= testDotArea.intWrapper().getBottom(); y++) {
                if ((mPage.getRowCount() <= y) || (mPage.getColumnCount() <= x)
                        || (y < 0) || (x < 0)
                        || mPage.getValue(y, x)) {
                    return false;
                }
            }
        }
        return true;
    }
}
