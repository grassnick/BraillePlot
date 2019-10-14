package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BrailleTextTest {

    @Test
    public  void setUp() {
        Assertions.assertDoesNotThrow(() -> {
            BrailleText text = new BrailleText("test", new Rectangle(0, 0, 0, 0), BrailleLanguage.Language.DE_BASISSCHRIFT);
        });
    }
}
