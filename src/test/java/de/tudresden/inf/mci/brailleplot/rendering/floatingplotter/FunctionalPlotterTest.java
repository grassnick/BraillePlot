package de.tudresden.inf.mci.brailleplot.rendering.floatingplotter;

import de.tudresden.inf.mci.brailleplot.configparser.ConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.diagrams.LinePlot;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author Leonard Kupper and Richard Schmidt
 */
class FunctionalPlotterTest {

    private static final String mDefaultConfig = getResource("config/plotter_test_default.properties").getAbsolutePath();
    private static final String mBaseConfig = getResource("config/base_format.properties").getAbsolutePath();
    private static Printer mPrinter;
    private static Format mFormat;

    static File getResource(String fileName) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }

    @BeforeAll
    static void initialize() {
        Assertions.assertDoesNotThrow(
                () -> {
                    ConfigurationParser parser = new JavaPropertiesConfigurationParser(mBaseConfig, mDefaultConfig);
                    mPrinter = parser.getPrinter();
                    mFormat = parser.getFormat("test");
                }
        );
    }

    // Invalid use test cases.

    @Test
    void testInvalidDirectCall() {
        // Create FunctionalPlotter
        FunctionalPlotter<ScatterPlot> plotter = new FunctionalPlotter<ScatterPlot>(ScatterPlot.class, (data, canvas) -> {
            // dummy
            return 0;
        });

        // The FunctionalRasterizer should not be called directly, it is meant to be called by its RenderingBase
        // which decides which plotter to use based on the Renderable type.
        // Directly passing the wrong Renderable type must cause exception:
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PlotCanvas testCanvas = new PlotCanvas(mPrinter, mFormat);
            // Pass Image to BrailleText rasterizer.
            plotter.plot(new LinePlot(new SimplePointListContainerImpl()), testCanvas);
        });
    }
}
