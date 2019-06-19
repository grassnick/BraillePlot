package de.tudresden.inf.mci.brailleplot.exporter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrintDirectorTest {

    @Test
    public void testNullInConstructor(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            PrintDirector print = new PrintDirector(null);
        });
    }
}
