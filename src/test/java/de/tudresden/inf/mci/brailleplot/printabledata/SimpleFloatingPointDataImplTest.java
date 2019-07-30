package de.tudresden.inf.mci.brailleplot.printabledata;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.ArrayList;
import java.util.Iterator;

import static tec.units.ri.unit.Units.METRE;

class SimpleFloatingPointDataImplTest {

    FloatingPointData<Integer> emptyIntFloatingPointData() {
        return new SimpleFloatingPointDataImpl<>(new Printer(new ArrayList<>()), new Format(new ArrayList<>()));
    }

    Point2DValued<Quantity<Length>, Integer> point2dInt(Integer one, Integer two, Integer val) {
        return new Point2DValued<>(Quantities.getQuantity(one, METRE), Quantities.getQuantity(two, METRE), val);
    }

    @Test
    void testAddPointNull() {
        FloatingPointData<Integer> data = emptyIntFloatingPointData();
        Assertions.assertThrows(NullPointerException.class, () -> {
            data.addPoint(null);
        });
    }

    @Test
    void testAddPointAndIterator() {
        FloatingPointData<Integer> data = emptyIntFloatingPointData();
        for (int i = 0; i < 5; i++) {
            data.addPoint(point2dInt(i, i, i));
        }
        Iterator<Point2DValued<Quantity<Length>, Integer>> it = data.getIterator();
        int i = 0;
        while (it.hasNext()) {
            Point2DValued<Quantity<Length>, Integer> point = it.next();
            Assertions.assertEquals(point, point2dInt(i, i, i));
            i++;
        }
    }
}
