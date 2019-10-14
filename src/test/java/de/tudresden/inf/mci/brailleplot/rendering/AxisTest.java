package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Not the best design, but it does what it needs to do.
 * @author Andrey Ruzhanskiy
 * @version 28.09.2019
 */
public class AxisTest {

    @BeforeAll
    public static void testConstructorValid() {
        Assertions.assertDoesNotThrow(() -> {
            Axis xAxis = new Axis(Axis.Type.X_AXIS,0,0,0.5,1);
            Axis yAxis = new Axis(Axis.Type.Y_AXIS,0,0,0.5,1);
        });
    }

    @Test
    public void testMethodsDoNotThrow() {
        Axis xAxis = new Axis(Axis.Type.X_AXIS,0,0,0.5,1);
        Assertions.assertDoesNotThrow(() -> {
            xAxis.getStepWidth();
            xAxis.getType();
            xAxis.getOriginX();
            xAxis.getOriginY();
            xAxis.getTickSize();
            xAxis.setLabels(new HashMap<Integer, String>());
            xAxis.setBoundary(new Rectangle(0,0,0,0));
        });
    }

    @Test
    public void testGetters() {
        Axis xAxis = new Axis(Axis.Type.X_AXIS,0.5,0,1,2);
        Assertions.assertEquals(Axis.Type.X_AXIS, xAxis.getType());
        Assertions.assertEquals(0.5, xAxis.getOriginX());
        Assertions.assertEquals(0, xAxis.getOriginY());
        Assertions.assertEquals(1, xAxis.getStepWidth());
        Assertions.assertEquals(2, xAxis.getTickSize());
        HashMap<Integer, String> test = new HashMap<Integer, String>();
        xAxis.setLabels(test);
        Assertions.assertEquals(test, xAxis.getLabels());
        Rectangle rect = new Rectangle(0,0,0,0);
        xAxis.setBoundary(rect);
        Assertions.assertEquals(xAxis.hasLabels(), true);
        Assertions.assertEquals(xAxis.hasBoundary(), true);
        Assertions.assertEquals(xAxis.getBoundary(), rect);

    }



    @Test
    public void testWrongTickSize() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Axis wrongTick = new Axis(Axis.Type.X_AXIS,0.5,0,0,0);
        });
    }
}
