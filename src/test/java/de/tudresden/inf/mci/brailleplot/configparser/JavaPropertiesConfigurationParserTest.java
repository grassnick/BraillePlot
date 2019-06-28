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
        String defaultConfigPath = getResource("defaultConfig.properties").getAbsolutePath();
        String configPath = getResource("dummyPrinterConfig.properties").getAbsolutePath();
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
            mFormatConfig = parser.getFormat("B5");
        });
    }
    @Test
    public void testCorrectValues() {
        // default values - not overwritten
        Assertions.assertEquals(35, mPrinterConfig.getProperty("max.charsPerLine").toInt());
        Assertions.assertEquals(29, mPrinterConfig.getProperty("max.linesPerPage").toInt());

        Assertions.assertEquals(10, mFormatConfig.getProperty("margin.left").toInt());

        // overwritten values
        Assertions.assertEquals(true, mPrinterConfig.getProperty("equidistantSupport").toBool());
        Assertions.assertEquals(2.5, mPrinterConfig.getProperty("max.characterDistance").toDouble());

        Assertions.assertEquals(176, mFormatConfig.getProperty("page.width").toInt());
        Assertions.assertEquals(250, mFormatConfig.getProperty("page.height").toInt());

        // values without default
        Assertions.assertEquals("Index Everest-D V4", mPrinterConfig.getProperty("name").toString());

        Assertions.assertEquals(false, mFormatConfig.getProperty("isPortrait").toBool());
    }
    @Test
    public void testCompatibleTypeConversion() {
        Assertions.assertEquals("35", mPrinterConfig.getProperty("max.charsPerLine").toString());
        Assertions.assertEquals(false, mPrinterConfig.getProperty("max.characterDistance").toBool());

        Assertions.assertEquals(250.0, mFormatConfig.getProperty("page.height").toDouble());
    }
    @Test
    public void testFallbackProperties() {

        String specifiedByConfig[] = {"name", "max.characterDistance", "equidistantSupport"};
        String specifiedByFallback[] = {"max.charsPerLine", "max.linesPerPage"};

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
        String configPath = getResource("defaultConfig.properties").getAbsolutePath();
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            ConfigurationParser parser = new JavaPropertiesConfigurationParser(configPath);
            parser.getFormat("B5");
        });
    }
    @Test
    public void testNonexistentProperties() {
        String configPath = getResource("defaultConfig.properties").getAbsolutePath();
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            ValidProperty spooderman = mPrinterConfig.getProperty("spooderman");
        });
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            ValidProperty doge = mFormatConfig.getProperty("doge");
        });
    }
    @Test
    public void testIncompatibleTypeConversion() {
        Assertions.assertThrows(NumberFormatException.class, () -> mPrinterConfig.getProperty("equidistantSupport").toInt());
        Assertions.assertThrows(NumberFormatException.class, () -> mPrinterConfig.getProperty("max.characterDistance").toInt());
        Assertions.assertThrows(NumberFormatException.class, () -> mPrinterConfig.getProperty("name").toDouble());

        Assertions.assertThrows(NumberFormatException.class, () -> mFormatConfig.getProperty("isPortrait").toDouble());
    }

}
