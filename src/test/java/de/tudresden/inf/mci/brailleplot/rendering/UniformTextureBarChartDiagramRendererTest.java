package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.configparser.ConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

public class UniformTextureBarChartDiagramRendererTest {

    public static Printer mPrinterConfig;
    public static Format mFormatConfig;
    public static UniformTextureBarChartDiagramRenderer mRasterizer;

    public static File getResource(String fileName) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File resourceFile = new File(classLoader.getResource(fileName).getFile());
        return resourceFile;
    }

    @BeforeAll
    public static void initialize() {
        Assertions.assertDoesNotThrow(() -> {
            // Parse configuration
            String configPath = getResource("dummyPrinterConfig.properties").getAbsolutePath();
            ConfigurationParser parser = new JavaPropertiesConfigurationParser(configPath);
            mPrinterConfig = parser.getPrinter();
            mFormatConfig = parser.getFormat("B5");
        });
    }

    // Correct use testcases
    @Test
    public void testRasterizerConstruction() {
        Assertions.assertDoesNotThrow(() -> {
            mRasterizer = new UniformTextureBarChartDiagramRenderer(mPrinterConfig, mFormatConfig);
        });
    }

}
