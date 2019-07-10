package de.tudresden.inf.mci.brailleplot.rendering;

public class UniversalTextRasterizer implements Rasterizer<Text> {
    @Override
    public void rasterize(Text data, AbstractRasterCanvas canvas) throws InsufficientRenderingAreaException {
        // TODO: rasterize the text (Take different grids into consideration! 6-dot / 8-dot / equidistant?)
        // Until then, we just display dummy characters
        int x = data.getArea().intWrapper().getX();
        int y = data.getArea().intWrapper().getY();
        for (int i = 0; i < data.getText().length(); i++) {
            System.out.println("y: " + y + " x: " + x);
            canvas.getCurrentPage().setValue(y, x, true);
            canvas.getCurrentPage().setValue(y+1, x+1, true);
            canvas.getCurrentPage().setValue(y+2, x, true);
            x += 2;
        }
        //Rasterizer.rectangle(data.getArea(), canvas.getCurrentPage(), true);
    }

    public int calculateRequiredHeight(final String text, final int xPos, final int yPos, final int maxWidth,
                                           final AbstractRasterCanvas canvas) {
        // TODO: Add calculations for required height to fit the given text into the given canvas. (Linebreaks!)
        // Until then we use a dummy value assuming one line of text:
        return canvas.getCellHeight();
    }

    public int calculateRequiredWidth(final String text, final int xPos, final int yPos, final AbstractRasterCanvas canvas) {
        // TODO: Add calculations for required width to fit the given text into the given canvas. (Extra spacing for equidistant grid!)
        // Until then we use a dummy value assuming single character on braille grid:
        return canvas.getCellWidth();
    }
}
