package de.tudresden.inf.mci.brailleplot.rendering;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

public class Image implements Renderable {

    private BufferedImage imageData;

    public Image(File imageFile) throws java.io.IOException {
        imageData = ImageIO.read(Objects.requireNonNull(imageFile));
    }

    BufferedImage getBufferedImage() {
        return imageData;
    }

}
