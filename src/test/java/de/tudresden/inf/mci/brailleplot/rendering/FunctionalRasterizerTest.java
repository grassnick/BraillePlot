package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.*;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.SixDotBrailleRasterCanvas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;


public class FunctionalRasterizerTest {

    public static final URL mDefaultConfig = getResource("config/rasterizer_test_default.properties");
    public static final URL mBaseConfig = getResource("config/base_format.properties");
    public static Printer mPrinter;
    public static Representation mRepresentation;
    public static Format mFormat;

    public static URL getResource(final String location) {
        return ClassLoader.getSystemClassLoader().getResource(location);
    }

    @BeforeAll
    public static void initialize() {
        Assertions.assertDoesNotThrow(
                () -> {
                    ConfigurationParser parser = new JavaPropertiesConfigurationParser(mBaseConfig, mDefaultConfig);
                    mPrinter = parser.getPrinter();
                    mRepresentation = parser.getRepresentation();
                    mFormat = parser.getFormat("test");
                }
        );
    }

    // Invalid use test cases.

    @Test
    public void testInvalidDirectCall() {
        // Create FunctionalRasterizer for BrailleText
        FunctionalRasterizer<BrailleText> textRasterizer = new FunctionalRasterizer<>(BrailleText.class, (data, canvas) -> {
            // dummy
        });

        // The FunctionalRasterizer should not be called directly, it is meant to be called by its RenderingBase
        // which decides which rasterizer to use based on the Renderable type.
        // Directly passing the wrong Renderable type must cause exception:
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RasterCanvas testCanvas = new SixDotBrailleRasterCanvas(mPrinter, mRepresentation, mFormat);
            // Pass Image to BrailleText rasterizer.
            textRasterizer.rasterize(new Image(getResource("examples/img/dummy.bmp")), testCanvas);
        });
    }
}
