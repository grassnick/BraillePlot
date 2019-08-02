package de.tudresden.inf.mci.brailleplot.printerbackend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for the NormalBuilder Class.
 * @author Andrey Ruzhanskiy
 * @version 15.07.2019
 */
public class NormalBuilderTest {


    /**
     * Test for giving a Null to the NormalBuilder assemble Method.
     * Expected: NullPointerException.
     */
    @Test
    public void testAssembleWithNull(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            NormalBuilder normalF1 = new NormalBuilder();
            normalF1.assemble(null);
        });
    }
}
