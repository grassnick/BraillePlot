package de.tudresden.inf.mci.brailleplot.printabledata;

import java.util.Iterator;
import java.util.Vector;

import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.configparser.Format;

/**
 * A low effort implementation of the {@link MatrixData} interface.
 * The underlying data is represented by a {@link Vector} which makes the lookup and insertion fast, but uses lots of memory.
 * @param <T> The type used for representing the intensity. Could be set to {@link Boolean} for basic Braille support,
 *  *           but could also by set to {@link Short} if different embossing strengths are required.
 * @author Georg Graßnick
 * @version 2019.06.26
 */
public class SimpleMatrixDataImpl<T> extends AbstractPrintableData implements MatrixData<T> {

    private final int mRows;
    private final int mColumns;
    private final Vector<T> mData;

    public SimpleMatrixDataImpl(final Printer printer, final Format format, final int rowCount, final int columnCount, final T defaultValue) {
        super(printer, format);
        mRows = rowCount;
        mColumns = columnCount;
        mData = new Vector<>(rowCount * columnCount);
        mData.setSize(rowCount * columnCount);
        for (int i = 0; i < mData.size(); i++) {
            mData.setElementAt(defaultValue, i);
        }
    }

    /**
     * Calculate the index for the underlying {@link java.util.ArrayList}.
     * @param row The row index of the requested index.
     * @param column The column index of the requested index.
     * @return The according index in the underlying {@link java.util.ArrayList}
     */
    private int calcIndex(final int row, final int column) {
        if (row >= mRows || column > mColumns) {
            throw new IndexOutOfBoundsException("Index (" + row + "," + column + ") out of bounds");
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
    public Iterator<T> getDotIterator(final int width, final int height) {
        return new ElementIter(width, height, this);
    }

    @Override
    public int getColumnCount() {
        return mColumns;
    }

    @Override
    public int getRowCount() {
        return mRows;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                sb.append(getValue(i, j));
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Iterator that iterates all elements of the matrix in a pattern that iterates Braille cells of specified height
     * and width from left to right and top to bottom.
     * See {@link MatrixData#getDotIterator(int, int)} for details.
     */
    class ElementIter implements Iterator<T> {

        private final SimpleMatrixDataImpl<T> mMatrix;

        private final int mCellWidth;
        private final int mCellHeight;

        // We use indices starting at 1, so that we do not have to check for the x-index to be 0 in the next() method call
        private int mCurrentX = 1;
        private int mCurrentY = 1;

        private boolean mIsFirstElem = true;

        ElementIter(final int cellWidth, final int cellHeight, final SimpleMatrixDataImpl<T> matrix) {
            if (matrix.getColumnCount() % cellWidth != 0) {
                throw new IllegalArgumentException("Cannot create requested iterator: matrix row count (" + matrix.getRowCount() + ") is not a multiple of cell width (" + cellWidth + ")");
            }
            if (matrix.getRowCount() % cellHeight != 0) {
                throw new IllegalArgumentException("Cannot create requested iterator: matrix column count (" + matrix.getColumnCount() + ") is not a multiple of cell height (" + cellHeight + ")");
            }
            mMatrix = matrix;
            mCellWidth = cellWidth;
            mCellHeight = cellHeight;
        }

        @Override
        public boolean hasNext() {
            return !(mCurrentY == mMatrix.getRowCount() && mCurrentX == mMatrix.getColumnCount());
        }

        @Override
        public T next() {
            if (mIsFirstElem) {
                mIsFirstElem = false;
            } else if (mCurrentX % mCellWidth != 0) {
                // Staying in the current cell, move to right
                mCurrentX++;
            } else if (mCurrentY % mCellHeight != 0) {
                // Staying in current cell, move down, set x to the left most index of the current cell
                mCurrentY++;
                mCurrentX = (((mCurrentX / mCellWidth) - 1) * mCellWidth) + 1;
            } else if (mCurrentX < mMatrix.getColumnCount()) { // Moving on to the next cell
                // Right is possible
                mCurrentX += 1;
                mCurrentY = (((mCurrentY / mCellHeight) - 1) * mCellHeight) + 1;
            } else {
                // We need to go downwards
                mCurrentY += 1;
                mCurrentX = (((mCurrentX / mCellWidth) - 1) * mCellWidth) + 1;
            }
            // Correct index to match the specifications of the MatrixData interface
            return mMatrix.getValue(mCurrentY - 1, mCurrentX - 1);
        }
    }
}
