package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.brailleparser.AbstractBrailleTableParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
//import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.printerbackend.NotSupportedFileExtensionException;
/* import org.liblouis.CompilationException;
import org.liblouis.DisplayException;
import org.liblouis.DisplayTable;
import org.liblouis.TranslationException;
import org.liblouis.TranslationResult;
import org.liblouis.Translator;
import org.liblouis.Typeform;
*/
import java.util.Objects;

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
    // TODO:

    public BrailleTextRasterizer(Printer printer) {
        try {
            mParser = AbstractBrailleTableParser.getParser(printer, "semantictable");
        } catch (NotSupportedFileExtensionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rasterize(final BrailleText data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {

      //  Translator translator = null;
        String test = "123";
        String testA = "";
      //  TranslationResult result = null;
      //  System.out.println(testMethod());

        Rectangle rect = data.getArea().intersectedWith(canvas.getDotRectangle());
        mCanvas = canvas;
        String[] letterAsBraille;
        // Complete Text, saved in an Array for easier retrieval.
        String[] textAsArray = data.getText().split("");
        // We need to know where to start
        x = rect.intWrapper().getX();
        origX = x;
        y = rect.intWrapper().getY();
        int maxHeight = rect.intWrapper().getHeight();
        maxWidth = rect.intWrapper().getWidth();
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

        }
    }

   /* public String testMethod() {
        TranslationResult result = null;
        TranslationResult wrongResult = null;
        try {
        //    Translator translator = new Translator("C:\\Users\\tEST\\Desktop\\FPMCI\\brailleplot\\src\\main\\resources\\mapping\\tables\\de-de-comp8.ctb");
        //    result = translator.translate("ABCD",null,null,null, DisplayTable.StandardDisplayTables.DEFAULT);
            // translator = Translator.find("locale: de");
            Translator wrongTranslator = new Translator("src\\main\\resources\\mapping\\tables\\de.utb");
            wrongResult = wrongTranslator.translate("123", null, null, null, DisplayTable.StandardDisplayTables.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(wrongResult.getBraille());
        return wrongResult.getBraille();
    }
*/
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

    // TODO: Return in Dots
    // TODO: Method Signature: Change so that a allignment (x), representing left and right is taken into consideration
    // TODO: Method Signature: Change so that a allignment (y), representing left and right is taken into consideration
    public int calculateRequiredHeight(final String text, final int xPos, final int yPos, final int maxWidth,
                                           final RasterCanvas canvas) {
        // TODO: Add calculations for required height to fit the given text into the given canvas. (Linebreaks!)
        // Until then we use a dummy value assuming one line of text:
        // Maximum Rectangle intersecting with real one, Leos suggestion.

        Rectangle rectToIntersect = new Rectangle(xPos, yPos, maxWidth, Integer.MAX_VALUE);
        Rectangle rect = canvas.getCellRectangle().intersectedWith(rectToIntersect);
        // Needed for calculating the number of chars (including space)
        String[] textAsArray = text.split("");
        // Get maximum width in cells
        int availableWidth = rect.intWrapper().getWidth();
        // Divide them, round up
        // TODO:
        canvas.getDotRectangle().intWrapper().getX();
        int height = (int) Math.ceil((double)((int) calculateWidthNormal(text, canvas)) / availableWidth);
        return height;
    }

    // public int calculateRequiredWidth(final String text, final int xPos, final int yPos, final RasterCanvas canvas) {
    public int calculateRequiredWidth(final String text,  RasterCanvas canvas) {
        // TODO: Add calculations for required width to fit the given text into the given canvas. (Extra spacing for equidistant grid!)
        // Until then we use a dummy value assuming single character on braille grid:
        String mode = canvas.getPrinter().getProperty("mode").toString();
        switch (mode) {
        //    case "normalprinter": return calculateWidthNormal(text, xPos, yPos, canvas);
            case "normalprinter": return Math.min((int)calculateWidthNormal(text, canvas), canvas.getCellRectangle().intWrapper().getX());
            // For the time being
            default: throw new UnsupportedOperationException();
        }
    }

    // TODO Remove canvas, not needed
    public long calculateWidthNormal(String text, RasterCanvas canvas) {
        String[] textAsArray = text.split("");
        int length = textAsArray.length;
        long upperCase = text.codePoints().filter(c-> c>='A' && c<='Z').count();
        long number = text.codePoints().filter(c-> c>='0' && c<='9').count();
        int multipleBrailleCells = countMultipleCells(text);
        long result = length + upperCase + number + multipleBrailleCells;
        return result;
    }

    private int countMultipleCells(String text) {
        int result = 0;
        text = text.toLowerCase();
        String[] textAsArray = text.split("");
        for (int i = 0; i < textAsArray.length ; i++) {
            if (mParser.getCharToBraille(textAsArray[i]).split(",").length>1) {
                String[] test = mParser.getCharToBraille(textAsArray[i]).split(",");
                result += mParser.getCharToBraille(textAsArray[i]).split(",").length;
                result --;
            }
        }
        return result;
    }
}
