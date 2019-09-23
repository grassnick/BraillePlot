package de.tudresden.inf.mci.brailleplot.svgexporter;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import java.awt.Color;
import java.util.List;

/**
 * SVG exporter class that supports {@link RasterCanvas} as input.
 * @author Georg Graßnick
 * @version 2019.08.30
 */
public class BoolMatrixDataSvgExporter extends AbstractSvgExporter<RasterCanvas, MatrixData<Boolean>> {

    private static final int EMPTY_DOT_COLOR_GRAY = 225;
    private static final Color EMPTY_DOT_COLOR = new Color(EMPTY_DOT_COLOR_GRAY, EMPTY_DOT_COLOR_GRAY, EMPTY_DOT_COLOR_GRAY);
    private static final Color FULL_DOT_COLOR = Color.BLACK;


    public BoolMatrixDataSvgExporter(final RasterCanvas rasterCanvas) {
        super(rasterCanvas);
    }

    @Override
    protected void renderPage(final MatrixData<Boolean> mat, final int dataIndex) {
        List<Double> xPositions = mCanvas.getXPositions();
        List<Double> yPositions = mCanvas.getYPositions();
        int dotDiameter = (int) mCanvas.getDotDiameter() * SCALE_FACTOR;
        mLogger.trace("Dot diameter: {}", dotDiameter);
        SVGGraphics2D svg = mSvgs.get(dataIndex);

        for (int y = 0; y < mat.getRowCount(); y++) {
            for (int x = 0; x < mat.getColumnCount(); x++) {
                int xPos = (int) Math.round((xPositions.get(x) + mCanvas.getFullConstraintLeft() - (double) dotDiameter / 2) * SCALE_FACTOR);
                int yPos = (int) Math.round((yPositions.get(y) + mCanvas.getFullConstraintTop() - (double) dotDiameter / 2) * SCALE_FACTOR);
                if (mat.getValue(y, x)) {
                    svg.setColor(FULL_DOT_COLOR);
                    mLogger.trace("Drew dot at position ({},{})", xPos, yPos);
                } else {
                    svg.setColor(EMPTY_DOT_COLOR);
                }

                svg.drawOval(xPos, yPos, dotDiameter, dotDiameter);
            }
        }
    }
}
