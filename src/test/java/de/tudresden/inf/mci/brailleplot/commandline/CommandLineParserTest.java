package de.tudresden.inf.mci.brailleplot.commandline;

import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests the CommandlineParser and the querying of parameters of the Settings object.
 */
class CommandLineParserTest {

    private CommandLineParser commandLineParser = new CommandLineParser();

    @Test
    void testParseLegalArgs() {
        String[] args = {"-h", "--csv-path", "foobar", "-p", "test", "-t", "title", "-x", "x-axis", "-y", "y-axis", "-d", "BarChart"};
        Assertions.assertDoesNotThrow(() -> {commandLineParser.parse(args);});
    }

    @Test
    void testIllegalArgs() {
        String[] args = {"-h --foobar"};
        Assertions.assertThrows(ParsingException.class, () -> {commandLineParser.parse(args);});
    }

    @Test
    void testEmptyArgs() {
        String[] args = {""};
        Assertions.assertThrows(ParsingException.class, () -> {commandLineParser.parse(args);});
    }

    @Test
    void testBoolFlagRecognized() {
        final String param = "xyz";
        String[] args = {"--csv-path", param, "-p", param, "-t", param, "-x", param, "-y", param, "-d", param, "-i"};
        SettingsReader settings;
        try {
            settings = commandLineParser.parse(args);
        } catch (ParsingException pe) {
            fail();
            return; // Never executed, satisfy compiler
        }
        Optional<Boolean> flag = settings.isTrue(SettingType.INHIBIT_PRINT);
        Assertions.assertTrue(flag.isPresent());
        Assertions.assertTrue(flag.get());
    }

    @Test
    void testBoolFlagCorrectlyNotRecognized() {
        final String param = "xyz";
        String[] args = {"--csv-path", param, "-p", param, "-t", param, "-x", param, "-y", param, "-d", param};
        SettingsReader settings;
        try {
            settings = commandLineParser.parse(args);
        } catch (ParsingException pe) {
            fail();
            return; // Never executed, satisfy compiler
        }
        Optional<Boolean> flag = settings.isTrue(SettingType.INHIBIT_PRINT);
        Assertions.assertFalse(flag.isPresent());
    }

    @Test
    void testParameterRecognized() {
        final String param = "xyz";
        String[] args = {"--csv-path", param, "-p", param, "-t", param, "-x", param, "-y", param, "-d", param};
        SettingsReader settings;
        try {
            settings = commandLineParser.parse(args);
        } catch (ParsingException pe) {
            fail();
            return; // Never executed, satisfy compiler
        }
        Optional<String> flag;
        for (SettingType setting : new SettingType[]{
                SettingType.CSV_LOCATION, SettingType.PRINTER_CONFIG_PATH, SettingType.DIAGRAM_TITLE,
                SettingType.X_AXIS_LABEL, SettingType.X_AXIS_LABEL, SettingType.DIAGRAM_TYPE}
                ) {
            flag = settings.getSetting(setting);
            Assertions.assertTrue(flag.isPresent());
            Assertions.assertTrue(flag.get().equals(param));
        }
    }

    @Test
    void testParameterCorrectlyNotRecognized() {
        final String param = "xyz";
        String[] args = {"--csv-path", param, "-p", param, "-t", param, "-x", param, "-y", param, "-d", param};
        SettingsReader settings;
        try {
            settings = commandLineParser.parse(args);
        } catch (ParsingException pe) {
            fail();
            return; // Never executed, satisfy compiler
        }
        Optional<String> flag = settings.getSetting(SettingType.SVG_EXPORT);
        Assertions.assertFalse(flag.isPresent());
    }
}

