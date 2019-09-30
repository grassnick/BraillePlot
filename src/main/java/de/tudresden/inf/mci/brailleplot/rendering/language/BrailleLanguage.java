package de.tudresden.inf.mci.brailleplot.rendering.language;

/**
 * Helper class for braillelanguage
 * @author Andrey Ruzhanskiy
 * @version 27.09.2019
 */
public class BrailleLanguage {

    public static String getCorrectLanguage(Language language){
        switch (language) {
            case GERMAN_VOLLSCHRIFT:
            case DE_VOLLSCHRIFT:
                return "de-g1.ctb";
            case GERMAN_BASISSCHRIFT:
            case DE_BASISSCHRIFT:
                return "de-g0.utb";
            case GERMAN_KURZSCHRIFT:
            case DE_KURZSCHRIFT:
                return "de-g2.ctb";
        }
        throw new RuntimeException("Unsupported language given as braillelanguage!  \"" + language.toString() + "\"");
    }


    public enum Language {
        DE_KURZSCHRIFT,
        DE_BASISSCHRIFT,
        DE_VOLLSCHRIFT,
        GERMAN_KURZSCHRIFT,
        GERMAN_BASISSCHRIFT,
        GERMAN_VOLLSCHRIFT
    }
}
