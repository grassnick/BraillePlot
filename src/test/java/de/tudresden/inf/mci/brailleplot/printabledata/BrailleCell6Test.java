package de.tudresden.inf.mci.brailleplot.printabledata;

import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BrailleCell6Test {

    public static BrailleCell6<Boolean> cell;

    @Test
    @BeforeAll
    public static void setUp() {
        Assertions.assertDoesNotThrow(() -> {
            cell = new BrailleCell6<>(true,true,true,true,true,true);
        });
    }

    @Test
    public void testGetters() {
        Assertions.assertEquals(cell.get(0), true);
        Assertions.assertEquals(cell.get(1), true);
        Assertions.assertEquals(cell.get(2), true);
        Assertions.assertEquals(cell.get(3), true);
        Assertions.assertEquals(cell.get(4), true);
        Assertions.assertEquals(cell.get(5), true);
        Assertions.assertEquals(cell.getBitRepresentationFromBool(), "111111");
    }

    @Test
    public void testSetters() {
        Assertions.assertDoesNotThrow(() -> {
            cell.set(2, false);
            Assertions.assertEquals(cell.get(2), false);
            cell.toString();
            cell.data();
        });
    }

}
