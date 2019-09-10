package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;

/**
 * A rasterizer for text on braille grids. This class is still a stub and must be implemented!
 * @version 2019.08.29
 * @author Leonard Kupper
 */
public final class BrailleTextRasterizer implements Rasterizer<BrailleText> {

    // Ignore this class.
    // It has to be replaced completely with Andreys implementation when the text rasterizer branch gets merged.

    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public void rasterize(final BrailleText data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        // TODO: rasterize the text (Take different grids into consideration! 6-dot / 8-dot)
        // Until then, we just display dummy characters
        int x = data.getArea().intWrapper().getX();
        int y = data.getArea().intWrapper().getY();
        for (int i = 0; i < data.getText().length(); i++) {
            if (x >= canvas.getCurrentPage().getColumnCount()) {
                x = data.getArea().intWrapper().getX();
                y += canvas.getCellHeight();
            }
            char character = data.getText().charAt(i);
            if (character != 0x20) {
                boolean dot1 = ((character % 2) > 0);
                boolean dot2 = ((character / 2 % 2) > 0);
                boolean dot3 = ((character / 4 % 2) > 0);
                boolean dot4 = ((character / 8 % 2) > 0);
                boolean dot5 = ((character / 16 % 2) > 0);
                boolean dot6 = ((character / 32 % 2) > 0);
                canvas.getCurrentPage().setValue(y, x, dot1);
                canvas.getCurrentPage().setValue(y + 1, x, dot2);
                canvas.getCurrentPage().setValue(y + 2, x, dot3);
                canvas.getCurrentPage().setValue(y, x + 1, dot4);
                canvas.getCurrentPage().setValue(y + 1, x + 1, dot5);
                canvas.getCurrentPage().setValue(y + 2, x + 1, dot6);
            }

            x += canvas.getCellWidth();
        }
    }

    public int getBrailleStringLength(final String str) {
        return str.length();
    }

    // TODO: Completely replace with help methods to calculate suited area for left or right alignment of given text.
    public int calculateRequiredHeight(final String text, final int xPos, final int yPos, final int maxWidth,
                                           final RasterCanvas canvas) {
        // TODO: Add calculations for required height to fit the given text into the given canvas. (Linebreaks!)
        // Until then we use a dummy value assuming one line of text:
        return canvas.getCellHeight();
    }

    public int calculateRequiredWidth(final String text, final int xPos, final int yPos, final RasterCanvas canvas) {
        // TODO: Add calculations for required width to fit the given text into the given canvas. (Extra spacing for equidistant grid!)
        // Until then we use a dummy value assuming single character on braille grid:
        return canvas.getCellWidth();
    }
}
