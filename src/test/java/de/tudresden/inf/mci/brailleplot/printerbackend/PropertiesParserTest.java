package de.tudresden.inf.mci.brailleplot.printerbackend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for the PropertiesParser Class.
 * @author Andrey Ruzhanskiy
 * @version 15.07.2019
 */
public class PropertiesParserTest {

    /**
     * Test for giving a Null to the PropertiesParser Constructor.
     * Expected: NullPointerException.
     */
    @Test
    public void testNullInConstructor(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            PropertiesParser parserF1 = new PropertiesParser(null);
        });
    }

    /**
     * Test for giving a non-existing Path to the PropertiesParser Constructor.
     * Expected: RuntimeException.
     */
    @Test
    public void testNonValidPath(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            PropertiesParser parserF1 = new PropertiesParser("/C/D/E/F/G");
        });
    }
}
