package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.brailleparser.AbstractBrailleTableParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.printerbackend.NotSupportedFileExtensionException;
import org.liblouis.DisplayException;
import org.liblouis.DisplayTable;
import org.liblouis.TranslationException;
import org.liblouis.TranslationResult;
import org.liblouis.Translator;

import static java.lang.Math.ceil;

/**
 * Class representing a brailletextrasterizing approach using the liblouis library.
 * @author Andrey Ruzhanskiy
 * @version 30.08.2019
 */

public class LiblouisBrailleTextRasterizer implements Rasterizer<BrailleText> {

    private AbstractBrailleTableParser mParser;
    // Parameters for rasterizing
    private int x;
    private int y;
    private int origX;
    private RasterCanvas mCanvas;
    private int mMaxWidth;
    private Translator mTranslator;


    /**
     * Constructor for liblouistextrasterizer.
     * @param printer Needed to get the semantictable according to the printer config.
     */
    public LiblouisBrailleTextRasterizer(final Printer printer) {
        try {
            mParser = AbstractBrailleTableParser.getParser(printer, "semantictable");
        } catch (NotSupportedFileExtensionException e) {
            throw new RuntimeException(e);
        }
        try {
            mTranslator = new Translator("C:\\Users\\tEST\\Desktop\\tables\\de-g0.utb");

        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }


    @Override
    public void rasterize(final BrailleText data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {

        Rectangle rect = data.getArea().intersectedWith(canvas.getDotRectangle());
        mCanvas = canvas;
        TranslationResult result = null;
        try {
            result = mTranslator.translate(data.getText(), null, null, null, DisplayTable.StandardDisplayTables.DEFAULT);
        } catch (TranslationException e) {
            e.printStackTrace();
        } catch (DisplayException e) {
            e.printStackTrace();
        }
        String[]resultAsArray = result.getBraille().split("");



        // We need to know where to start
        x = rect.intWrapper().getX();
        origX = x;
        y = rect.intWrapper().getY();
        mMaxWidth = rect.intWrapper().getWidth() + x;
        for (int i = 0; i < resultAsArray.length; i++) {
            writeChar(resultAsArray[i]);
        }

    }

    private void writeToCanvas(final String[] braille, final int offsetX, final int offsetY, final RasterCanvas canvas) {
        int temp = 0;
        for (int j = 0; j < canvas.getCellWidth(); j++) {
            for (int k = 0; k < canvas.getCellHeight(); k++) {
                // If it is 1, returns 1, if not return false
                canvas.getCurrentPage().setValue(k + offsetY, j + offsetX, braille[temp].equals("1"));
                boolean a = canvas.getCurrentPage().getValue(k, j);
                temp++;
            }
        }
    }

    private void writeChar(final String s) throws InsufficientRenderingAreaException {
        String[] braille = mParser.getCharToBraille(s).split("");
        writeToCanvas(braille, x, y, mCanvas);
        jumpToNextCell();
    }

    private void jumpToNextCell() throws InsufficientRenderingAreaException {
        x += 2;
        // Check if linebreak is needed.
        if (x >= mMaxWidth) {
            // Jump into the next line
            y = y + mCanvas.getCellHeight();
            // Reset x
            x = origX;
        }
        if (x > mCanvas.getCellRectangle().getWidth() * mCanvas.getCellWidth() || y > mCanvas.getCellRectangle().getHeight() * mCanvas.getCellHeight()) {
            throw new InsufficientRenderingAreaException();
        }
    }

    /**
     * Calculates the required height for the text.
     * @param text Text to be analyzed.
     * @param xPos X position where to start.
     * @param yPos Y position where to start.
     * @param maxWidth the maximum width of the area where the text has to be
     * @param canvas Canvas on which the text should later appear
     * @return Height in dots.
     */
    public int calculateRequiredHeight(final String text, final int xPos, final int yPos, final int maxWidth,
                                       final RasterCanvas canvas) {
        TranslationResult result = null;
        try {
            result = mTranslator.translate(text, null, null, null, DisplayTable.StandardDisplayTables.DEFAULT);
        } catch (TranslationException e) {
            e.printStackTrace();
        } catch (DisplayException e) {
            e.printStackTrace();
        }
        String sResult = result.getBraille();
        int widthOfText = sResult.length();
        int tempMaxWidth;
        // If its not dividable by two, make it dividable by two;
        if (maxWidth % 2 != 0) {
            tempMaxWidth = maxWidth - 1;
        } else {
            tempMaxWidth = maxWidth;
        }
        return (int) ceil(widthOfText / tempMaxWidth);
    }
}
