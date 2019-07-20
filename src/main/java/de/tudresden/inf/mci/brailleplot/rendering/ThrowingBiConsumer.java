package de.tudresden.inf.mci.brailleplot.rendering;

@FunctionalInterface
interface ThrowingBiConsumer<T, U, E extends Exception> {
    void accept(T t, U u) throws E;
}
