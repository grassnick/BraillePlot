package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.ConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ListIterator;


public class RasterCanvasTest {

    public static final String mDefaultConfig = getResource("default.properties").getAbsolutePath();
    public static final String mBaseConfig = getResource("base_format.properties").getAbsolutePath();
    public static final String mMarginsOnlyConfig = getResource("margins_only.properties").getAbsolutePath();
    public static final String mConstraintOnlyConfig = getResource("constraint_only.properties").getAbsolutePath();
    public static final String mBothConfig = getResource("margins_and_constraint.properties").getAbsolutePath();

    public static File getResource(String fileName) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File resourceFile = new File(classLoader.getResource(fileName).getFile());
        return resourceFile;
    }

    @Test
    public void testBaseFormat() {
        Assertions.assertDoesNotThrow(
                () -> {
                    ConfigurationParser parser = new JavaPropertiesConfigurationParser(mBaseConfig, mDefaultConfig);
                    RasterCanvas canvas = new SixDotBrailleRasterCanvas(parser.getPrinter(), parser.getFormat("test"));

                    // pre-calculated and measured correct values:
                    int x = 0;
                    int y = 0;
                    int w = 35;
                    int h = 30;
                    double printW = (w * (2.5 + 3.5)) - 3.5; // 206.5 mm
                    double printH = (h * (2 * 2.5 + 5.0)) - 5.0; // 295.0 mm

                    // Test the calculated raster against the pre-calculated values
                    Rectangle raster = canvas.getCellRectangle();
                    Assertions.assertEquals(x, raster.getX());
                    Assertions.assertEquals(y, raster.getY());
                    Assertions.assertEquals(w, raster.getWidth());
                    Assertions.assertEquals(h, raster.getHeight());
                    Assertions.assertEquals(x, raster.intWrapper().getX());
                    Assertions.assertEquals(y, raster.intWrapper().getY());
                    Assertions.assertEquals(w, raster.intWrapper().getWidth());
                    Assertions.assertEquals(h, raster.intWrapper().getHeight());
                    Assertions.assertEquals(printW, canvas.getPrintableWidth());
                    Assertions.assertEquals(printH, canvas.getPrintableHeight());

                    // Test quantification (by equivalence class partitioning testing)
                    Assertions.assertEquals(0, canvas.quantifyX(-5));
                    Assertions.assertEquals(0, canvas.quantifyX(0));
                    Assertions.assertEquals(9, canvas.quantifyX(28.25));
                    Assertions.assertEquals(10, canvas.quantifyX(28.26));
                    Assertions.assertEquals(69, canvas.quantifyX(206.5));
                    Assertions.assertEquals(69, canvas.quantifyX(250));

                    Assertions.assertEquals(0, canvas.quantifyY(-5));
                    Assertions.assertEquals(0, canvas.quantifyY(0));
                    Assertions.assertEquals(15, canvas.quantifyY(51.25));
                    Assertions.assertEquals(16, canvas.quantifyY(51.26));
                    Assertions.assertEquals(89, canvas.quantifyY(295.0));
                    Assertions.assertEquals(89, canvas.quantifyY(350.0));
                }
        );
    }

    @Test
    public void testMarginsOnly() {
        Assertions.assertDoesNotThrow(
                () -> {
                    ConfigurationParser parser = new JavaPropertiesConfigurationParser(mMarginsOnlyConfig, mDefaultConfig);
                    RasterCanvas canvas = new SixDotBrailleRasterCanvas(parser.getPrinter(), parser.getFormat("test"));

                    // pre-calculated and measured correct values:
                    // 6 mm left margin -> 1 cell border
                    // 12 mm top margin -> ~ 1.2 cell sizes -> 2 cell border
                    // 30 mm bottom margin -> 3 cell border
                    int x = 1;
                    int y = 2;
                    int w = 34; // 35 - 1
                    int h = 25; // 30 - 2 - 3
                    double printW = (w * (2.5 + 3.5)) - 3.5; // 200.5 mm
                    double printH = (h * (2 * 2.5 + 5.0)) - 5.0; // 245.0 mm

                    // Test the calculated raster against the pre-calculated values
                    Rectangle raster = canvas.getCellRectangle();
                    Assertions.assertEquals(x, raster.getX());
                    Assertions.assertEquals(y, raster.getY());
                    Assertions.assertEquals(w, raster.getWidth());
                    Assertions.assertEquals(h, raster.getHeight());
                    Assertions.assertEquals(x, raster.intWrapper().getX());
                    Assertions.assertEquals(y, raster.intWrapper().getY());
                    Assertions.assertEquals(w, raster.intWrapper().getWidth());
                    Assertions.assertEquals(h, raster.intWrapper().getHeight());
                    Assertions.assertEquals(printW, canvas.getPrintableWidth());
                    Assertions.assertEquals(printH, canvas.getPrintableHeight());
                }
        );
    }

    @Test
    public void testConstraintOnly() {
        Assertions.assertDoesNotThrow(
                () -> {
                    ConfigurationParser parser = new JavaPropertiesConfigurationParser(mConstraintOnlyConfig, mDefaultConfig);
                    RasterCanvas canvas = new SixDotBrailleRasterCanvas(parser.getPrinter(), parser.getFormat("test"));

                    // pre-calculated and measured correct values:
                    // width-constraint: 190.0 mm -> fits 32 cells h.
                    // height-constraint: 250.0 mm -> fits 25 cells v.
                    int x = 0; // zero because constraint
                    int y = 0; // moves reference point.
                    int w = 30; // because raster.constraint.width = 30 < 32 (will pick minimum)
                    int h = 25; // because 25 < raster.constraint.height = 28
                    double printW = (w * (2.5 + 3.5)) - 3.5; // 176.5 mm
                    double printH = (h * (2 * 2.5 + 5.0)) - 5.0; // 245.0 mm

                    // Test the calculated raster against the pre-calculated values
                    Rectangle raster = canvas.getCellRectangle();
                    Assertions.assertEquals(x, raster.getX());
                    Assertions.assertEquals(y, raster.getY());
                    Assertions.assertEquals(w, raster.getWidth());
                    Assertions.assertEquals(h, raster.getHeight());
                    Assertions.assertEquals(x, raster.intWrapper().getX());
                    Assertions.assertEquals(y, raster.intWrapper().getY());
                    Assertions.assertEquals(w, raster.intWrapper().getWidth());
                    Assertions.assertEquals(h, raster.intWrapper().getHeight());
                    Assertions.assertEquals(printW, canvas.getPrintableWidth());
                    Assertions.assertEquals(printH, canvas.getPrintableHeight());
                }
        );
    }

    @Test
    public void testBoth() {
        Assertions.assertDoesNotThrow(
                () -> {
                    ConfigurationParser parser = new JavaPropertiesConfigurationParser(mBothConfig, mDefaultConfig);
                    RasterCanvas canvas = new SixDotBrailleRasterCanvas(parser.getPrinter(), parser.getFormat("test"));

                    // pre-calculated and measured correct values:
                    // width-constraint: 190.0 mm -> fits 32 cells h.
                    // height-constraint: 250.0 mm -> fits 25 cells v.
                    int x = 0;
                    int y = 1;
                    int w = 30;
                    int h = 24;
                    double printW = (w * (2.5 + 3.5)) - 3.5; // 176.5 mm
                    double printH = (h * (2 * 2.5 + 5.0)) - 5.0; // 235.0 mm

                    // Test the calculated raster against the pre-calculated values
                    Rectangle raster = canvas.getCellRectangle();
                    Assertions.assertEquals(x, raster.getX());
                    Assertions.assertEquals(y, raster.getY());
                    Assertions.assertEquals(w, raster.getWidth());
                    Assertions.assertEquals(h, raster.getHeight());
                    Assertions.assertEquals(x, raster.intWrapper().getX());
                    Assertions.assertEquals(y, raster.intWrapper().getY());
                    Assertions.assertEquals(w, raster.intWrapper().getWidth());
                    Assertions.assertEquals(h, raster.intWrapper().getHeight());
                    Assertions.assertEquals(printW, canvas.getPrintableWidth());
                    Assertions.assertEquals(printH, canvas.getPrintableHeight());
                }
        );
    }

    @Test @SuppressWarnings("unchecked")
    // RasterCanvas is guaranteed to create MatrixData instances for its pages
    public void testPageIterator() {
        Assertions.assertDoesNotThrow(
                () -> {
                    ConfigurationParser parser = new JavaPropertiesConfigurationParser(mBaseConfig, mDefaultConfig);
                    MasterRenderer renderer = new MasterRenderer(parser.getPrinter(), parser.getFormat("test"));
                    RasterCanvas result = renderer.rasterize(new Image(getResource("dummy.bmp")));
                    ListIterator iter = result.getPageIterator();
                    while (iter.hasNext()) {
                        MatrixData<Boolean> page = (MatrixData<Boolean>) iter.next();
                        Assertions.assertNotNull(page);
                    }
                }
        );
    }
}
