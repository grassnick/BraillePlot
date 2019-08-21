package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.brailleparser.AbstractBrailleTableParser;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
//import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.printerbackend.NotSupportedFileExtensionException;

/**
 * A rasterizer for text on braille grids. This class is still a stub and must be implemented!
 * @version 2019.08.17
 * @author Leonard Kupper, Andrey Ruzhanskiy
 */
public final class BrailleTextRasterizer implements Rasterizer<BrailleText> {
    private AbstractBrailleTableParser mParser;


    // Parameters for rasterizing
    private int x;
    private int y;
    private int origX;
    private int maxWidth;
    private RasterCanvas mCanvas;
    private boolean writeCharNormaly = true;

    // TODO use y in helperfunction
    // TODO throw unsufficiant if test is bigger
    // TODO Suspension is only needed, if the number is in the name(ABC93123)
    @Override
    public void rasterize(final BrailleText data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        // Get correct parser according to the config.
        try {
            mParser = AbstractBrailleTableParser.getParser(canvas.getPrinter(), "semantictable");
        } catch (NotSupportedFileExtensionException e) {
            throw new RuntimeException(e.getMessage());
        }
       mCanvas = canvas;
        String[] letterAsBraille;
        // Complete Text, saved in an Array for easier retrieval.
        String[] textAsArray = data.getText().split("");
        // We need to know where to start
        x = data.getArea().intWrapper().getX();
        origX = x;
        y = data.getArea().intWrapper().getY();
        maxWidth = data.getArea().intWrapper().getWidth();
        // Loop through
        for (int i = 0; i < data.getText().length(); i++) {
            // Get current letter as temp.
            String letter = textAsArray[i];
            // If its uppercase, write the corresponding char.
            // Only one of these cases can apply at the same time, all uppercase letters are not special encoded
            //
            if (checkAndWriteForUpperCase(letter.charAt(0))) {
                // Convert letter to lowercase because the map does not include upper case letters
                letter = letter.toLowerCase();
                // Check if its one of the special encoded chars (seperated via ",")
            } else if (checkForSpecialChars(letter)) {
                writeCharNormaly = false;
            } else if (checkForNumbers(letter)) {
                writeChar("NUM");
            }
            // Write the actual letter, if its a normal letter
            if (writeCharNormaly) {
                writeChar(letter);
            }

            /*if (Character.isUpperCase(textAsArray[i].charAt(0))) {
                textAsArray[i] = String.valueOf(Character.toLowerCase(textAsArray[i].charAt(0)));
                isUppercase = true;
                String[] specialUpperChar = mParser.getCharToBraille("CAP").split("");
                rasterizeBrailleCell(specialUpperChar,x,y,canvas);
                // Next BrailleCell
                x += 2;
                // Check if linebreak is needed.
                if (x == maxWidth) {
                    // Jump into the next line
                    y = y + canvas.getCellHeight();
                    // Reset x
                    x = origX;
                }
                letterAsBraille = mParser.getCharToBraille(textAsArray[i]).split("");
                rasterizeBrailleCell(letterAsBraille, x, y, canvas);
                // Next BrailleCell
                x += 2;
                // Check if linebreak is needed.
                if (x == maxWidth) {
                    // Jump into the next line
                    y = y + canvas.getCellHeight();
                    // Reset x
                    x = origX;
                    }

            } else {
                // Normalcase
                // Letter to be printed to the canvas (braille string representation).
                letterAsBraille = mParser.getCharToBraille(textAsArray[i]).split("");
                // The string braille is looking like this: 123456
                // For reference, the real braille is like this:
                // 1 4
                // 2 5
                // 3 6
                rasterizeBrailleCell(letterAsBraille, x, y, canvas);

                }
            // Next BrailleCell
            x += 2;
            // Check if linebreak is needed.
            if (x == maxWidth) {
                // Jump into the next line
                y = y + canvas.getCellHeight();
                // Reset x
                x = origX;
             */
        }
    }

    // TODO Get Liblouis
    private boolean checkForNumbers(String possibleNumber) {
        if (possibleNumber.matches("[0-9]")) {
            //String[] NUM = mParser.getCharToBraille("NUM").split("");
            //rasterizeBrailleCell(NUM);
            return true;
        }
        return false;
    }


    private boolean checkForSpecialChars(String check) {
        String possibleMultiString = mParser.getCharToBraille(check);
        if (possibleMultiString.length() > mCanvas.getCellHeight()*mCanvas.getCellWidth()) {
            String[] possibleStringArray = possibleMultiString.split(",");
            for (int i = 0; i < possibleStringArray.length; i++) {
                rasterizeBrailleCell(possibleStringArray[i].split(""), x, y, mCanvas);
                jumpToNextCell();
            }
            return true;
        }
        return false;
    }

    /**
     * Method for writing the given String, assuming its a char.
     * @param s
     */
    private void writeChar(String s) {
        String[] braille = mParser.getCharToBraille(s).split("");
        rasterizeBrailleCell(braille, x, y, mCanvas);
        jumpToNextCell();
    }

    private boolean checkAndWriteForUpperCase(char charAt) {
        // If the char is uppercase, we need to add a special char(CAP) to signal that the coming braille char is uppercase
        if (Character.isUpperCase(charAt)) {
            String[] specialUpperChar = mParser.getCharToBraille("CAP").split("");
            rasterizeBrailleCell(specialUpperChar,x,y,mCanvas);
            jumpToNextCell();
            return true;
        }
        return false;
    }

    private void jumpToNextCell() {
        x += 2;
        // Check if linebreak is needed.
        if (x >= maxWidth) {
            // Jump into the next line
            y = y + mCanvas.getCellHeight();
            // Reset x
            x = origX;
        }
    }

    /**
     * Helper method to rasterize a single Braille cell on a given canvas with an index.
     * @param letterAsBraille Braillecell to set on the canvas.
     * @param offsetX Offset to ensure that we set the values on the correct X positions.
     * @param canvas Where to set the values.
     */

    private void rasterizeBrailleCell(final String[] letterAsBraille, final int offsetX, final int offsetY, final RasterCanvas canvas) {
        int temp = 0;
        for (int j = 0; j < canvas.getCellWidth(); j++) {
            for (int k = 0; k < canvas.getCellHeight(); k++) {
                // If it is 1, returns 1, if not return false
                canvas.getCurrentPage().setValue(k + offsetY, j + offsetX, letterAsBraille[temp].equals("1"));
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
        // Maximum Rectangle intersecting with real one, Leos suggestion.
        Rectangle rectToIntersect = new Rectangle(xPos, yPos, maxWidth, Integer.MAX_VALUE);
        Rectangle rect = canvas.getCellRectangle().intersectedWith(rectToIntersect);
        // Needed for calculating the number of chars (including space)
        String[] textAsArray = text.split("");
        int spaceInChars = textAsArray.length;
        // Get maximum width in cells
        int availableWidth = rect.intWrapper().getWidth();
        // Divide them, round up
        int height = (int) Math.ceil((double)spaceInChars / availableWidth);
        return height;
    }

    public int calculateRequiredWidth(final String text, final int xPos, final int yPos, final RasterCanvas canvas) {
        // TODO: Add calculations for required width to fit the given text into the given canvas. (Extra spacing for equidistant grid!)
        // Until then we use a dummy value assuming single character on braille grid:
        String mode = canvas.getPrinter().getProperty("mode").toString();
        switch (mode) {
            case "normalprinter": return calculateWidthNormal(text, xPos, yPos, canvas);
            // For the time being
            default: throw new UnsupportedOperationException();
        }
    }

    private int calculateWidthNormal(String text, int xPos, int yPos, RasterCanvas canvas) {
        String[] textAsArray= text.split("");
        return textAsArray.length;
    }
}
