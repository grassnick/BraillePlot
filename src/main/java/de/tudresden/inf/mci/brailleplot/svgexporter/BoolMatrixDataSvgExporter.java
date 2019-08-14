package de.tudresden.inf.mci.brailleplot.svgexporter;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;
import de.tudresden.inf.mci.brailleplot.rendering.RasterCanvas;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ListIterator;

/**
 * SVG exporter class that supports {@link MatrixData}{@literal <}{@literal Boolean}{@literal >}.
 */
public class BoolMatrixDataSvgExporter implements SvgExporter<MatrixData<Boolean>> {

    private final RasterCanvas mRasterCanvas;
    private SVGGraphics2D mSvg;
    private final Logger mLogger = LoggerFactory.getLogger(BoolMatrixDataSvgExporter.class);

    public BoolMatrixDataSvgExporter(final RasterCanvas rasterCanvas) {
        mRasterCanvas = rasterCanvas;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void renderSvg() {
        setupSvg();
        ListIterator<PrintableData> matIt = mRasterCanvas.getPageIterator();
        while (matIt.hasNext()) {
            renderPage(((MatrixData<Boolean>) matIt.next()));
        }
    }

    @Override
    public void dump(final OutputStream os) throws IOException {
        mLogger.trace("Start dunping file to stream ...");
        os.write(mSvg.getSVGDocument().getBytes());
        mLogger.trace("Finished dunping file to stream ...");
    }

    private void setupSvg() {
        mSvg = new SVGGraphics2D((int) mRasterCanvas.getPrintableWidth(), (int) mRasterCanvas.getPrintableHeight(), SVGUnits.MM);
    }

    private void renderPage(final MatrixData<Boolean> mat) {
        List<Double> xPositions = mRasterCanvas.getXPositions();
        List<Double> yPositions = mRasterCanvas.getYPositions();
        int dotDiameter = (int) mRasterCanvas.getDotDiameter();
        final int mult = 4;

        for (int y = 0; y < mat.getRowCount(); y++) {
            for (int x = 0; x < mat.getColumnCount(); x++) {
                if (mat.getValue(y, x)) {
                    int xPos = (int) (double) xPositions.get(x);
                    int yPos = (int) (double) yPositions.get(y);
                    mSvg.drawOval(xPos * mult, yPos * mult, dotDiameter * mult, dotDiameter * mult);
                }
            }
        }
    }
}
