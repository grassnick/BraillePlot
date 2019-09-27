package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;
import de.tudresden.inf.mci.brailleplot.util.GeneralResource;
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

import java.io.File;
import java.util.Objects;

import static java.lang.Math.ceil;

/**
 * Class representing a brailletextrasterizing approach using the liblouis library.
 *
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

    // translator needs whole table directory, therefore it is exported one time at start (static resource).
    private static File mLibLouisTableDirectory = GeneralResource.getOrExportResourceFile("mapping/liblouis/");

    /**
     * Constructor for liblouistextrasterizer.
     *
     * @param printer Needed to get the semantictable according to the printer config.
     */
    public LiblouisBrailleTextRasterizer(final Printer printer) {
        Objects.requireNonNull(printer, "The given printer for the LiblouisBrailleTextRasterizer was null!");
        try {
            mParser = AbstractBrailleTableParser.getParser(printer, "semantictable");
        } catch (NotSupportedFileExtensionException e) {
            throw new RuntimeException(e);
        }
        try {
            File tableFile = mLibLouisTableDirectory.toPath().resolve("de-g0.utb").toFile(); // reference to specific table file in exported directory
            String tableFilePath = tableFile.getAbsolutePath();
            mTranslator = new Translator(tableFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating liblouis translator:", e);
        }
    }


    @Override
    public void rasterize(final BrailleText data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        Objects.requireNonNull(data, "The data given to the brailletextrasterizer was null!");
        Objects.requireNonNull(canvas, "The canvas given to the brailletextrasterizer was null!");
        if (data.getText() == "") {
            return;
        }
        try {
            File tableFile = mLibLouisTableDirectory.toPath().resolve(data.getLanguage()).toFile(); // reference to specific table file in exported directory
            String tableFilePath = tableFile.getAbsolutePath();
            mTranslator = new Translator(tableFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating liblouis translator:", e);
        }
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
        String[] resultAsArray = result.getBraille().split("");


        // We need to know where to start
        x = rect.intWrapper().getX();
        origX = x;
        y = rect.intWrapper().getY();
        mMaxWidth = rect.intWrapper().getWidth() + x;
        for (int i = 0; i < resultAsArray.length; i++) {
            writeChar(resultAsArray[i]);
        }

    }

    private void writeToCanvas(final String[] braille, final int offsetX, final int offsetY, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        Objects.requireNonNull(braille, "The string array given to writeToCanvas in liblouisBraileTextRasterizer was null!");
        Objects.requireNonNull(canvas, "The canvas given to writeToCanvas was null");
        int temp = 0;
        for (int j = 0; j < canvas.getCellWidth(); j++) {
            for (int k = 0; k < canvas.getCellHeight(); k++) {
                // If it is 1, returns 1, if not return false
                try {
                    canvas.getCurrentPage().setValue(k + offsetY, j + offsetX, braille[temp].equals("1"));
                    boolean a = canvas.getCurrentPage().getValue(k, j);
                    temp++;
                } catch (IndexOutOfBoundsException e) {
                    throw new InsufficientRenderingAreaException("The area given to the brailletextrasterizer was too small!");
                }

            }
        }
    }

    private void writeChar(final String s) throws InsufficientRenderingAreaException {
        Objects.requireNonNull(s, "The given String to writeChar was null!");
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
    }

    /**
     * Calculates the required height for the text.
     *
     * @param text     Text to be analyzed.
     * @param maxWidth the maximum width of the area where the text has to be. In dots.
     * @param canvas   Canvas on which the text should later appear
     * @return Height in braillecells.
     */
    public int calculateRequiredHeight(final String text, final int maxWidth,
                                       final RasterCanvas canvas) {
        Objects.requireNonNull(text, "The given string for calculateRequiredHeight was null!");
        Objects.requireNonNull(canvas, "The given canvas for calculateRequiredHeight was null!");

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
        return (int) ceil((double) widthOfText / (double) tempMaxWidth);

    }

    /**
     * Calculates the required height for the text with the given language.
     *
     * @param text     Text to be analyzed.
     * @param maxWidth the maximum width of the area where the text has to be. In dots.
     * @param canvas   Canvas on which the text should later appear
     * @param language {@link BrailleLanguage.Language} The language which is to be used.
     * @return Height in braillecells.
     */
    public int calculateRequiredHeight(final String text, final int maxWidth,
                                       final RasterCanvas canvas, final BrailleLanguage.Language language) {
        Objects.requireNonNull(text, "The given string for calculateRequiredHeight was null!");
        Objects.requireNonNull(canvas, "The given canvas for calculateRequiredHeight was null!");
        Translator temp = mTranslator;
        try {
            File tableFile = mLibLouisTableDirectory.toPath().resolve(BrailleLanguage.getCorrectLanguage(language)).toFile(); // reference to specific table file in exported directory
            String tableFilePath = tableFile.getAbsolutePath();
            mTranslator = new Translator(tableFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating liblouis translator:", e);
        }
        int length = calculateRequiredHeight(text, maxWidth, canvas);
        mTranslator = temp;
        return length;
    }


    /**
     * Method for getting the braillelength for a given string.
     * @param text String to analyze
     * @return length of the braille
     */
    public int getBrailleStringLength(final String text) {
        Objects.requireNonNull(text, "The given string for getBrailleStringLength was null!");
        if (text == "") {
            return 0;
        }
        TranslationResult result = null;
        try {
            result = mTranslator.translate(text, null, null, null, DisplayTable.StandardDisplayTables.DEFAULT);
        } catch (TranslationException e) {
            e.printStackTrace();
        } catch (DisplayException e) {
            e.printStackTrace();
        }
        return result.getBraille().length();
    }

    /**
     * Method for getting the braillelength for a given string with the given {@link BrailleLanguage.Language}.
     * @param text String to analyze
     * @param language {@link BrailleLanguage.Language} The language which is to be used.
     * @return length of the braille
     */
    public int getBrailleStringLength(final String text, final BrailleLanguage.Language language) {
        Objects.requireNonNull(text, "The given string for getBrailleStringLength was null!");
        if (text == "") {
            return 0;
        }
        Translator temp = mTranslator;
        try {
            File tableFile = mLibLouisTableDirectory.toPath().resolve(BrailleLanguage.getCorrectLanguage(language)).toFile(); // reference to specific table file in exported directory
            String tableFilePath = tableFile.getAbsolutePath();
            mTranslator = new Translator(tableFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating liblouis translator:", e);
        }
        int length = getBrailleStringLength(text);
        mTranslator = temp;
        return length;
    }
}
