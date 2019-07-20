package de.tudresden.inf.mci.brailleplot.rendering;

/**
 * A rasterizer for text on braille grids.
 * @version 2019.07.20
 * @author Leonard Kupper
 */
public class BrailleTextRasterizer implements Rasterizer<Text> {
    @Override
    public void rasterize(final Text data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        // TODO: rasterize the text (Take different grids into consideration! 6-dot / 8-dot)
        // Until then, we just display dummy characters
        int x = data.getArea().intWrapper().getX();
        int y = data.getArea().intWrapper().getY();
        for (int i = 0; i < data.getText().length(); i++) {
            canvas.getCurrentPage().setValue(y, x, true);
            canvas.getCurrentPage().setValue(y + 1, x + 1, true);
            canvas.getCurrentPage().setValue(y + 2, x, true);
            x += 2;
        }
        //Rasterizer.rectangle(data.getArea(), canvas.getCurrentPage(), true);
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
