package de.tudresden.inf.mci.brailleplot.svgexporter;

import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;
import de.tudresden.inf.mci.brailleplot.rendering.AbstractCanvas;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    AbstractSvgExporter(final T canvas) {
        Objects.requireNonNull(canvas);
        mCanvas = canvas;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render() {
        ListIterator it = (mCanvas.getPageIterator());
        int idx = 0;
        while (it.hasNext()) {
            mSvgs.add(new SVGGraphics2D((int) mCanvas.getPrintableWidth(), (int) mCanvas.getPrintableHeight(), SVGUnits.MM));
            renderPage((((U) it.next())), idx++);
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

    protected abstract void renderPage(U mat, int dataIndex);
}
