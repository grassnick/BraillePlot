package de.tudresden.inf.mci.brailleplot;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Placeholder.
 * @author Andrey Ruzhanskiy, Leonard Kupper
 * @version 29.05.2019
 */

public class FloatingDotArea extends AbstractDocumentBuilder {
    private final byte[] mActivateDotArea = new byte[]{0x1B, 0x46};
    private byte[] mDotList;
    private final byte[] mSemicolon = new byte[] {0x3B};
    private final byte[] mNewLine = new byte[] {0x0A};
    private final byte mColon = 0x3A;


    public FloatingDotArea(final byte[] data) {
            mDocument = this.assemble(data);
    }

    @Override
    public byte[] assemble(final byte[] data) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {

            stream.write(mActivateDotArea);
            stream.write(mSemicolon);
            stream.write(mNewLine);

            stream.write(data);

            stream.write(parseData(data));

            stream.write(mSemicolon);

        } catch (IOException e) {
            e.getMessage();
        }
        return mDocument;
    }

    private byte[] parseData(final byte[] data) {

        for (byte b : data) {
            if (Byte.compare(b, mColon) == 0) {
                System.out.println("No Warning pls");
            }
        }

        return null;
    }

}
