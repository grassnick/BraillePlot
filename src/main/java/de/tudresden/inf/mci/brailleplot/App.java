package de.tudresden.inf.mci.brailleplot;

import de.tudresden.inf.mci.brailleplot.exporter.DirectPrint;

/**
 * Main class.
 * Set up the application and run it.
 * @author Leonard Kupper
 */
public final class App {

    public String getGreeting() {
        return "Hello world.";
    }


    public static void main(final String[] args) {
        System.out.println(new App().getGreeting());

        // Just placeholder Georg
        DirectPrint lt = new DirectPrint();
        byte[] data = lt.buildDemo(1);
        lt.printString(data);
    }

}
