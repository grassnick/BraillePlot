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
    private final byte[] mParameterOrigo = new byte[]{0x4F, 0x52};
    private final byte[] mParameterWidth = new byte[]{0x57, 0x58};
    private final byte[] mParameterHeight = new byte[]{0x48, 0x59};
    private byte[] mDotList;
    private final byte[] mSemicolon = new byte[] {0x3B};
    private final byte[] mNewLine = new byte[] {0x0A};
    private final byte mColon = 0x3A;
    private final byte mComma = 0x2C;
    private int mOrigoX = -1;
    private int mOrigoY = -1;
    private int mWidthX = -1;
    private int mHeightY = -1;

    /**
     * Construct a floating dot area with default origin and size.
     * @param data
     * A sequence of bytes containing a newline separated line of colon separated values: xxx.xx:yy.yyy
     */
    public FloatingDotArea(final byte[] data) {
        mDocument = this.assemble(data);
    }

    /**
     * Construct a floating dot area with specified origin and default size.
     * @param data
     * A sequence of bytes containing a newline separated line of colon separated values: xxx.xx:yy.yyy
     * @param origoX
     * Origin x value in centimetres
     * @param origoY
     * Origin y value in centimetres
     */
    public FloatingDotArea(final byte[] data, final int origoX, final int origoY) {
        this.mOrigoX = origoX;
        this.mOrigoY = origoY;
        mDocument = this.assemble(data);
    }

    /**
     * Construct a floating dot area with specified origin and size.
     * @param data
     * A sequence of bytes containing a newline separated line of colon separated values: xxx.xx:yy.yyy
     * @param origoX
     * Origin x value in centimeters
     * @param origoY
     * Origin y value in centimetres
     * @param widthX
     * Area width in millimeters
     * @param heightY
     * Area height in millimeters
     */
    public FloatingDotArea(
            final byte[] data,
            final int origoX,
            final int origoY,
            final int widthX,
            final int heightY
    ) {
        this.mOrigoX = origoX;
        this.mOrigoY = origoY;
        this.mWidthX = widthX;
        this.mHeightY = heightY;
        mDocument = this.assemble(data);
    }

    @Override
    public byte[] assemble(final byte[] data) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {

            stream.write(mActivateDotArea);
            if ((mOrigoX >= 0) && (mOrigoY >= 0)) {
                stream.write(mParameterOrigo);
                //stream.write("(5.00, 3.00)".getBytes());
                stream.write("5.00:0.00".getBytes());
            }
            if (mWidthX >= 0) {
                stream.write(mComma);
                stream.write(mParameterWidth);
                stream.write(Integer.toString(mWidthX).getBytes());
            }
            if (mHeightY >= 0) {
                stream.write(mComma);
                stream.write(mParameterHeight);
                stream.write(Integer.toString(mHeightY).getBytes());
            }
            stream.write(mSemicolon);
            stream.write(mNewLine);

            stream.write(data);

            //stream.write(parseData(data));

            stream.write(mSemicolon);

        } catch (IOException e) {
            e.getMessage();
        }
        mDocument = stream.toByteArray();
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
