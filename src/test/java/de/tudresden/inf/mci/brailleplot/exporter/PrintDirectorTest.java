package de.tudresden.inf.mci.brailleplot.exporter;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



/**
 * Unit Tests for the PrintDirector Class.
 * @author Andrey Ruzhanskiy
 * @version 15.07.2019
 */

public class PrintDirectorTest {

    // Negative Tests

    /**
     * Test for giving a Null to the PrintDirector-Constructor.
     * Expected: NullPointerException.
     */
    @Test
    public void testNullInConstructor(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            PrintDirector printF1 = new PrintDirector(null);
        });
    }

    /**
     * Test for setting a printername (with the static method printerExists) which does not exist in the System.
     * This should breake if someone registers a printer with the name "kek".
     * Expected: returns false.
     */
    @Test
    public void testPrinterDoesNotExist(){
        Assertions.assertFalse(PrintDirector.printerExists("kek"));
    }

    /**
     * Test for Constructer of the PrintDirector. Tests if the PrinterConfiguration "NormalPrinter" is accepted.
     * Expected: Does not throw a Exception.
     */
    @Test
    public void testConstructerWithNormalBuilder() {
        Assertions.assertDoesNotThrow(() -> {
            PrintDirector printT1 = new PrintDirector(PrinterConfiguration.NORMALPRINTER);
        });
    }

    /**
     * Test for Constructer of the PrintDirector. Tests if the PrinterConfiguration
     * "INDEX_EVEREST_D_V4_FLOATINGDOT_PRINTER" is accepted.
     * Expected: Does not throw a Exception.
     */
    @Test
    public void testConstructerD4WithFloatingDot() {
        Assertions.assertDoesNotThrow(() -> {
            PrintDirector printT1 = new PrintDirector(PrinterConfiguration.INDEX_EVEREST_D_V4_FLOATINGDOT_PRINTER);
        });
    }

    /**
     * Test for Constructer of the PrintDirector. Tests if the PrinterConfiguration
     * "INDEX_EVEREST_D_V4_GRAPHIC_PRINTER" is accepted.
     * Expected: Does not throw a Exception.
     */
    @Test
    public void testConstructerD4WithGraphicPrint() {
        Assertions.assertDoesNotThrow(() -> {
            PrintDirector printT1 = new PrintDirector(PrinterConfiguration.INDEX_EVEREST_D_V4_GRAPHIC_PRINTER);
        });
    }



/*
    @Test
    public void testSetWrongPrinterName(){
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
         PrintDirector printD = new PrintDirector(PrinterConfiguration.NORMALPRINTER);
         Format format = new Format(new ArrayList<>());
         Printer printer = new Printer(new ArrayList<>());
         printD.print("kek", new SimpleMatrixDataImpl<Boolean>(printer, format,12,10,
                 true));
        });
    }

*/

}
