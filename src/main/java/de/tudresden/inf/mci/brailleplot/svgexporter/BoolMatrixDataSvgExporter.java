package de.tudresden.inf.mci.brailleplot.svgexporter;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.rendering.RasterCanvas;
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
        int dotDiameter = (int) mCanvas.getDotDiameter();
        final int mult = 4;

        for (int y = 0; y < mat.getRowCount(); y++) {
            for (int x = 0; x < mat.getColumnCount(); x++) {
                if (mat.getValue(y, x)) {
                    int xPos = (int) (double) xPositions.get(x);
                    int yPos = (int) (double) yPositions.get(y);
                    mSvgs.get(dataIndex).drawOval(xPos * mult, yPos * mult, dotDiameter * mult, dotDiameter * mult);
                }
            }
        }
    }
}
