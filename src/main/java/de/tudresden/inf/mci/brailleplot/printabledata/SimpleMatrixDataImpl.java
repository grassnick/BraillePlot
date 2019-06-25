package de.tudresden.inf.mci.brailleplot.printabledata;

import java.util.Vector;

import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.configparser.Format;

/**
 * A low effort implementation of the {@link MatrixData} interface.
 * The underlying data is represented by a {@link Vector} which makes the lookup and insertion fast, but uses lots of memory.
 * @param <T> The type used for representing the intensity. Could be set to {@link Boolean} for basic Braille support,
 *  *           but could also by set to {@link Short} if different embossing strengths are required.
 */
public class SimpleMatrixDataImpl<T> extends AbstractPrintableData implements MatrixData<T> {

    private final int mRows;
    private final int mColumns;
    private final Vector<T> mData;

    SimpleMatrixDataImpl(final Printer printer, final Format format, final int rowCount, final int columnCount) {
        super(printer, format);
        mRows = rowCount;
        mColumns = columnCount;
        mData = new Vector<>(rowCount * columnCount);
    }

    /**
     * Calculate the index for the underlying {@link java.util.ArrayList}.
     * @param row The row index of the requested index.
     * @param column The column index of the requested index.
     * @return The according index in the underlying {@link java.util.ArrayList}
     */
    private int calcIndex(final int row, final int column) {
        if (row >= mRows || column > mColumns) {
            throw new IllegalArgumentException("Index (" + row + "," + column + ") out of bounds");
        }
        return row * mColumns + column;
    }

    @Override
    public T getValue(final int row, final int column) {
        return mData.get(calcIndex(row, column));
    }

    @Override
    public void setValue(final int row, final int column, final T value) {
        if (value == null) {
            throw new NullPointerException();
        }
        mData.set(calcIndex(row, column), value);
    }

    @Override
    public int getColumnCount() {
        return mColumns;
    }

    @Override
    public int getRowCount() {
        return mRows;
    }
}
