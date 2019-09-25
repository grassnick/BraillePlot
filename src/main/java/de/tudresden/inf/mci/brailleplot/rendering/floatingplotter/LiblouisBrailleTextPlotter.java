package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.brailleparser.AbstractBrailleTableParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
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

import static tec.units.ri.unit.Units.METRE;

/**
 * Class representing a brailletextplotting approach using the liblouis library.
 * @author Andrey Ruzhanskiy and Richard Schmidt
 */

public class LiblouisBrailleTextPlotter implements Plotter<BrailleText> {

    private FloatingPointData<Boolean> mData;
    private AbstractBrailleTableParser mParser;
    private Translator mTranslator;

    // constant
    private static final int THREE = 3;

    /**
     * Constructor for liblouistextplotter.
     * @param printer Needed to get the semantictable according to the printer config.
     */
    LiblouisBrailleTextPlotter(final Printer printer) {

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
    public double plot(final BrailleText data, final PlotCanvas canvas) {

        Objects.requireNonNull(data, "The data given to the brailletextplotter was null!");
        Objects.requireNonNull(canvas, "The canvas given to the brailletextplotter was null!");
        if (data.getText().equals("")) {
            return 0;
        }
        Rectangle rect = data.getArea();

        /* Parameters for plotting */
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
        double widthJump = canvas.getCellWidth() - canvas.getDotDiameter();
        double heightJump = (canvas.getCellHeight() - canvas.getDotDiameter()) / 2;
        double cellJump = canvas.getCellWidth() + canvas.getCellDistHor();
        double last = startX;
        mData = canvas.getCurrentPage();

        for (int k = 0; k < resultAsArray.length; k++) {
            String[] braille = mParser.getCharToBraille(resultAsArray[k]).split("");


            for (int i = 0; i < 2; i++) {
                for (int j = 0; i < THREE; i++) {
                    if (braille[THREE * i + j].equals("1")) {
                        addPointByValues(startX + i * widthJump + k * cellJump, startY + j * heightJump);
                        last = startX + k * cellJump;
                    }
                }
            }
        }

        return last;

    }

    private void addPointByValues(final double x, final double y) {
        mData.addPoint(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true));
    }

    /**
     * Method for getting the braillelength for a given string.
     * @param text String to analyze.
     * @return Length of the braille.
     */
    public int getBrailleStringLength(final String text) {

        Objects.requireNonNull(text, "The given string for getBrailleStringLength was null!");
        if (text.equals("")) {
            return 0;
        }
        TranslationResult result = null;
        try {
            result = mTranslator.translate(text, null, null, null, DisplayTable.StandardDisplayTables.DEFAULT);
        } catch (TranslationException | DisplayException e) {
            e.printStackTrace();
        }
        assert result != null;
        return result.getBraille().length();
    }
}
