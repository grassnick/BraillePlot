import de.tudresden.inf.mci.brailleplot.App;
import de.tudresden.inf.mci.brailleplot.commandline.CommandLineParser;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsReader;
import de.tudresden.inf.mci.brailleplot.commandline.SettingsWriter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CommandlineAndConfigTest {

    private static App mApp;
    private static String[] Fargs;
    private static CommandLineParser cliParser;
    private static SettingsWriter settings;
    private static SettingsReader settingsReader;


    @BeforeAll
    public static void setUp() {
        Assertions.assertDoesNotThrow(() -> {
            Fargs = new String[]{"-p", "src/integrationTest/resources/index_everest_d_v4.properties"};
            mApp = App.getInstance();
            cliParser = new CommandLineParser();
            settings = cliParser.parse(Fargs);
            settingsReader = settings;
        });
    }

    @AfterAll
    public static void tearDown() {
    }

    @Test
    public void testNormalBuilder() {

    }
}
