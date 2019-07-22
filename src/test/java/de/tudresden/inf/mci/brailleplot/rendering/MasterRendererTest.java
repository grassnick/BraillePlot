package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.ConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;


public class MasterRendererTest {

    public static final String mDefaultConfig = getResource("rasterizer_test_default.properties").getAbsolutePath();
    public static final String mBaseConfig = getResource("base_format.properties").getAbsolutePath();
    public static Printer mPrinter;
    public static Format mFormat;

    public static File getResource(String fileName) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File resourceFile = new File(classLoader.getResource(fileName).getFile());
        return resourceFile;
    }

    @BeforeAll
    public static void initialize() {
        Assertions.assertDoesNotThrow(
                () -> {
                    ConfigurationParser parser = new JavaPropertiesConfigurationParser(mBaseConfig, mDefaultConfig);
                    mPrinter = parser.getPrinter();
                    mFormat = parser.getFormat("test");
                }
        );
    }

    // Valid use test cases.

    @Test
    public void testRasterizerSelection() {
        Assertions.assertDoesNotThrow(
                () -> {
                    // Create own rendering base
                    FunctionalRenderingBase renderingBase = new FunctionalRenderingBase();

                    // Register two different rasterizers for two different types.
                    // The rasterizers are later distinguished by the number of pages they generate.
                    FunctionalRasterizer<BrailleText> rasterizerRef1 = new FunctionalRasterizer<>(BrailleText.class, (data, canvas) -> {
                        for (int i = 0; i < 1; i++) {
                            canvas.getNewPage();
                        }
                    });
                    FunctionalRasterizer<Image> rasterizerRef2 = new FunctionalRasterizer<>(Image.class, (data, canvas) -> {
                        for (int i = 0; i < 2; i++) {
                            canvas.getNewPage();
                        }
                    });
                    renderingBase.registerRasterizer(rasterizerRef1);
                    renderingBase.registerRasterizer(rasterizerRef2);

                    // create renderer from rendering base
                    MasterRenderer renderer = new MasterRenderer(mPrinter, mFormat, renderingBase);

                    // Test rasterizer selection
                    RasterCanvas result;

                    result= renderer.rasterize(new BrailleText("dummy text", new Rectangle(0,0,1,1)));
                    Assertions.assertEquals(1, result.getPageCount());

                    result = renderer.rasterize(new Image(getResource("dummy.bmp")));
                    Assertions.assertEquals(2, result.getPageCount());

                    // Test replacement of rasterizer
                    FunctionalRasterizer<Image> rasterizerRef3 = new FunctionalRasterizer<>(Image.class, (data, canvas) -> {
                        for (int i = 0; i < 3; i++) {
                            canvas.getNewPage();
                        }
                    });
                    renderingBase.registerRasterizer(rasterizerRef3);

                    result = renderer.rasterize(new Image(getResource("dummy.bmp")));
                    Assertions.assertEquals(3, result.getPageCount());

                }
        );
    }

    // Invalid use test cases.

    @Test
    public void testRasterizerNotAvailable() {
        // Create MasterRenderer with empty rendering base.
        MasterRenderer empty = new MasterRenderer(mPrinter, mFormat, new FunctionalRenderingBase());

        Assertions.assertThrows(IllegalArgumentException.class, () -> empty.rasterize(new Image(getResource("dummy.bmp"))));
    }
}
