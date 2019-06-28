package de.tudresden.inf.mci.brailleplot.exporter;

import java.util.HashMap;
import java.util.Map;

/**
 * Common Interface for all Braillealphabets.
 * @author Andrey Ruzhanskiy
 * @version 28.06.2019
 */

public interface BrailleAlphabet {
    public byte[] getValue(BrailleCell cell);
    public Map<BrailleCell, byte[]> mAlphabet = new HashMap<>();
}
