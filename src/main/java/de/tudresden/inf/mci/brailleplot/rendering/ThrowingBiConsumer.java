package de.tudresden.inf.mci.brailleplot.rendering;

import java.util.function.BiConsumer;

@FunctionalInterface
interface ThrowingBiConsumer<T, U, E extends Exception> {
    void accept(T t, U u) throws E;
}
