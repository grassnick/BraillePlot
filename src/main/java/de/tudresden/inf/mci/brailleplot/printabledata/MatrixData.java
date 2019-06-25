package de.tudresden.inf.mci.brailleplot.printabledata;

/**
 * This data is used to describe the data for the "Braille" and "Graphics" print modes.
 * The data is organized in a matrix structure, which can be queried for its values on integer x (row) and y (column) indices.
 * @param <T> The type used for representing the intensity. Could be set to {@link Boolean} for basic Braille support,
 *           but could also by set to {@link Short} if different embossing strengths are required.
 */
public interface MatrixData<T> extends PrintableData {

    /**
     * Get the value for a specific position in the matrix.
     * Indices start at 0.
     * @param row The row index of the position.
     * @param column The column index of the position.
     * @return The value at the requested position.
     */
    T getValue(int row, int column);

    /**
     * Set the value at a specific position.
     * Indices start at 0.
     * @param row The row index of the position.
     * @param column The column index of the position.
     * @param value The value to set.
     */
    void setValue(int row, int column, T value);

    /**
     * Getter.
     * @return The number of rows.
     */
    int getRowCount();

    /**
     * Getter.
     * @return The number of columns.
     */
    int getColumnCount();
}
