package de.tudresden.inf.mci.brailleplot.datacontainers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Richard Schmidt
 */
class SimplePointListContainerImplTest {

    private static List<PointList> elements;

    @BeforeAll
    static void initialize() {
        elements = new LinkedList<>();
        PointList list = new SimplePointListImpl();
        list.setName("test_list");
        elements.add(list);

    }

    @Test
    void testSimplePointListContainerImpl() {
        SimplePointListContainerImpl container = new SimplePointListContainerImpl(elements);
        Iterator<PointList> it = container.iterator();
        PointList my_list = it.next();
        Assertions.assertEquals("test_list", my_list.getName());
    }
}
