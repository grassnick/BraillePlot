package de.tudresden.inf.mci.brailleplot.printabledata;

import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static tec.units.ri.unit.Units.METRE;

class Point2DValuedTest {

    private Quantity<Length> length() {
        return Quantities.getQuantity(5, MetricPrefix.CENTI(METRE));
    }

    private Integer integer() {
        return 42;
    }

    @Test
    void testConstructorNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Point2DValued<>(null, length(), 3);
            new Point2DValued<>( length(), null, 3);
            new Point2DValued<>( length(), length(), null);
        });
    }

    @Test
    void testSetGet() {
        Point2DValued<Quantity<Length>, Integer> point = new Point2DValued<>(length(), length(), integer());
        Assertions.assertEquals(point.getVal(), integer());
        Assertions.assertEquals(point.getX(), length());
        Assertions.assertEquals(point.getY(), length());
    }
}