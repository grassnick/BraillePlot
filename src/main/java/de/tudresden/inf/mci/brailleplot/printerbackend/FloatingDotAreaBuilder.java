package de.tudresden.inf.mci.brailleplot.printerbackend;


import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleFloatingPointDataImpl;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import static tec.units.ri.unit.Units.METRE;

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
    byte[] assemble(final SimpleFloatingPointDataImpl<Boolean> data){
        mData = Objects.requireNonNull(data);
        Iterator<Point2DValued<Quantity<Length>, Boolean>> iter = mData.getIterator();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //FileOutputStream stream = null;

        /*
        try {
            stream = new FileOutputStream("PlsWork");
        } catch (FileNotFoundException e) {
            e.getCause();
        }

         */


        try {
            stream.write(mStartFloatingMode);
            // Here goes Origo, Width and Height

            // End of Origo etc.
            stream.write(mSemicolon);
            stream.write(mNewLine);
            // Start iteration over values
            while (iter.hasNext()) {
                Point2DValued<Quantity<Length>, Boolean> current = iter.next();
                Quantity<Length> x = current.getX().to(MetricPrefix.MILLI(METRE));
                Quantity<Length> y = current.getY().to(MetricPrefix.MILLI(METRE));
                String xString = String.format("%.2f", x.getValue().doubleValue()).replace(',','.');
                String yString = String.format("%.2f", y.getValue().doubleValue()).replace(',','.');
                stream.write(xString.getBytes());
                stream.write(mColon);
                stream.write(yString.getBytes());
                //stream.write(y.floatValue());
                if (iter.hasNext()) {
                    stream.write(mNewLine);
                }
            }
            // End with ;
            stream.write(mSemicolon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
        //return null;
    }
}
