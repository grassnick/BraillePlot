package de.tudresden.inf.mci.brailleplot.csvparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

/**
 * @author Richard Schmidt
 */
public class CsvDotParserTest {

    public static CsvParser parser;

    @BeforeAll
    public static void initialize() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream csvStream = classloader.getResourceAsStream("examples/csv/1_scatter_plot.csv");
        Reader csvReader = new BufferedReader(new InputStreamReader(csvStream));
        try {
            parser = new CsvParser(csvReader, ',', '\"');
        } catch (Exception e) {
            Assertions.fail();
        }

    }

    @Test
    public void testHorizontalParsing() {
        PointListContainer<PointList> container = parser.parse(CsvType.DOTS, CsvOrientation.HORIZONTAL);
        Iterator<PointList> containerIt = container.iterator();
        PointList list_1 = containerIt.next();
        Assertions.assertEquals(list_1.getName(), "Linie1");

        Iterator<Point2DDouble> it = list_1.getListIterator();

        Point2DDouble point = it.next();
        Assertions.assertEquals(point.getX(), 1.0);
        Assertions.assertEquals(point.getY(), 1.0);

        point = it.next();
        Assertions.assertEquals(point.getX(), 7.0);
        Assertions.assertEquals(point.getY(), 2.0);

        point = it.next();
        Assertions.assertEquals(point.getX(), 9.0);
        Assertions.assertEquals(point.getY(), 5.0);

        point = it.next();
        Assertions.assertEquals(point.getX(), 2.0);
        Assertions.assertEquals(point.getY(), 4.0);

        point = it.next();
        Assertions.assertEquals(point.getX(), 10.0);
        Assertions.assertEquals(point.getY(), 10.0);

        PointList list_2 = containerIt.next();
        Assertions.assertEquals(list_2.getName(), "Linie2");

        it = list_2.getListIterator();

        point = it.next();
        Assertions.assertEquals(point.getX(), 0.0);
        Assertions.assertEquals(point.getY(), 3.0);

        point = it.next();
        Assertions.assertEquals(point.getX(), 2.0);
        Assertions.assertEquals(point.getY(), 9.0);

        point = it.next();
        Assertions.assertEquals(point.getX(), 7.0);
        Assertions.assertEquals(point.getY(), 4.0);

        point = it.next();
        Assertions.assertEquals(point.getX(), 9.0);
        Assertions.assertEquals(point.getY(), 2.0);

        point = it.next();
        Assertions.assertEquals(point.getX(), 1.0);
        Assertions.assertEquals(point.getY(), 5.0);

        point = it.next();
        Assertions.assertEquals(point.getX(), 4.0);
        Assertions.assertEquals(point.getY(), 7.0);

    }
/*
    @Test
    public void testVerticalParsing() {

    }
*/
}
