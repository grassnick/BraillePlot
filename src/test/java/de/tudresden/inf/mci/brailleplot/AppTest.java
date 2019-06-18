package de.tudresden.inf.mci.brailleplot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AppTest {

    private final App mApp = App.getInstance();

    @Test
    public void testAppIsSingleton() {
        assertEquals(mApp, App.getInstance());
    }

    // TODO Add system tests
}
