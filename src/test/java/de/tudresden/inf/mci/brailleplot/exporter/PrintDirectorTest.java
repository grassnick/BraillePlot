package de.tudresden.inf.mci.brailleplot.exporter;


import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



/**
 * Unit Tests for the PrintDirector Class.
 * @author Andrey Ruzhanskiy
 * @version 15.07.2019
 */

public class PrintDirectorTest {

    // Negative Tests
    // Because this componend depends heavily on almost all other components, the testcases will be more integration t
    // tests then unittests.
    /**
     * Test for giving a Null to the PrintDirector-Constructor.
     * Expected: NullPointerException.
     */
    @Test
    public void testNullInConstructor(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            PrintDirector printF1 = new PrintDirector(null, null);
        });
    }

}
