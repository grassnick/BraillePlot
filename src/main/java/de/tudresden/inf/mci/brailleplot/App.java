package de.tudresden.inf.mci.brailleplot;

/**
 * Main class.
 * Set up the application and run it.
 */
public final class App {

    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(final String[] args) {
        System.out.println(new App().getGreeting());
    }
}
