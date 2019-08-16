package de.tudresden.inf.mci.brailleplot.svgexporter;

import de.tudresden.inf.mci.brailleplot.layout.AbstractCanvas;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for all classes that support exporting PrintableData to SVG files.
 * @param <T> The type of the supported PrintableData.
 * @author Georg Graßnick
 * @version 2019.08.16
 */
public interface SvgExporter<T extends AbstractCanvas> {

    /**
     * Process the actual rendering of the data to SVG.
     */
    void render();

    /**
     * Dump a specific rendered SVG to an output stream.
     * @param os The stream to dump the SVG to.
     * @param dataIndex The index of the {@link de.tudresden.inf.mci.brailleplot.printabledata.PrintableData} in {@link AbstractCanvas#getPageIterator()}
     * @throws IOException on any IO related issues.
     */
    void dump(OutputStream os, int dataIndex) throws IOException;

    /**
     Dump a specific rendered SVG to a file.
     * @param filePath The path to dump the file at.
     * @param dataIndex The index of the {@link de.tudresden.inf.mci.brailleplot.printabledata.PrintableData} in {@link AbstractCanvas#getPageIterator()}
     * @throws IOException on any IO related issues.
     */
    void dump(String filePath, int dataIndex) throws IOException;

    /**
     * Dump all rendered SVGs to separate files.
     * @param baseFileName The basename of all files. Output filename format: ${basename}_${Index}.svg
     * @throws IOException on any IO related issues.
     */
    void dump(String baseFileName) throws IOException;
}
