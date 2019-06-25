package de.tudresden.inf.mci.brailleplot.exporter;

import de.tudresden.inf.mci.brailleplot.PrintableData.MatrixData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Class representing the Graphic-Mode-Protocol from Braille Index Everest D4.
 * @author Andrey Ruzhanskiy
 */
public class GraphicPrintBuilder extends AbstractIndexV4Builder {

    private final byte[] mEnterImageMode = new byte[] {0x1B, 0x09};
    private final byte[] mExitImageMode = new byte[] {0x1B, 0x0A};
    private final byte[] mSetImageType = new byte[] {0x1B, 0x0B};

    protected GraphicPrintBuilder() {

    }
    /**
     * Assemble the Document. Hides the Protocol from the user.
     * @return
     * The ready to print Document, as byte[]
     * @param data
     */
    @Override
    public byte[] assemble(final MatrixData data) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(mEnterImageMode);
            stream.write(mSetImageType);

            //


            stream.write(mExitImageMode);
        } catch (IOException e) {
            e.getMessage();
        }
        mDocument = stream.toByteArray();
        return mDocument;
    }

}
