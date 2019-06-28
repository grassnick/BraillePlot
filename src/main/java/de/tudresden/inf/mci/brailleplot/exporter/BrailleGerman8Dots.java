package de.tudresden.inf.mci.brailleplot.exporter;


/**
 * Implements the Braille German 8 Dots Alphabet.
 * @author Andrey Ruzhanskiy
 * @version 28.06.2019
 */
public class BrailleGerman8Dots implements BrailleAlphabet {
    @Override
    public byte[] getValue(BrailleCell cell) {
        if (cell == null){
            throw new NullPointerException();
        }
        return mAlphabet.get(cell);
    }
}
