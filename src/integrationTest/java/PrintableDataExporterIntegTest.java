import de.tudresden.inf.mci.brailleplot.commandline.CommandLineParser;
import de.tudresden.inf.mci.brailleplot.commandline.SettingType;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsReader;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsWriter;
import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.JavaPropertiesConfigurationParser;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.exporter.PrintDirector;
import de.tudresden.inf.mci.brailleplot.exporter.PrinterCapability;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.print.DocFlavor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Integrationtests for the components PrintableData and Exporter.
 * Because most of the Exporter Unit tests depend heavily on the packages PrintableData and configParser,
 * these are also located here. Most, if not all unittests for the exporter are tested via Reflection. It is
 * contestable if these tests are really needed, but the with more LOC Coverage, there comes more possible stability.
 * @author Andrey Ruzhanskiy
 * @version 30.07.2019
 */

public class PrintableDataExporterIntegTest {

    private static String[] args;
    private static CommandLineParser cliParser;
    private static SettingsWriter settings;
    private static SettingsReader settingsReader;
    private static Printer printer;
    private static Format format;
    private static MatrixData<Boolean> data;


    /**
     * Setup Method for testing the exporter package.
     */
    @BeforeAll
    public static void setUp() {
        Assertions.assertDoesNotThrow(() -> {
            String correct = getResource("config/correct.properties").getAbsolutePath();
            String standard = getResource("config/default.properties").getAbsolutePath();
            JavaPropertiesConfigurationParser configParser = new JavaPropertiesConfigurationParser(correct, standard);
            printer = configParser.getPrinter();
            printer.getProperty("brailletable").toString();
            format = configParser.getFormat("A4");
            data = new SimpleMatrixDataImpl<>(printer, format, 18, 20, true);
        });
    }

    public static File getResource(String fileName) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File resourceFile = new File(classLoader.getResource(fileName).getFile());
        return resourceFile;
    }
    /**
     * Unittest/Integrationtest for the private Print method with a Null Servive.
     * Expected: Nullpointerexception.
     */
    @Test
    public void testPrivatePrintWithNullService() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            Method privatePrint = PrintDirector.class.getDeclaredMethod("print", byte[].class);
            privatePrint.setAccessible(true);
            try {
                privatePrint.invoke(new PrintDirector(PrinterCapability.NORMALPRINTER,printer), new byte[] {0x55});
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        });
    }

    /**
     * Unittest/Integrationtest for private print method with a null byte.
     * Expected: Nullpointerexception.
     */
    @Test
    public void testPrivatePrintWithNullByte() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            Method privatePrint = PrintDirector.class.getDeclaredMethod("print", byte[].class);
            privatePrint.setAccessible(true);
            try {
                privatePrint.invoke(new PrintDirector(PrinterCapability.NORMALPRINTER,printer), (Object) null);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        });
    }

    /**
     * Unittest/Integrationtest for private print method with non existing Docflavour.
     * Expected: Nullpointerexception.
     */
    @Test
    public void testPrivatePrintWithNoDocFlavour() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            Method privatePrint = PrintDirector.class.getDeclaredMethod("print", byte[].class);
            privatePrint.setAccessible(true);
            Method setUpService = PrintDirector.class.getDeclaredMethod("setUpService");
            setUpService.setAccessible(true);
            try {
                PrintDirector printD = new PrintDirector(PrinterCapability.NORMALPRINTER,printer);
                setUpService.invoke(printD);
                privatePrint.invoke(printD, new byte[] {0x55});
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        });
    }

    /**
     * Unittest/Integrationtest for private print Method with non existing Printer.
     * Expected: RuntimeException
     */

    @Test
    public void testPrivateSetUpServiceWithNotExistentPrinter() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Method setUpService = PrintDirector.class.getDeclaredMethod("setUpService");
            setUpService.setAccessible(true);
            Field mPrinterName = PrintDirector.class.getDeclaredField("mPrinterName");
            mPrinterName.setAccessible(true);
            try {
                PrintDirector printD = new PrintDirector(PrinterCapability.NORMALPRINTER, printer);
                mPrinterName.set(printD, "please dont exist, please please please");
                setUpService.invoke(printD);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        });
    }

    /**
     * Unittest/Integrationtest for Method SetUpService with NullPrinter.
     * Expected: Nullpointerexception.
     */
    @Test
    public void testPrivateSetUpServiceWithNullPrinter() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            Method setUpService = PrintDirector.class.getDeclaredMethod("setUpService");
            setUpService.setAccessible(true);
            Field mPrinterName = PrintDirector.class.getDeclaredField("mPrinterName");
            mPrinterName.setAccessible(true);
            try {
                PrintDirector printD = new PrintDirector(PrinterCapability.NORMALPRINTER, printer);
                mPrinterName.set(printD, (Object)null);
                setUpService.invoke(printD);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        });
    }


    /**
     * Unittest/Integrationtest for setting a Wrong Doc Flavor and trying to print with it.
     * Expected: RuntimeException.
     */
    @Test
    public void testWrongDocFlavor() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Field mDocflavor = PrintDirector.class.getDeclaredField("mDocflavor");
            mDocflavor.setAccessible(true);
            PrintDirector printD = new PrintDirector(PrinterCapability.NORMALPRINTER, printer);
            mDocflavor.set(printD, new DocFlavor("text/html", "[B"));
            Method setUpService = PrintDirector.class.getDeclaredMethod("setUpService");
            setUpService.setAccessible(true);
            Method privatePrint = PrintDirector.class.getDeclaredMethod("print", byte[].class);
            privatePrint.setAccessible(true);
            try {
                setUpService.invoke(printD);
                privatePrint.invoke(printD, new byte[] {0x50});
            } catch (InvocationTargetException e){
                throw e.getTargetException();
            }
        });
    }

    // Positve Testcases

    @Test
    public void testFloatingDotCapability() {
        Assertions.assertDoesNotThrow(() -> {
            PrintDirector printD = new PrintDirector(PrinterCapability.INDEX_EVEREST_D_V4_FLOATINGDOT_PRINTER, printer);
        });
    }

    @Test
    public void testGraphicPrintCapability() {
        Assertions.assertDoesNotThrow(() -> {
            PrintDirector printD = new PrintDirector(PrinterCapability.INDEX_EVEREST_D_V4_GRAPHIC_PRINTER, printer);
        });
    }

    @Test
    public void testIsPrintServiceOn() {
        Assertions.assertTrue(PrintDirector.isPrintServiceOn());
    }
}
