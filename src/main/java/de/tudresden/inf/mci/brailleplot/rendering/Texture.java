package de.tudresden.inf.mci.brailleplot.rendering;

/**
 * A class representing a transformable (translation, stretching, rotation) texture made up from pixel values with arbitrary type.
 * @param <T> The type of the pixel values.
 * @author Leonard Kupper
 * @version 2019.08.23
 */
public class Texture<T> {

    private T[][] mTexturePattern;
    private int mWidth;
    private int mHeight;
    private static final int TRANSLATION_SIZE = 2;
    private static final int LINEARTRANS_SIZE = 4;
    private static final int TRANSFORMATION_SIZE = TRANSLATION_SIZE + LINEARTRANS_SIZE;
    private double[] mAffineTransformation = new double[TRANSFORMATION_SIZE];

    /**
     * Constructor.
     * Creates a new texture from a two-dimensional pattern.
     * @param texturePattern A two-dimensional array of type double describing the texture pattern.
     *                       e.g. diagonal line = [[1,0]
     *                                             [0,1]]
     */
    public Texture(final T[][] texturePattern) {
        mWidth = 0;
        mHeight = validatePatternSize(texturePattern.length);
        if (mHeight < 1) {
            throw new IllegalArgumentException("Given texture pattern is empty.");
        }
        for (int i = 0; i < mHeight; i++) {
            mWidth = Math.max(mWidth, validatePatternSize(texturePattern[i].length));
        }
        mTexturePattern = texturePattern;
        setAffineTransformation(new double[]{0, 0, 1, 0, 0, 1});
    }

    /**
     * Resets the transformation to a given description.
     * @param transformation The description of the new transformation as array of type double:
     *                       [x, y] or [x, y, a, b, c, d]
     *                       (x, y) is a vector describing the translation.
     *                       | a b |
     *                       | c d | is a linear transformation matrix.
     */
    public Texture<T> setAffineTransformation(final double[] transformation) {
        if ((transformation.length != TRANSLATION_SIZE) && (transformation.length != TRANSFORMATION_SIZE)) {
            throw new IllegalArgumentException("Invalid transformation description.");
        }
        for (int i = 0; i < Math.min(TRANSFORMATION_SIZE, transformation.length); i++) {
            mAffineTransformation[i] = transformation[i];
        }
        return this;
    }

    /**
     * Applies a transformation on top of the current transformation.
     * @param transformation The description of the transformation as array of type double:
     *                       [x, y, a, b, c, d]
     *                       (x, y) is a vector describing the translation.
     *                       | a b |
     *                       | c d | is a linear transformation matrix.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public Texture<T> applyAffineTransformation(final double[] transformation) {
        if (transformation.length != TRANSFORMATION_SIZE) {
            throw new IllegalArgumentException("Invalid transformation description.");
        }
        double x, y, a, b, c, d;
        x = mAffineTransformation[0] + transformation[0];
        y = mAffineTransformation[1] + transformation[1];
        a = mAffineTransformation[2] * transformation[2] + mAffineTransformation[4] * transformation[3];
        b = mAffineTransformation[3] * transformation[2] + mAffineTransformation[5] * transformation[3];
        c = mAffineTransformation[2] * transformation[4] + mAffineTransformation[4] * transformation[5];
        d = mAffineTransformation[3] * transformation[4] + mAffineTransformation[5] * transformation[5];
        setAffineTransformation(new double[]{x, y, a, b, c, d});
        return this;
    }

    /**
     * Returns a description of the currently applied transformation.
     * @return An array of type double in the format [x, y, a, b, c, d]
     */
    public double[] getAffineTransformation() {
        return mAffineTransformation;
    }

    /**
     * Returns the width of the texture.
     * @return The width of the texture, after which the texture repeats for x-coordinates bigger than this value.
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * Returns the height of the texture.
     * @return The height of the texture, after which the texture repeats for y-coordinates bigger than this value.
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * Returns the value of the texture at given coordinates. The coordinates can be bigger than the
     * respective texture size, the texture will repeat itself. The given coordinates are treated with
     * the current applied affine transformation.
     * @param x The x-coordinate of the retrieved values point.
     * @param y The y-coordinate of the retrieved values point.
     * @return A pixel value of type T.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public T getTextureValueAt(final int x, final int y) {
        // affine transformation
        double vx = (x + mAffineTransformation[0]);
        double vy = (y + mAffineTransformation[1]);
        int tx = (int) Math.floor(vx * mAffineTransformation[2] + vy * mAffineTransformation[3]);
        int ty = (int) Math.floor(vx * mAffineTransformation[4] + vy * mAffineTransformation[5]);
        // read value
        T[] row = mTexturePattern[modulo(ty, mHeight)];
        return row[modulo(tx, row.length)];
    }

    private int validatePatternSize(final int size) {
        if (size < 1) {
            throw new IllegalArgumentException("The given texture pattern is empty");
        }
        return size;
    }

    private int modulo(final int n, final int mod) {
        int r = n % mod;
        if (r < 0) {
            r += mod;
        }
        return r;
    }

}
