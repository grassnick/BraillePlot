package de.tudresden.inf.mci.brailleplot.datacontainers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

/**
 * @author Richard Schmidt
 */
public class SimpleCategoricalPointListContainerImplTest {

    public static SimpleCategoricalPointListContainerImpl container;
    public static NullPointerException e;
    public static IndexOutOfBoundsException b;

    @BeforeAll
    public static void initialize() {
        container = new SimpleCategoricalPointListContainerImpl();
        e = new NullPointerException();
        b = new IndexOutOfBoundsException();
    }

    @Test
    public void testPushBackCategory() {
        try {
            container.pushBackCategory(null);}
        catch (Exception exc) {
            Assertions.assertEquals(e.getCause(), exc.getCause());
        }

        int a = container.pushBackCategory("Kat. 1");
        int b = container.pushBackCategory("Kat. 2");

        Assertions.assertEquals(0, a);
        Assertions.assertEquals(1, b);

        Assertions.assertEquals("Kat. 1", container.getCategory(0));
        Assertions.assertEquals("Kat. 2", container.getCategory(1));
    }

    @Test
    public void testGetCategory() {
        try {
            container.getCategory(-1);}
        catch (Exception exc) {
            Assertions.assertEquals(b.getCause(), exc.getCause());
            System.out.print("hier");
        }

        try {
            container.getCategory(2);}
        catch (Exception exc) {
            Assertions.assertEquals(b.getCause(), exc.getCause());
            System.out.print("hier");
        }
    }
}
