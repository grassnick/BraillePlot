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
import java.util.List;

/**
 * @author Richard Schmidt
 */
public class CsvXAlignedParserTest {

    public static CsvParser parser;

    @BeforeAll
    public static void initialize() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream csvStream = classloader.getResourceAsStream("examples/csv/0_bar_chart.csv");
        Reader csvReader = new BufferedReader(new InputStreamReader(csvStream));
        try {
            parser = new CsvParser(csvReader, ',', '\"');
        } catch (Exception e) {

        }

    }

    @Test
    public void testVerticalParsing() {
        PointListContainer<PointList> container = parser.parse(CsvType.X_ALIGNED, CsvOrientation.VERTICAL);
        Iterator containerIt = container.iterator();
        PointList list_1 = (PointList) containerIt.next();
        Assertions.assertEquals(list_1.getName(), "a");

        Iterator it = list_1.getListIterator();

        Point2DDouble point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 0.0);
        Assertions.assertEquals(point.getY(), 1.0);

        PointList list_2 = (PointList) containerIt.next();
        Assertions.assertEquals(list_2.getName(), "b");

        it = list_2.getListIterator();

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 0.0);
        Assertions.assertEquals(point.getY(), 2.2);

        PointList list_3 = (PointList) containerIt.next();
        Assertions.assertEquals(list_3.getName(), "c");

        it = list_3.getListIterator();

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 0.0);
        Assertions.assertEquals(point.getY(), 3.0);

        PointList list_4 = (PointList) containerIt.next();
        Assertions.assertEquals(list_4.getName(), "d");

        it = list_4.getListIterator();

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 0.0);
        Assertions.assertEquals(point.getY(), 4.4);

        PointList list_5 = (PointList) containerIt.next();
        Assertions.assertEquals(list_5.getName(), "e");

        it = list_5.getListIterator();

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 0.0);
        Assertions.assertEquals(point.getY(), 5.0);

        PointList list_6 = (PointList) containerIt.next();
        Assertions.assertEquals(list_6.getName(), "f");

        it = list_6.getListIterator();

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 0.0);
        Assertions.assertEquals(point.getY(), 6.0);

        PointList list_7 = (PointList) containerIt.next();
        Assertions.assertEquals(list_7.getName(), "g");

        it = list_7.getListIterator();

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 0.0);
        Assertions.assertEquals(point.getY(), 7.0);

    }
/*
    @Test
    public void testHorizontalParsing() {

    }
*/
}
