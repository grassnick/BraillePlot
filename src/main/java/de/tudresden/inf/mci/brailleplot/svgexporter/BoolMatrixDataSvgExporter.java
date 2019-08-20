package de.tudresden.inf.mci.brailleplot.svgexporter;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import java.util.ArrayList;
import java.util.List;

/**
 * SVG exporter class that supports {@link RasterCanvas} as input.
 * @author Georg Graßnick
 * @version 2019.08.16
 */
public class BoolMatrixDataSvgExporter extends AbstractSvgExporter<RasterCanvas, MatrixData<Boolean>> {


    public BoolMatrixDataSvgExporter(final RasterCanvas rasterCanvas) {
        super(rasterCanvas);
        mSvgs = new ArrayList<>(mCanvas.getPageCount());
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
                if (mat.getValue(y, x)) {
                    int xPos = (int) (double) Math.round(xPositions.get(x)) * SCALE_FACTOR + mCanvas.getRasterConstraintLeft();
                    int yPos = (int) (double) Math.round(yPositions.get(y)) * SCALE_FACTOR + mCanvas.getRasterConstraintTop();
                    svg.drawOval(xPos, yPos, dotDiameter, dotDiameter);
                    mLogger.trace("Drew dot at position ({},{})", xPos, yPos);
                }
            }
        }
    }
}
