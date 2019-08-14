package de.tudresden.inf.mci.brailleplot.svgexporter;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;
import de.tudresden.inf.mci.brailleplot.rendering.RasterCanvas;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * SVG exporter class that supports {@link RasterCanvas} as input.
 */
public class BoolMatrixDataSvgExporter implements SvgExporter<RasterCanvas> {

    private final RasterCanvas mRasterCanvas;
    private List<SVGGraphics2D> mSvgs;
    private final Logger mLogger = LoggerFactory.getLogger(BoolMatrixDataSvgExporter.class);

    public BoolMatrixDataSvgExporter(final RasterCanvas rasterCanvas) {
        mRasterCanvas = rasterCanvas;
        mSvgs = new ArrayList<>(mRasterCanvas.getPageCount());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render() {
        ListIterator<PrintableData> matIt = mRasterCanvas.getPageIterator();
        int idx = 0;
        while (matIt.hasNext()) {
            mSvgs.add(new SVGGraphics2D((int) mRasterCanvas.getPrintableWidth(), (int) mRasterCanvas.getPrintableHeight(), SVGUnits.MM));
            renderPage(((MatrixData<Boolean>) matIt.next()), idx++);
        }
    }

    @Override
    public void dump(final OutputStream os, final int dataIndex) throws IOException {
        Objects.requireNonNull(os);
        mLogger.trace("Start dumping file to stream ...");
        os.write(mSvgs.get(dataIndex).getSVGDocument().getBytes());
        mLogger.trace("Finished dumping file to stream");
    }

    @Override
    public void dump(final String filePath, final int dataIndex) throws IOException {
        Objects.requireNonNull(filePath);
        try (FileOutputStream fs = new FileOutputStream(filePath)) {
            dump(fs, dataIndex);
        }
    }

    @Override
    public void dump(final String baseFileName) throws IOException {
        Objects.requireNonNull(baseFileName);
        for (int i = 0; i < mSvgs.size(); i++) {
            dump(baseFileName + String.format("_%03d.svg", i), i);
        }
    }

    private void renderPage(final MatrixData<Boolean> mat, final int dataIndex) {
        List<Double> xPositions = mRasterCanvas.getXPositions();
        List<Double> yPositions = mRasterCanvas.getYPositions();
        int dotDiameter = (int) mRasterCanvas.getDotDiameter();
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
