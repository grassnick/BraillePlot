package de.tudresden.inf.mci.brailleplot.rendering.language;

/**
 * Helper class for braillelanguage.
 * @author Andrey Ruzhanskiy
 * @version 27.09.2019
 */

// If we want to use this class, it has to be public
    @SuppressWarnings("HideUtilityClassConstructor")
public class BrailleLanguage {

    /**
     * Method to get the correct name of the table for the given enum.
     * @param language Enum, for which the table is to be known.
     * @return String containing the name of the table.
     */
    public static String getCorrectLanguage(final Language language) {
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
            default: throw new RuntimeException("Unsupported language given as braillelanguage!  \"" + language.toString() + "\"");
        }
    }


    /**
     * Enum describing the current supported braille languages and grammars.
     */
    public enum Language {
        DE_KURZSCHRIFT,
        DE_BASISSCHRIFT,
        DE_VOLLSCHRIFT,
        GERMAN_KURZSCHRIFT,
        GERMAN_BASISSCHRIFT,
        GERMAN_VOLLSCHRIFT
    }
}
