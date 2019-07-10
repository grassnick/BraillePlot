package de.tudresden.inf.mci.brailleplot.exporter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DirectPrintTest {
    private final DirectPrint mPrint = new DirectPrint();

    @Test
    public void testNullIsThrownInPrettyPrintCLI(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            mPrint.prettyPrintCLI(null);
        });
    }

    @Test
    public void testNullInPrintString(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mPrint.printString(null);
        });
    }



}
