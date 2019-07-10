package de.tudresden.inf.mci.brailleplot.exporter;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrintDirectorTest {

    @Test
    public void testNullInConstructor(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            PrintDirector print = new PrintDirector(null);
        });
    }

    @Test
    public void testPrinterDoesNotExist(){
        Assertions.assertEquals(false, PrintDirector.printerExists("kek"));
    }
    
    @Test
    public void testNullPointerInPrintString(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            PrintDirector print = new PrintDirector(PrinterConfiguration.NORMALPRINTER);
            print.print(null, new SimpleMatrixDataImpl<Boolean>(new Printer(), new Format(), 18,
                    20, true) {
            });
        });
    }

}
