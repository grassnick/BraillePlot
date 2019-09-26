package de.tudresden.inf.mci.brailleplot.printerbackend;


import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleFloatingPointDataImpl;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

/**
 * Class representing the FloatingDotArea protocol for the braille Index Everest V4 for printing
 * variable areas on paper via coordinates.
 * @author Andrey Ruzhanskiy, Leonard Kupper
 * @version 29.05.2019
 */

class FloatingDotAreaBuilder extends AbstractIndexV4Builder<SimpleFloatingPointDataImpl<Boolean>> {

    /**
     * Constructor. Does not have any functionality. Should only be used in  {@link PrintDirector}
     */
    FloatingDotAreaBuilder() { }

    /**
     * Currently not implemented.
     * @param data Raw data to be printed via the FloatingDotArea
     * @return Exception.
     */
    @Override
    byte[] assemble(final SimpleFloatingPointDataImpl data) {
        mData = Objects.requireNonNull(data);
        Iterator<Point2DValued<Quantity<Length>, Boolean>> iter = mData.getIterator();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(mStartFloatingMode);
            // Here goes Origo, Width and Height

            // End of Origo etc.
            stream.write(mSemicolon);
            stream.write(mNewLine);
            // Start iteration over values
            while (iter.hasNext()) {
                Point2DValued<Quantity<Length>, Boolean> current = iter.next();
                Number x = current.getX().getValue();
                Number y = current.getY().getValue();

                //stream.write(x.floatValue());
                stream.write(mColon);
                //stream.write(y.floatValue());
                stream.write(mNewLine);
            }
            // End with ;
            stream.write(mColon);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return stream.toByteArray();
    }


}
