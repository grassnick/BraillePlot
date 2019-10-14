package de.tudresden.inf.mci.brailleplot.rendering.language;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BrailleLanguageTest {

    @Test
    public void testEnums() {
        Assertions.assertEquals(BrailleLanguage.getCorrectLanguage(BrailleLanguage.Language.DE_BASISSCHRIFT),"de-g0.utb"
        );
        Assertions.assertEquals(BrailleLanguage.getCorrectLanguage(BrailleLanguage.Language.GERMAN_BASISSCHRIFT),"de-g0.utb"
        );
        Assertions.assertEquals(BrailleLanguage.getCorrectLanguage(BrailleLanguage.Language.DE_KURZSCHRIFT),"de-g2.ctb"
        );
        Assertions.assertEquals(BrailleLanguage.getCorrectLanguage(BrailleLanguage.Language.GERMAN_KURZSCHRIFT),"de-g2.ctb"
        );
        Assertions.assertEquals(BrailleLanguage.getCorrectLanguage(BrailleLanguage.Language.DE_VOLLSCHRIFT),"de-g1.ctb"
        );
        Assertions.assertEquals(BrailleLanguage.getCorrectLanguage(BrailleLanguage.Language.GERMAN_VOLLSCHRIFT),"de-g1.ctb"
        );
        Assertions.assertThrows(RuntimeException.class, () -> {
            BrailleLanguage.getCorrectLanguage(null);
        });
        Assertions.assertDoesNotThrow(() -> {
            BrailleLanguage lang = new BrailleLanguage();
        });
    }
}
