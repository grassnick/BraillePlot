package de.tudresden.inf.mci.brailleplot.datacontainers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Richard Schmidt
 */
public class SimpleCategoricalPointListContainerImplTest {

    public static SimpleCategoricalPointListContainerImpl container;

    @BeforeAll
    public static void initialize() {
        container = new SimpleCategoricalPointListContainerImpl();
    }

    @Test
    public void testPushBackCategory() {

        Assertions.assertThrows(NullPointerException.class, () -> {container.pushBackCategory(null);});

        int a = container.pushBackCategory("Kat. 1");
        int b = container.pushBackCategory("Kat. 2");

        Assertions.assertEquals(0, a);
        Assertions.assertEquals(1, b);

        Assertions.assertEquals("Kat. 1", container.getCategory(0));
        Assertions.assertEquals("Kat. 2", container.getCategory(1));
    }

    @Test
    public void testGetCategory() {

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {container.getCategory(-1);});
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {container.getCategory(2);});

    }
}
