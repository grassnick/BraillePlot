package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

/**
 * A rasterizer that is able to fill an area with a texture specified by a {@link TexturedArea}.
 * @author Leonard Kupper
 * @version 2019.08.23
 */
public class TextureRasterizer implements Rasterizer<TexturedArea> {

    /**
     * Rasterizes a {@link TexturedArea} instance onto a {@link RasterCanvas}.
     * @param data An instance of {@link TexturedArea} representing the area to be filled with a texture.
     * @param canvas An instance of {@link RasterCanvas} representing the target for the rasterizer output.
     */
    @Override
    public void rasterize(final TexturedArea data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        Rectangle.IntWrapper area = data.getArea().intWrapper();
        MatrixData<Boolean> page = canvas.getCurrentPage();

        Texture<Boolean> texture = data.getTexture();
        for (int y = 0; y < area.getHeight(); y++) {
            for (int x = 0; x < area.getWidth(); x++) {
                Boolean value = texture.getTextureValueAt(x, y);
                page.setValue(y + area.getY(), x + area.getX(), value);
            }
        }

    }


}
