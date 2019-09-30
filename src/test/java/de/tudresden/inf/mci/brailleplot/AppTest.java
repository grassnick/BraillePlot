package de.tudresden.inf.mci.brailleplot;

import de.tudresden.inf.mci.brailleplot.App;
import de.tudresden.inf.mci.brailleplot.configparser.ConfigurationParsingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AppTest {

    private final App mApp = App.getInstance();

    @Test
    public void testAppIsSingleton() {
        assertEquals(mApp, App.getInstance());
    }

    @Test
    public void smokeTest() {
        Assertions.assertEquals(0, mApp.run(new String[]{"-p", "src/main/resources/config/index_everest_d_v4.properties"}));
    }
    // TODO Add system tests
}
