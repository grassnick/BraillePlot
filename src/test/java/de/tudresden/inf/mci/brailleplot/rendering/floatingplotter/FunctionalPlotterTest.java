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
import java.net.URL;
import java.util.Objects;

/**
 * @author Leonard Kupper and Richard Schmidt
 */
class FunctionalPlotterTest {

    private static final URL mDefaultConfig = getResource("config/plotter_test_default.properties");
    private static final URL mBaseConfig = getResource("config/base_format.properties");
    private static Printer mPrinter;
    private static Format mFormat;

    static URL getResource(final String location) {
        return ClassLoader.getSystemClassLoader().getResource(location);
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
