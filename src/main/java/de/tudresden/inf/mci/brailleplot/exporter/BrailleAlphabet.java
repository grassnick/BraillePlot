package de.tudresden.inf.mci.brailleplot.exporter;

import de.tudresden.inf.mci.brailleplot.printabledata.BrailleCell6;

import java.util.HashMap;
import java.util.Map;

/**
 * Common Interface for all Braillealphabets.
 * @author Andrey Ruzhanskiy
 * @version 28.06.2019
 */

public interface BrailleAlphabet {
    public byte[] getValue(BrailleCell6 cell);
    public Map<BrailleCell6, byte[]> mAlphabet = new HashMap<>();
}
