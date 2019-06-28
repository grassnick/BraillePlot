package de.tudresden.inf.mci.brailleplot.exporter;

/**
 * This Class represents the German 6 Dot Braille Alphabet.
 *
 * @author Andrey Ruzhanskiy
 * @version 28.06.2019
 */

public class BrailleGerman6Dots implements BrailleAlphabet{

    @Override
    public byte[] getValue(BrailleCell cell) {
        if(cell == null){
            throw new NullPointerException();
        }
        return mAlphabet.get(cell);
    }

    public BrailleGerman6Dots() {

        /*
            Letter A
            1
         */

        BrailleCell A = new BrailleCell();
        A.setFirst();
        mAlphabet.put(A, new byte[0x41]);

        /*
            Letter B
            1
            2
         */

        BrailleCell B = new BrailleCell();
        B.setFirst();
        B.setSecond();
        mAlphabet.put(B, new byte[]{0x42});

        /*
            Letter C
            1 4
         */

        BrailleCell C = new BrailleCell();
        C.setFirst();
        C.setFourth();
        mAlphabet.put(C, new byte[]{0x43});

        /*
            Letter D
            1 4
              5
         */

        BrailleCell D = new BrailleCell();
        D.setFirst();
        D.setFourth();
        D.setFifth();
        mAlphabet.put(D, new byte[]{0x44});

        /*
            Letter E
            1
              5
         */

        BrailleCell E = new BrailleCell();
        E.setFirst();
        E.setFifth();
        mAlphabet.put(E, new byte[]{0x45});

        /*
            Letter F
            1 4
            2
         */

        BrailleCell F = new BrailleCell();
        F.setFirst();
        F.setSecond();
        F.setFourth();
        mAlphabet.put(F, new byte[]{0x46});

        /*
            Letter G
            1 4
            2 5
         */

        BrailleCell G = new BrailleCell();
        G.setFirst();
        G.setSecond();
        G.setFourth();
        G.setFifth();
        mAlphabet.put(G, new byte[]{0x47});






    }
}
