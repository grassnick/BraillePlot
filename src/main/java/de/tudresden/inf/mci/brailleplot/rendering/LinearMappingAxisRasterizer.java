package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import static de.tudresden.inf.mci.brailleplot.rendering.Axis.Type.X_AXIS;
import static de.tudresden.inf.mci.brailleplot.rendering.Axis.Type.Y_AXIS;
import static java.lang.Math.abs;

/**
 * A rasterizer for instances of {@link Axis} which is using a simple approach by linear mapping.
 * @author Leonard Kupper
 * @version 2019.07.20
 */

public class LinearMappingAxisRasterizer implements Rasterizer<Axis> {

    private BrailleTextRasterizer mTextRasterizer = new BrailleTextRasterizer();
    private RasterCanvas mCanvas;

    /**
     * Rasterizes a {@link Axis} instance onto a {@link RasterCanvas}.
     * @param axis A instance of {@link Axis} representing the visual diagram axis.
     * @param canvas A instance of {@link RasterCanvas} representing the target for the rasterizer output.
     * @throws InsufficientRenderingAreaException If too few space is available on the {@link RasterCanvas}
     * to display the given axis.
     */
    @Override
    public void rasterize(final Axis axis, final RasterCanvas canvas) throws InsufficientRenderingAreaException {

        mCanvas = canvas;
        MatrixData<Boolean> data = mCanvas.getCurrentPage();

        int dotX, startY, endY, dotY, startX, endX;
        int stepWidth = (int) axis.getStepWidth();
        int tickSize = (int) axis.getTickSize();
        boolean setTicks = (abs(tickSize) > 0);
        boolean hasLabels = axis.hasLabels();

        if (axis.getType() == X_AXIS) {
            Rectangle bound;
            dotY = (int) axis.getOriginY();
            if (axis.hasBoundary()) {
                bound = axis.getBoundary();
            } else {
                bound = mCanvas.getDotRectangle();
            }
            startX = bound.intWrapper().getX();
            endX = bound.intWrapper().getRight();
            Rasterizer.fill(startX, dotY, endX, dotY, data, true);

            if (setTicks) {
                int i;
                startY = dotY;
                endY = dotY + tickSize;
                i = 0;
                for (dotX = (int) axis.getOriginX(); dotX <= endX; dotX += stepWidth) {
                    Rasterizer.fill(dotX, startY, dotX, endY, data, true);
                    // TODO: refactor to have labeling functionality in extra method.
                    if (hasLabels && axis.getLabels().containsKey(i)) {
                        String label = axis.getLabels().get(i);
                        Rectangle labelArea = new Rectangle(dotX - 1, endY + 1, stepWidth, mCanvas.getCellHeight());
                        mTextRasterizer.rasterize(new BrailleText(label, labelArea), mCanvas);
                    }
                    i++;
                }
                i = -1;
                for (dotX = (int) axis.getOriginX() - stepWidth; dotX >= startX; dotX -= stepWidth) {
                    Rasterizer.fill(dotX, startY, dotX, endY, data, true);
                    if (hasLabels && axis.getLabels().containsKey(i)) {
                        String label = axis.getLabels().get(i);
                        Rectangle labelArea = new Rectangle(dotX - 1, endY + 1, stepWidth, mCanvas.getCellHeight());
                        mTextRasterizer.rasterize(new BrailleText(label, labelArea), mCanvas);
                    }
                    i--;
                }
            }
        }

        if (axis.getType() == Y_AXIS) {
            Rectangle bound;
            dotX = (int) axis.getOriginX();
            if (axis.hasBoundary()) {
                bound = axis.getBoundary();
            } else {
                bound = mCanvas.getDotRectangle();
            }
            startY = bound.intWrapper().getY();
            endY = bound.intWrapper().getBottom();
            Rasterizer.fill(dotX, startY, dotX, endY, data, true);

            if (setTicks) {
                int i;
                startX = dotX;
                endX = dotX + tickSize;
                i = 0;
                for (dotY = (int) axis.getOriginY(); dotY <= endY; dotY += stepWidth) {
                    Rasterizer.fill(startX, dotY, endX, dotY, data, true);
                    /*
                    if (hasLabels && axis.getLabels().containsKey(i)) {
                        String label = axis.getLabels().get(i);
                        Rectangle labelArea = new Rectangle(endX + Integer.signum(tickSize), dotY, stepWidth, mCanvas.getCellHeight());
                        mTextRasterizer.rasterize(new BrailleText(label, labelArea), mCanvas);
                    }
                    */
                    i++;
                }
                i = -1;
                for (dotY = (int) axis.getOriginY() - stepWidth; dotY >= startY; dotY -= stepWidth) {
                    Rasterizer.fill(startX, dotY, endX, dotY, data, true);
                    /*
                    if (hasLabels && axis.getLabels().containsKey(i)) {
                        String label = axis.getLabels().get(i);
                        Rectangle labelArea = new Rectangle(endX + Integer.signum(tickSize), dotY, stepWidth, mCanvas.getCellHeight());
                        mTextRasterizer.rasterize(new BrailleText(label, labelArea), mCanvas);
                    }
                    */
                    i--;
                }
            }
        }
    }
}
