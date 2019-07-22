package de.tudresden.inf.mci.brailleplot.rendering;

/**
 * A functional interface representing a void function taking two parameters of types  T and U which can throw an
 * exception E on failure.
 * @param <T> First parameter type.
 * @param <U> Second parameter type.
 * @param <E> Exception type.
 * @author Leonard Kupper
 * @version 2019.07.09
 */
@FunctionalInterface
interface ThrowingBiConsumer<T, U, E extends Exception> {
    void accept(T t, U u) throws E;
}
