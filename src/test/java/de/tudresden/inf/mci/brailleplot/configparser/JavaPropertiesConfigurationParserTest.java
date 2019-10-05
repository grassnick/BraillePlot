package de.tudresden.inf.mci.brailleplot.configparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;


public class JavaPropertiesConfigurationParserTest {

    public static final URL mDefaultConfigPath = getResource("config/default.properties");
    public static final URL mConcreteConfigPath = getResource("config/concrete.properties");
    public static Printer mPrinterConfig;
    public static Format mFormatConfig;

    public static URL getResource(final String location) {
        return ClassLoader.getSystemClassLoader().getResource(location);
    }

    // Correct use testcases
    @Test @BeforeAll
    public static void testSuccessfulParsing() {
        Assertions.assertDoesNotThrow(() -> {
                // Parse concrete properties and defaults
                ConfigurationParser parser = new JavaPropertiesConfigurationParser(mConcreteConfigPath, mDefaultConfigPath);
                mPrinterConfig = parser.getPrinter();
                Set<String> properties = mPrinterConfig.getPropertyNames();
                Set<String> formats = parser.getFormatNames();
                mFormatConfig = parser.getFormat("A4");
        });
    }
    @Test
    public void testCorrectValues() {
        // default values - not overwritten
        Assertions.assertEquals(200, mPrinterConfig.getProperty("raster.constraint.width").toDouble());
        Assertions.assertEquals("6-dot", mPrinterConfig.getProperty("raster.type").toString());
        Assertions.assertEquals(10, mFormatConfig.getProperty("margin.bottom").toInt());

        // overwritten values

        Assertions.assertEquals(0, mFormatConfig.getProperty("margin.left").toInt());

        // values without default
        Assertions.assertEquals("Dummy Printer", mPrinterConfig.getProperty("name").toString());
        Assertions.assertEquals(0.05, mPrinterConfig.getProperty("floatingDot.resolution").toDouble());

    }
    @Test
    public void testCompatibleTypeConversion() {
        Assertions.assertEquals("5.0", mPrinterConfig.getProperty("constraint.top").toString());
        Assertions.assertEquals(false, mPrinterConfig.getProperty("raster.dotDiameter").toBool());

        Assertions.assertEquals(297.0, mFormatConfig.getProperty("page.height").toDouble());
    }
    @Test
    public void testFallbackProperties() {

        String specifiedByConfig[] = {"name", "mode", "floatingDot.resolution", "constraint.top", "constraint.left", "raster.dotDistance.horizontal", "raster.dotDistance.vertical", "raster.cellDistance.horizontal", "raster.cellDistance.vertical", "raster.dotDiameter"};
        String specifiedByFallback[] = {"mode", "semantictable", "constraint.top", "constraint.left", "raster.constraint.top", "raster.constraint.left", "raster.constraint.width", "raster.constraint.height", "raster.type", "raster.dotDistance.horizontal", "raster.dotDistance.vertical", "raster.cellDistance.horizontal", "raster.cellDistance.vertical", "raster.dotDiameter"};

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
                NullPointerException.class,
                () -> new JavaPropertiesConfigurationParser(getResource("config/nonexistent.properties"), mDefaultConfigPath)
        );
    }
    @Test
    public void testMissingRequired() {
        URL configPath = getResource("config/missingRequiredPropertyExample.properties");
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> new JavaPropertiesConfigurationParser(configPath, mDefaultConfigPath)
        );
    }
    @Test
    public void testIllegalProperty() {
        URL configPath = getResource("config/illegalPropertyNameExample.properties");
        Assertions.assertThrows(
                ConfigurationValidationException.class,
                () -> new JavaPropertiesConfigurationParser(configPath, mDefaultConfigPath)
        );
    }
    @Test
    public void testIllegalValue() {
        URL configPath = getResource("config/illegalPropertyValueExample.properties");
        Assertions.assertThrows(
                ConfigurationValidationException.class,
                () -> new JavaPropertiesConfigurationParser(configPath, mDefaultConfigPath)
        );
    }
    @Test
    public void testNonexistentFormat() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            ConfigurationParser parser = new JavaPropertiesConfigurationParser(mConcreteConfigPath, mDefaultConfigPath);
            parser.getFormat("B5");
        });
    }
    @Test
    public void testNonexistentProperties() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            ValidProperty spooderman = mPrinterConfig.getProperty("spooderman");
        });
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            ValidProperty doge = mFormatConfig.getProperty("doge");
        });
    }
    @Test
    public void testIncompatibleTypeConversion() {
        Assertions.assertThrows(NumberFormatException.class, () -> mPrinterConfig.getProperty("raster.cellDistance.horizontal").toInt());
        Assertions.assertThrows(NumberFormatException.class, () -> mPrinterConfig.getProperty("name").toDouble());
    }
}
