package de.tudresden.inf.mci.brailleplot.commandline;

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
        String[] args = {"-h", "--csv-path", "foobar"};
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
        Assertions.assertDoesNotThrow(() -> {commandLineParser.parse(args);});
    }

    @Test
    void testBoolFlagRecognized() {
        String[] args = {"-h"};
        SettingsReader settings;
        try {
            settings = commandLineParser.parse(args);
        } catch (ParsingException pe) {
            fail();
            return; // Never executed, satisfy compiler
        }
        Optional<Boolean> flag = settings.isTrue(SettingType.DISPLAY_HELP);
        Assertions.assertTrue(flag.isPresent());
        Assertions.assertTrue(flag.get());
    }

    @Test
    void testBoolFlagCorrectlyNotRecognized() {
        String[] args = {""};
        SettingsReader settings;
        try {
            settings = commandLineParser.parse(args);
        } catch (ParsingException pe) {
            fail();
            return; // Never executed, satisfy compiler
        }
        Optional<Boolean> flag = settings.isTrue(SettingType.DISPLAY_HELP);
        Assertions.assertFalse(flag.isPresent());
    }

    @Test
    void testParameterRecognized() {
        final String param = "xyz";
        String[] args = {"--csv-path", param};
        SettingsReader settings;
        try {
            settings = commandLineParser.parse(args);
        } catch (ParsingException pe) {
            fail();
            return; // Never executed, satisfy compiler
        }
        Optional<String> flag = settings.getSetting(SettingType.CSV_LOCATION);
        Assertions.assertTrue(flag.isPresent());
        Assertions.assertTrue(flag.get().equals(param));
    }

    @Test
    void testParameterCorrectlyNotRecognized() {
        String[] args = {""};
        SettingsReader settings;
        try {
            settings = commandLineParser.parse(args);
        } catch (ParsingException pe) {
            fail();
            return; // Never executed, satisfy compiler
        }
        Optional<String> flag = settings.getSetting(SettingType.CSV_LOCATION);
        Assertions.assertFalse(flag.isPresent());
    }
}

