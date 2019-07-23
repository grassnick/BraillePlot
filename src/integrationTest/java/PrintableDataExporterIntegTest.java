import de.tudresden.inf.mci.brailleplot.App;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.print.DocFlavor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

public class PrintableDataExporterIntegTest {

    private static App mApp;
    private static String[] args;
    private static CommandLineParser cliParser;
    private static SettingsWriter settings;
    private static SettingsReader settingsReader;
    private static Printer printer;
    private static Format format;
    private static MatrixData<Boolean> data;


    @BeforeAll
    public static void setUp() {
        Assertions.assertDoesNotThrow(() -> {
            args = new String[]{"-p", "src/integrationTest/resources/correct.properties"};
            mApp = App.getInstance();
            cliParser = new CommandLineParser();
            settings = cliParser.parse(args);
            settingsReader = settings;
            Optional<String> configPath = settingsReader.getSetting(SettingType.PRINTER_CONFIG_PATH);
            JavaPropertiesConfigurationParser configParser = new JavaPropertiesConfigurationParser(configPath.get(), "default.properties");
            printer = configParser.getPrinter();
            printer.getProperty("brailletable").toString();
            format = configParser.getFormat("A4");
            data = new SimpleMatrixDataImpl<>(printer, format, 18, 20, true);

        });
    }

    @AfterAll
    public static void tearDown() {
    }

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



    @Test
    public void testWrongDocFlavor() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Field mDocflavor = PrintDirector.class.getDeclaredField("mDocflavor");
            mDocflavor.setAccessible(true);
            PrintDirector printD = new PrintDirector(PrinterCapability.NORMALPRINTER, printer);
            mDocflavor.set(printD, new DocFlavor("text/html", "[B"));
            Method setUpService = PrintDirector.class.getDeclaredMethod("setUpService");
            setUpService.setAccessible(true);
            try {
                setUpService.invoke(printD);
                Method privatePrint = PrintDirector.class.getDeclaredMethod("print", byte[].class);
                privatePrint.setAccessible(true);
                privatePrint.invoke(printD, new byte[] {0x50});
            } catch (InvocationTargetException e){
                throw e.getTargetException();
            }

        });
    }

    // Positve Testcases

    @Test
    public void testIfPrintJobIsExistent() {
        Assertions.assertDoesNotThrow(() ->{
            PrintDirector printD = new PrintDirector(PrinterCapability.NORMALPRINTER, printer);
            printD.print(data);
        });

    }




}
