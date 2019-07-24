package de.tudresden.inf.mci.brailleplot.rendering;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

/**
 * A representation of an (raster graphic) image. Basically just a wrapper for {@link javax.imageio.ImageIO} and
 * {@link java.awt.image.BufferedImage}.
 * @author Leonard Kupper
 * @version 2019.07.22
 */
public class Image implements Renderable {

    private BufferedImage imageData;

    /**
     * Constructor. Creates a new renderable representation from an image file.
     * @param imageFile A file containing an raster graphic image. (Different types supported. BMP, PNG, JPEG, ...)
     * @throws java.io.IOException If an I/O exception of some sort has occurred while reading the image file.
     */
    public Image(final File imageFile) throws java.io.IOException {
        imageData = ImageIO.read(Objects.requireNonNull(imageFile));
    }

    /**
     * Get the loaded image as {@link BufferedImage}.
     * @return An instance of {@link BufferedImage}.
     */
    BufferedImage getBufferedImage() {
        return imageData;
    }

}
