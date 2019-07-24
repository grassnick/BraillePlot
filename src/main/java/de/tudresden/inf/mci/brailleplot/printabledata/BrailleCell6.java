package de.tudresden.inf.mci.brailleplot.printabledata;

/**
 * Simple container class encapsulating 6 dots to form a representation of a Braille cell.
 * The ordering of the positions follows the following convention:
 * Top to bottom, then left to right; indices start at 0:
 *
 * 0  3
 * 1  4
 * 2  5
 *
 * @param <T> The type used for representing the intensity. Could be set to {@link Boolean} for basic Braille support,
 *           but could also by set to {@link Short} if different embossing strengths are required.
 * @author Georg Graßnick
 * @version 2019.06.28
 */
public final class BrailleCell6<T> {

    static final int DOT_COUNT = 6;
    static final int ROW_COUNT = 3;
    static final int COLUMN_COUNT = 2;

    private T[] mDots;

    /**
     * Constructor.
     * The values are not checked for null!
     * @param first Value of the first dot.
     * @param second Value of the second dot.
     * @param third Value of the third dot.
     * @param fourth Value of the fourth dot.
     * @param fifth Value of the fifth dot.
     * @param sixth  Value of the sixth dot.
     */
    @SuppressWarnings({"unchecked", "checkstyle:MagicNumber"})
    public BrailleCell6(final T first, final T second, final T third, final T fourth, final T fifth, final T sixth) {
        mDots = (T[]) new Object[DOT_COUNT];
        mDots[0] = first;
        mDots[1] = second;
        mDots[2] = third;
        mDots[3] = fourth;
        mDots[4] = fifth;
        mDots[5] = sixth;
    }

    /**
     * Constructor.
     * The values are not checked for null!
     * @param vals An array of values to obtain the values from.
     * @throws IllegalArgumentException If the length is not equal to 6.
     */
    BrailleCell6(final T[] vals) {
        if (vals.length != DOT_COUNT) {
            throw new IllegalArgumentException("Input Array must be of length " + DOT_COUNT);
        }
        mDots = vals.clone();
    }

    /**
     * Get the value at the specified position.
     * @param index The index of the position,
     * @return The according value.
     * @throws ArrayIndexOutOfBoundsException If the index is out of bounds (not in 0-5).
     */
    public T get(final int index) {
        if (index < 0 || index >= DOT_COUNT) {
            throw new ArrayIndexOutOfBoundsException("Index not valid");
        }
        return mDots[index];
    }

    /**
     * Set the value at the specified position.
     * @param index The index of the position.
     * @param value The value to set.
     * @throws ArrayIndexOutOfBoundsException If the index is out of bounds (not in 0-5).
     */
    public void set(final int index, final T value) {
        if (index < 0 || index >= DOT_COUNT) {
            throw new ArrayIndexOutOfBoundsException("Index not valid");
        }
        mDots[index] = value;
    }

    /**
     * Get the internal data array.
     * @return The internal data array of length 6.
     */
    public T[] data() {
        return mDots;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                sb.append(mDots[i * COLUMN_COUNT + j]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Method for getting the Bit Representation of the Cell (110001). Should only be used if T is boolean.
     * @return String containing the Bit Representation.
     */
    public String getBitRepresentationFromBool() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mDots.length; i++) {
            if (Boolean.parseBoolean(mDots[i].toString())) {
                sb.append("1");
            } else {
                sb.append("0");
            }

        }
        return sb.toString();
    }
}
