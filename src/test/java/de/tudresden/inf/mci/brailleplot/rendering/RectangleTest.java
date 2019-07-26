package de.tudresden.inf.mci.brailleplot.rendering;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RectangleTest {

    Rectangle rect1 = new Rectangle(4.5, 2.75, 8.3, 16);
    Rectangle rect2 = new Rectangle(8.5, 6.75, 8.3, 16);
    Rectangle rect3 = new Rectangle(0, 0, 3, 2);

    @Test
    public void testDimensions() {
        Assertions.assertEquals(4.5, rect1.getX());
        Assertions.assertEquals(2.75, rect1.getY());
        Assertions.assertEquals(8.3, rect1.getWidth());
        Assertions.assertEquals(16, rect1.getHeight());
        Assertions.assertEquals(12.8, rect1.getRight());
        Assertions.assertEquals(18.75, rect1.getBottom());
    }

    @Test
    public void testIntegerCoordinates() {
        Rectangle.IntWrapper intRect;

        // with rational original coordinates (get rounded)
        intRect = rect1.intWrapper();
        Assertions.assertEquals(5, intRect.getX());
        Assertions.assertEquals(3, intRect.getY());
        Assertions.assertEquals(8, intRect.getWidth());
        Assertions.assertEquals(16, intRect.getHeight());
        Assertions.assertEquals(12, intRect.getRight());
        Assertions.assertEquals(18, intRect.getBottom());

        // with natural original coordinates
        intRect = rect3.intWrapper();
        Assertions.assertEquals(0, intRect.getX());
        Assertions.assertEquals(0, intRect.getY());
        Assertions.assertEquals(3, intRect.getWidth());
        Assertions.assertEquals(2, intRect.getHeight());
        Assertions.assertEquals(2, intRect.getRight());
        Assertions.assertEquals(1, intRect.getBottom());
    }

    @Test
    public void testScaling() {
        Rectangle scaledRect = rect1.scaledBy(5.5, 0.4);
        Assertions.assertEquals(4.5 * 5.5, scaledRect.getX());
        Assertions.assertEquals(2.75 * 0.4, scaledRect.getY());
        Assertions.assertEquals(8.3 * 5.5, scaledRect.getWidth());
        Assertions.assertEquals(16 * 0.4, scaledRect.getHeight());
        Assertions.assertEquals(12.8 * 5.5, scaledRect.getRight());
        Assertions.assertEquals(18.75 * 0.4, scaledRect.getBottom());
    }

    @Test
    public void testIntersecting() {
        Rectangle itsct;

        // Non-empty intersection
        itsct = rect1.intersectedWith(rect2);
        Assertions.assertEquals(8.5, itsct.getX());
        Assertions.assertEquals(6.75, itsct.getY());
        Assertions.assertEquals(12.8 - 8.5, itsct.getWidth());
        Assertions.assertEquals(18.75 - 6.75, itsct.getHeight());
        Assertions.assertEquals(12.8, itsct.getRight());
        Assertions.assertEquals(18.75, itsct.getBottom());

        // Swapping order of rectangles gives equivalent intersection
        itsct = rect2.intersectedWith(rect1);
        Assertions.assertEquals(8.5, itsct.getX());
        Assertions.assertEquals(6.75, itsct.getY());
        Assertions.assertEquals(12.8 - 8.5, itsct.getWidth());
        Assertions.assertEquals(18.75 - 6.75, itsct.getHeight());
        Assertions.assertEquals(12.8, itsct.getRight());
        Assertions.assertEquals(18.75, itsct.getBottom());

        // Empty intersection
        itsct = rect1.intersectedWith(rect3);
        Assertions.assertEquals(4.5, itsct.getX());
        Assertions.assertEquals(2.75, itsct.getY());
        Assertions.assertEquals(0, itsct.getWidth());
        Assertions.assertEquals(0, itsct.getHeight());
        Assertions.assertEquals(4.5, itsct.getRight());
        Assertions.assertEquals(2.75, itsct.getBottom());
    }

    @Test
    public void testCropping() {
        Assertions.assertDoesNotThrow(() -> {
            Rectangle copy, crop;

            // Create copy by selecting whole width / height (should not throw)
            copy = rect1.fromTop(16);
            copy = rect1.fromBottom(16);
            copy = rect1.fromLeft(8.3);
            copy = rect1.fromBottom(8.3);

            // Create copy with copy constructor
            copy = new Rectangle(rect1);

            crop = copy.removeFromTop(1);
            Assertions.assertEquals(2.75, crop.getY());
            Assertions.assertEquals(1, crop.getHeight());

            crop = copy.removeFromLeft(2);
            Assertions.assertEquals(4.5, crop.getX());
            Assertions.assertEquals(2, crop.getWidth());

            crop = copy.removeFromBottom(3);
            Assertions.assertEquals(18.75, crop.getBottom());
            Assertions.assertEquals(3, crop.getHeight());

            crop = copy.removeFromRight(4);
            Assertions.assertEquals(12.8, crop.getRight());
            Assertions.assertEquals(4, crop.getWidth());

            Assertions.assertEquals(4.5 + 2, copy.getX());
            Assertions.assertEquals(2.75 + 1, copy.getY());
            Assertions.assertEquals(8.3 - 2 - 4, copy.getWidth());
            Assertions.assertEquals(16 - 1 - 3, copy.getHeight());
            Assertions.assertEquals(12.8 - 4, copy.getRight());
            Assertions.assertEquals(18.75 - 3, copy.getBottom());

        });
    }

    @Test
    public void testOutOfSpace() {
        Assertions.assertThrows(Rectangle.OutOfSpaceException.class, () -> {
            Rectangle copy, crop;

            copy = new Rectangle(rect1);

            // selecting more space than available
            crop = copy.fromTop(16.01);

        });
    }

}
