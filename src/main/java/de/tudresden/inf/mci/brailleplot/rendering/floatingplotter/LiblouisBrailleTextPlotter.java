package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.brailleparser.AbstractBrailleTableParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import de.tudresden.inf.mci.brailleplot.printerbackend.NotSupportedFileExtensionException;
import de.tudresden.inf.mci.brailleplot.rendering.BrailleText;
import de.tudresden.inf.mci.brailleplot.util.GeneralResource;
import org.liblouis.DisplayException;
import org.liblouis.DisplayTable;
import org.liblouis.TranslationException;
import org.liblouis.TranslationResult;
import org.liblouis.Translator;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.io.File;
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
    private static final int ITERATORSCALE = 3;

    // translator needs whole table directory, therefore it is exported one time at start (static resource).
    private static File mLibLouisTableDirectory = GeneralResource.getOrExportResourceFile("mapping/liblouis/");

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
            File tableFile = mLibLouisTableDirectory.toPath().resolve("de-g0.utb").toFile(); // reference to specific table file in exported directory
            String tableFilePath = tableFile.getAbsolutePath();
            mTranslator = new Translator(tableFilePath);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }


    /**
     * Translates a text into Braille characters and plots it onto a {@link PlotCanvas}.
     * @param data BrailleText with the text and the rectangle representing the plotting area.
     * @param canvas An instance of {@link PlotCanvas} representing the target for the plotter output.
     * @return Last y-coordinate.
     * @throws InsufficientRenderingAreaException If the translator can't be constructed.
     */
    @Override
    public double plot(final BrailleText data, final PlotCanvas canvas) throws InsufficientRenderingAreaException {

        Objects.requireNonNull(data, "The data given to the brailletextplotter was null!");
        Objects.requireNonNull(canvas, "The canvas given to the brailletextplotter was null!");
        if (data.getText() == "") {
            return 0;
        }

        try {
            File tableFile = mLibLouisTableDirectory.toPath().resolve(data.getLanguage()).toFile(); // reference to specific table file in exported directory
            String tableFilePath = tableFile.getAbsolutePath();
            mTranslator = new Translator(tableFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating liblouis translator", e);
        }

        Rectangle rect = data.getArea();
        TranslationResult result = null;
        try {
            result = mTranslator.translate(data.getText(), null, null, null, DisplayTable.StandardDisplayTables.DEFAULT);
        } catch (TranslationException e) {
            e.printStackTrace();
        } catch (DisplayException e) {
            e.printStackTrace();
        }
        assert result != null;
        String[] resultAsArray = result.getBraille().split("");

        double startX = rect.getX();
        double startY = rect.getY();
        double widthJump = canvas.getDotDistHor();
        double heightJump = canvas.getDotDistVer();
        double cellJump = widthJump + canvas.getCellDistHor();
        double last = startX;
        mData = canvas.getCurrentPage();

        for (int k = 0; k < resultAsArray.length; k++) {
            String[] braille = mParser.getCharToBraille(resultAsArray[k]).split("");

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j <= 2; j++) {
                    if (braille[ITERATORSCALE * i + j].equals("1")) {
                        addPointByValues(startX + i * widthJump + k * cellJump, startY + j * heightJump);
                        last = startX + k * cellJump;
                    }
                }
            }
        }

        return last;

    }

    /**
     * Adds a point by its absolute x- and y-value to the floating point data.
     * @param x Absolute x-value.
     * @param y Absolute y-value.
     */
    private void addPointByValues(final double x, final double y) {
        mData.addPointIfNotExisting(new Point2DValued<Quantity<Length>, Boolean>(Quantities.getQuantity(x, MetricPrefix.MILLI(METRE)), Quantities.getQuantity(y, MetricPrefix.MILLI(METRE)), true));
    }

}
