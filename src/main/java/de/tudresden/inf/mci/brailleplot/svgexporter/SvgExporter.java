package de.tudresden.inf.mci.brailleplot.svgexporter;

import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for all classes that support exporting PrintableData to SVG files.
 * @param <T> The type of the supported PrintableData.
 */
public interface SvgExporter<T extends PrintableData> {

    void renderSvg();

    void dump(OutputStream os) throws IOException;
}
