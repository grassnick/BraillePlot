package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.layout.Rectangle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

/**
 * Representation of an area to be filled with a specific texture.
 * Furthermore container for predefined texture patterns and helper methods for texture creation.
 * @author Leonard Kupper
 * @version 2019.08.23
 */
public class TexturedArea implements Renderable {

    private Rectangle mArea;
    private Texture<Boolean> mTexture;

    // Predefined texture patterns
    public static final Boolean[][] DOTTED_PATTERN = {
        {false, false},
        {false, true},
        {false, false},
        {true, false}
    };
    public static final Boolean[][] DASHED_PATTERN = {
        {false, false, false},
        {false, true, true},
        {false, true, true},
        {false, true, true}
    };

    /**
     * Texture factory method to create a texture from a bitmap.
     * @param imageFile A file object representing the texture bitmap. Each non-white pixel in the bitmap will
     *                  cause a pixel to be set in the resulting monochrome texture.
     * @return A {@link Texture}&lt;Boolean&gt; containing the bitmaps texture pattern.
     * @throws java.io.IOException On any error while reading the bitmap into a {@link BufferedImage}.
     */
    public static Texture<Boolean> textureFromImage(final File imageFile) throws java.io.IOException {
        BufferedImage imgBuf = ImageIO.read(Objects.requireNonNull(imageFile));
        Boolean[][] texturePattern = new Boolean[imgBuf.getHeight()][imgBuf.getWidth()];
        final int white = -0x000001; // Java does not know unsigned ints.....
        for (int y = 0; y < imgBuf.getHeight(); y++) {
            for (int x = 0; x < imgBuf.getWidth(); x++) {
                System.out.println(imgBuf.getRGB(x, y));
                texturePattern[y][x] = (imgBuf.getRGB(x, y) != white);
            }
        }
        return new Texture<>(texturePattern);
    }


    /**
     * Constructor. Creates a representation of a texture filled area.
     * @param texture The areas texture represented by a {@link Texture} instance.
     * @param area The desired area for the texture to be rendered on.
     */
    public TexturedArea(final Texture<Boolean> texture, final Rectangle area) {
        setTexture(texture);
        setArea(area);
    }

    /**
     * Sets a new texture.
     * @param texture The areas texture represented by a {@link Texture} instance.
     */
    public void setTexture(final Texture<Boolean> texture) {
        mTexture = Objects.requireNonNull(texture);
    }

    /**
     * Gets the currently set texture.
     * @return A {@link Texture} instance representing the texture.
     */
    public Texture<Boolean> getTexture() {
        return mTexture;
    }

    /**
     * Sets a new area for the texture.
     * @param area The new area which the texture fills.
     */
    public void setArea(final Rectangle area) {
        mArea = Objects.requireNonNull(area);
    }

    /**
     * Gets the current area of the texture.
     * @return The area to be filled with the texture.
     */
    public Rectangle getArea() {
        return mArea;
    }
}
