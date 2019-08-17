package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.brailleparser.AbstractBrailleTableParser;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
//import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;
import de.tudresden.inf.mci.brailleplot.printerbackend.NotSupportedFileExtensionException;

/**
 * A rasterizer for text on braille grids. This class is still a stub and must be implemented!
 * @version 2019.08.17
 * @author Leonard Kupper, Andrey Ruzhanskiy
 */
public final class BrailleTextRasterizer implements Rasterizer<BrailleText> {
    private AbstractBrailleTableParser mParser;

    @Override
    public void rasterize(final BrailleText data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        // Get correct parser according to the config.
        try {
            mParser = AbstractBrailleTableParser.getParser(canvas.getPrinter());
        } catch (NotSupportedFileExtensionException e) {
            throw new RuntimeException(e.getMessage());
        }
        String[] letterAsBraille;
        // Complete Text, saved in an Array for easier retrieval.
        String[] textAsArray = data.getText().split("");
        // We need to know where to start
        int x = data.getArea().intWrapper().getX();
        // Loop through
        for (int i = 0; i < data.getText().length(); i++) {

            // If the char is uppercase, we need to add a special char to signal that the coming braille char is uppercase
            // Depended on the used brailletable
            // Currently, it is simply converted to lowercase.
            if (Character.isUpperCase(textAsArray[i].charAt(0))) {
                    textAsArray[i] = String.valueOf(Character.toLowerCase(textAsArray[i].charAt(0)));
            }
            // Letter to be printed to the canvas (braille string representation).
            int j = data.getText().length();
            String letter = textAsArray[i];
            letterAsBraille = mParser.getDots(textAsArray[i]).split("");

            // First cell width, then cell height.
            // The string braille is looking like this: 123456
            // For reference, the real braille is like this:
            // 1 4
            // 2 5
            // 3 6
            rasterizeBrailleCell(letterAsBraille, x, canvas);
            //SimpleMatrixDataImpl<Boolean> mat = (SimpleMatrixDataImpl) canvas.getCurrentPage();
            //System.out.println(mat.toBoolString());
            // Next BrailleCell
            x += 2;
        }
    }

    /**
     * Helper method to rasterize a single Braille cell on a given canvas with an index.
     * @param letterAsBraille Braillecell to set on the canvas.
     * @param offset Offset to ensure that we set the values on the correct positions.
     * @param canvas Where to set the values.
     */

    private void rasterizeBrailleCell(final String[] letterAsBraille, final int offset, final RasterCanvas canvas) {
        int temp = 0;
        for (int j = 0; j < canvas.getCellWidth(); j++) {
            for (int k = 0; k < canvas.getCellHeight(); k++) {
                // If it is 1, returns 1, if not return false
                canvas.getCurrentPage().setValue(k, j + offset, letterAsBraille[temp].equals("1"));
                boolean a = canvas.getCurrentPage().getValue(k, j);
                temp++;

            }
        }
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
