package de.tudresden.inf.mci.brailleplot;

import de.tudresden.inf.mci.brailleplot.App;
import de.tudresden.inf.mci.brailleplot.configparser.ConfigurationParsingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AppTest {

    private final App mApp = App.getInstance();

    @Test
    public void testAppIsSingleton() {
        assertEquals(mApp, App.getInstance());
    }

    @Test
    public void smokeTest() {
        Assertions.assertEquals(0, mApp.run(new String[]{"-p", "src/test/resources/config/dummyprinter.properties",
        "-c", "src/test/resources/examples_csv/2_line_chart.csv",
        "-d", "LineChart",
        "-t", "title",
        "-x", "X Axis",
        "-y", "Y Axis"}));
    }
    // TODO Add system tests
}
