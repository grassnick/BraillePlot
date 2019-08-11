package de.tudresden.inf.mci.brailleplot.csvparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
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
public class CsvXAlignedCategoriesParserTest {

    public static CsvParser parser;

    @BeforeAll
    public static void initialize() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream csvStream = classloader.getResourceAsStream("examples/csv/0_bar_chart_categorical.csv");
        Reader csvReader = new BufferedReader(new InputStreamReader(csvStream));
        try {
            parser = new CsvParser(csvReader, ',', '\"');
        } catch (Exception e) {

        }

    }

    @Test
    public void testVerticalParsing() {
        CategoricalPointListContainer<PointList> container = parser.parse(CsvType.X_ALIGNED_CATEGORIES, CsvOrientation.VERTICAL);
        Assertions.assertEquals(container.getCategory(1), " Reihe a ");
        Assertions.assertEquals(container.getCategory(2), " Reihe b ");
        Assertions.assertEquals(container.getCategory(3), " Reihe c ");
        Assertions.assertEquals(container.getCategory(4), " Reihe d");

        Iterator containerIt = container.iterator();
        PointList list_1 = (PointList) containerIt.next();
        Assertions.assertEquals(list_1.getName(), "Kat.1");

        Iterator it = list_1.getListIterator();

        Point2DDouble point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 1.0);
        Assertions.assertEquals(point.getY(), 3.0);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 2.0);
        Assertions.assertEquals(point.getY(), 2.5);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 3.0);
        Assertions.assertEquals(point.getY(), 1.0);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 4.0);
        Assertions.assertEquals(point.getY(), 3.0);

        PointList list_2 = (PointList) containerIt.next();
        Assertions.assertEquals(list_2.getName(), "Kat.2");

        it = list_2.getListIterator();

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 1.0);
        Assertions.assertEquals(point.getY(), 4.0);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 2.0);
        Assertions.assertEquals(point.getY(), 3.0);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 3.0);
        Assertions.assertEquals(point.getY(), 2.0);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 4.0);
        Assertions.assertEquals(point.getY(), 5.0);

        PointList list_3 = (PointList) containerIt.next();
        Assertions.assertEquals(list_3.getName(), "Kat.3");

        it = list_3.getListIterator();

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 1.0);
        Assertions.assertEquals(point.getY(), 4.5);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 2.0);
        Assertions.assertEquals(point.getY(), 3.0);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 3.0);
        Assertions.assertEquals(point.getY(), 1.0);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 4.0);
        Assertions.assertEquals(point.getY(), 0.2);

        PointList list_4 = (PointList) containerIt.next();
        Assertions.assertEquals(list_4.getName(), "Kat.4");

        it = list_4.getListIterator();

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 1.0);
        Assertions.assertEquals(point.getY(), 4.5);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 2.0);
        Assertions.assertEquals(point.getY(), 3.0);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 3.0);
        Assertions.assertEquals(point.getY(), 1.0);

        point = (Point2DDouble) it.next();
        Assertions.assertEquals(point.getX(), 4.0);
        Assertions.assertEquals(point.getY(), 0.2);

    }
/*
    @Test
    public void testHorizontalParsing() {

    }
*/
}
