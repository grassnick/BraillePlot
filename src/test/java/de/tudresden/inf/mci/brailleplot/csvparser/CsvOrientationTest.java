package de.tudresden.inf.mci.brailleplot.csvparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CsvOrientationTest {

    @Test
    public void testCsvOrientation() {
        Assertions.assertEquals(CsvOrientation.fromString("vertical"), CsvOrientation.VERTICAL);
        Assertions.assertEquals(CsvOrientation.fromString("h"), CsvOrientation.HORIZONTAL);
        Assertions.assertDoesNotThrow(() -> {
            CsvOrientation.CsvOrientationConverter or = new CsvOrientation.CsvOrientationConverter();
            Assertions.assertEquals(or.convert("vertical"),CsvOrientation.VERTICAL);
        });

    }
}
