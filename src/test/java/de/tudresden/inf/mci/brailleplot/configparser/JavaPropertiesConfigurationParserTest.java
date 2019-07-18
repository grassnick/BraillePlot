package de.tudresden.inf.mci.brailleplot.configparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;


public class JavaPropertiesConfigurationParserTest {

    public static Printer mPrinterConfig;
    public static Format mFormatConfig;

    public static File getResource(String fileName) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File resourceFile = new File(classLoader.getResource(fileName).getFile());
        return resourceFile;
    }

    // Correct use testcases
    @Test @BeforeAll
    public static void testSuccessfulParsing() {
        String defaultConfigPath = getResource("default.properties").getAbsolutePath();
        String configPath = getResource("concrete.properties").getAbsolutePath();
        Assertions.assertDoesNotThrow(() -> {
            // Parse defaults
            ConfigurationParser defaultPropertyParser = new JavaPropertiesConfigurationParser(defaultConfigPath);
            Printer defaultPrinterConfig = defaultPropertyParser.getPrinter();
            Format defaultFormatConfig = defaultPropertyParser.getFormat("default");

            // Parse actual properties with defaults set
            ConfigurationParser parser = new JavaPropertiesConfigurationParser(configPath, defaultPrinterConfig, defaultFormatConfig);
            mPrinterConfig = parser.getPrinter();
            Set<String> properties = mPrinterConfig.getPropertyNames();
            Set<String> formats = parser.getFormatNames();
            mFormatConfig = parser.getFormat("A4");
        });
    }
    @Test
    public void testCorrectValues() {
        // default values - not overwritten
        Assertions.assertEquals(2.5, mPrinterConfig.getProperty("max.characterDistance").toDouble());
        Assertions.assertEquals(false, mPrinterConfig.getProperty("floatingDotSupport").toBool());

        Assertions.assertEquals(10, mFormatConfig.getProperty("margin.bottom").toInt());

        // overwritten values

        Assertions.assertEquals(0, mFormatConfig.getProperty("margin.left").toInt());

        // values without default
        Assertions.assertEquals("Index Everest-D V4", mPrinterConfig.getProperty("name").toString());

    }
    @Test
    public void testCompatibleTypeConversion() {
        Assertions.assertEquals("5.0", mPrinterConfig.getProperty("indent.top").toString());
        Assertions.assertEquals(false, mPrinterConfig.getProperty("max.characterDistance").toBool());

        Assertions.assertEquals(297.0, mFormatConfig.getProperty("page.height").toDouble());
    }
    @Test
    public void testFallbackProperties() {

        String specifiedByConfig[] = {"name", "brailletable", "indent.top", "indent.left",
                "indent.bottom", "indent.right", "raster.indent.top", "raster.indent.left", "raster.indent.bottom",
                "raster.indent.right", "raster.dotDistance.horizontal", "raster.dotDistance.vertical",
                "raster.cellDistance.horizontal", "raster.cellDistance.vertical", "raster.dotDiameter"};
        String specifiedByFallback[] = {"floatingDotSupport", "max.characterDistance", "raster.dotDiameter"};

        // config shall extend the fallback
        HashSet<String> expectedPropertyNames = new HashSet<>(Arrays.asList(specifiedByConfig));
        expectedPropertyNames.addAll(new HashSet<>(Arrays.asList(specifiedByFallback)));

        HashSet<String> propertyNames = mPrinterConfig.getPropertyNames();
        Assertions.assertEquals(expectedPropertyNames, propertyNames);
    }


    // Incorrect use testcases
    @Test
    public void testIllegalFile() {
        Assertions.assertThrows(
                ConfigurationParsingException.class,
                () -> new JavaPropertiesConfigurationParser("nonexistent.properties")
        );
    }
    @Test
    public void testIllegalProperty() {
        String configPath = getResource("illegalPropertyNameExample.properties").getAbsolutePath();
        Assertions.assertThrows(
                ConfigurationValidationException.class,
                () -> new JavaPropertiesConfigurationParser(configPath)
        );
    }
    @Test
    public void testIllegalValue() {
        String configPath = getResource("illegalPropertyValueExample.properties").getAbsolutePath();
        Assertions.assertThrows(
                ConfigurationValidationException.class,
                () -> new JavaPropertiesConfigurationParser(configPath)
        );
    }
    @Test
    public void testNonexistentFormat() {
        String configPath = getResource("default.properties").getAbsolutePath();
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            ConfigurationParser parser = new JavaPropertiesConfigurationParser(configPath);
            parser.getFormat("B5");
        });
    }
    @Test
    public void testNonexistentProperties() {
        String configPath = getResource("default.properties").getAbsolutePath();
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            ValidProperty spooderman = mPrinterConfig.getProperty("spooderman");
        });
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            ValidProperty doge = mFormatConfig.getProperty("doge");
        });
    }
    @Test
    public void testIncompatibleTypeConversion() {
        Assertions.assertThrows(NumberFormatException.class, () -> mPrinterConfig.getProperty("floatingDotSupport").toInt());
        Assertions.assertThrows(NumberFormatException.class, () -> mPrinterConfig.getProperty("max.characterDistance").toInt());
        Assertions.assertThrows(NumberFormatException.class, () -> mPrinterConfig.getProperty("name").toDouble());

        //Assertions.assertThrows(NumberFormatException.class, () -> mFormatConfig.getProperty("isPortrait").toDouble());
    }

}
