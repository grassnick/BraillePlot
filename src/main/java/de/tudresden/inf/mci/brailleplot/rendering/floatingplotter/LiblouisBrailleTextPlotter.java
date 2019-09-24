package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.brailleparser.AbstractBrailleTableParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printerbackend.NotSupportedFileExtensionException;
import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
import org.liblouis.DisplayException;
import org.liblouis.DisplayTable;
import org.liblouis.TranslationException;
import org.liblouis.TranslationResult;
import org.liblouis.Translator;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Objects;

import static java.lang.Math.ceil;
import static tec.units.ri.unit.Units.METRE;

/**
 * Class representing a brailletextplotting approach using the liblouis library.
 * @author Andrey Ruzhanskiy and Richard Schmidt
 */

public class LiblouisBrailleTextPlotter implements Plotter<BrailleText> {

    private AbstractBrailleTableParser mParser;

    // Parameters for plotting
    private PlotCanvas mCanvas;
    private Translator mTranslator;
    private FloatingPointData<Boolean> mData;

    // constant
    private static final double THREE = 3;


    /**
     * Constructor for liblouistextplotter.
     * @param printer Needed to get the semantictable according to the printer config.
     */
    public LiblouisBrailleTextPlotter(final Printer printer) {
        Objects.requireNonNull(printer, "The given printer for the LiblouisBrailleTextPlotter was null!");
        try {
            mParser = AbstractBrailleTableParser.getParser(printer, "semantictable");
        } catch (NotSupportedFileExtensionException e) {
            throw new RuntimeException(e);
        }
        try {
            mTranslator = new Translator("src\\main\\resources\\mapping\\liblouis\\de-g0.utb");
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }


    @Override
    public void plot(final BrailleText data, final PlotCanvas canvas) throws InsufficientRenderingAreaException {
        Objects.requireNonNull(data, "The data given to the brailletextplotter was null!");
        Objects.requireNonNull(canvas, "The canvas given to the brailletextplotter was null!");
        if (data.getText() == "") {
            return;
        }
        Rectangle rect = data.getArea();
        mCanvas = canvas;
        TranslationResult result = null;
        try {
            result = mTranslator.translate(data.getText(), null, null, null, DisplayTable.StandardDisplayTables.DEFAULT);
        } catch (TranslationException | DisplayException e) {
            e.printStackTrace();
        }
        assert result != null;
        String[] resultAsArray = result.getBraille().split("");

        double startX = rect.getX();
        double startY = rect.getX();
        double widthJump = mCanvas.getCellWidth() - mCanvas.getDotDiameter();
        double heightJump = (mCanvas.getCellHeight() - mCanvas.getDotDiameter()) / 2;
        double cellJumpHor = mCanvas.getCellDistHor();
        double cellJumpVer = mCanvas.getCellDistVer();
        mData = mCanvas.getCurrentPage();

        for (int k = 0; k < resultAsArray.length; k++) {
            String[] braille = mParser.getCharToBraille(resultAsArray[k]).split("");


            for (int i = 0; i < 2; i++) {
                for (int j = 0; i < THREE; i++) {
                        if (braille[i + j].equals("one")) {
                            addPointByValues(startX + i * widthJump + k * cellJumpHor, startY + j * heightJump);
                        }
                }
            }
        }

    }

    private void addPointByValues(final double x, final double y) {
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true));
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

    private void writeC(final String s) throws InsufficientRenderingAreaException {
        Objects.requireNonNull(s, "The given String to writeChar was null!");
        String[] braille = mParser.getCharToBraille(s).split("");
        //writeToCanvas(braille, x, y, mCanvas);
        jumpToNextCell();
    }

    private void jumpToNextCell() throws InsufficientRenderingAreaException {
        //x += 2;
        // Check if linebreak is needed.
        //if (x >= mMaxWidth) {
            // Jump into the next line
            //y = y + mCanvas.getCellHeight();
            // Reset x
         //   x = origX;

    }

    /**
     * Calculates the required height for the text.
     * @param text Text to be analyzed.
     * @param xPos X position where to start.
     * @param yPos Y position where to start.
     * @param maxWidth the maximum width of the area where the text has to be
     * @param canvas Canvas on which the text should later appear
     * @return Height in braillecells.
     */
    public int calculateRequiredHeight(final String text, final int xPos, final int yPos, final int maxWidth,
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
}
