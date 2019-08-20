package de.tudresden.inf.mci.brailleplot.svgexporter;

import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;
import de.tudresden.inf.mci.brailleplot.layout.AbstractCanvas;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUnits;
import org.jfree.graphics2d.svg.ViewBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Abstract parent class for all SVG exporter classes.
 * @param <T> The type of the Abstract Canvas that is used.
 * @param <U> The type of the {@link PrintableData} that is used.
 * @author Georg Graßnick
 * @version 2019.08.16
 */
abstract class AbstractSvgExporter<T extends AbstractCanvas, U extends PrintableData> implements SvgExporter<T> {

    protected List<SVGGraphics2D> mSvgs;
    protected final T mCanvas;
    protected final Logger mLogger = LoggerFactory.getLogger(getClass());
    protected static final int SCALE_FACTOR = 2;
    protected static final float STROKE_WIDTH = 1f;

    private ViewBox mViewBox;


    AbstractSvgExporter(final T canvas) {
        Objects.requireNonNull(canvas);
        mCanvas = canvas;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render() {
        final int docWidth = (int) Math.ceil(mCanvas.getPageWidth());
        final int docHeight = (int) Math.ceil(mCanvas.getPageHeight());

        final int viewBoxWidth = (int) Math.ceil(mCanvas.getPageWidth()) * SCALE_FACTOR;
        final int viewBoxHeight = (int) Math.ceil(mCanvas.getPageHeight()) * SCALE_FACTOR;
        mViewBox = new ViewBox(0, 0, viewBoxWidth, viewBoxHeight);

        ListIterator it = (mCanvas.getPageIterator());
        int idx = 0;
        while (it.hasNext()) {
            SVGGraphics2D svg = new SVGGraphics2D(docWidth, docHeight, SVGUnits.MM);

            svg.setBackground(Color.WHITE);
            svg.clearRect(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
            svg.setStroke(new BasicStroke(STROKE_WIDTH));
            mSvgs.add(svg);
            mLogger.debug("Created SVG with StrokeWitdh {}, ScaleFactor {}, DocumentWidth {}mm, DocumentHeight {}mm, ViewPort: ({},{})", STROKE_WIDTH, SCALE_FACTOR, docWidth, docHeight, viewBoxWidth, viewBoxHeight);

            renderPage((((U) it.next())), idx++);
        }
    }

    @Override
    public void dump(final OutputStream os, final int dataIndex) throws IOException {
        Objects.requireNonNull(os);
        final String doc = mSvgs.get(dataIndex).getSVGElement(null, true, mViewBox, null, null);
        mLogger.trace("Start dumping file to stream ...");
        os.write(doc.getBytes());
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

    protected abstract void renderPage(U mat, int dataIndex);
}
