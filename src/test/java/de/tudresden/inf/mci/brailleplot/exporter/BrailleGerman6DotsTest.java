package de.tudresden.inf.mci.brailleplot.exporter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BrailleGerman6DotsTest {

    @Test
    public void testNullIsThrownInPrettyPrintCLI(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            BrailleGerman6Dots B = new BrailleGerman6Dots();
            B.getValue(null);
        });
    }
}
