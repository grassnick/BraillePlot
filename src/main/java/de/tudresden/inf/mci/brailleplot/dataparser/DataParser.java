package de.tudresden.inf.mci.brailleplot.dataparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointContainer;

import java.io.InputStream;

/**
 * Interface for all classes supporting the parsing of input data.
 * @param <T> The type of the supported {@link de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer}.
 * @author Georg Graßnick
 * @version 2019.08.02
 */
public interface DataParser<T extends PointContainer> {

    /**
     * Reads the contents of the specified stream and constructs a {@link de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer} encapsulating the data.
     * @param inputStream The stream to read the data from.
     * @return The according data container.
     */
    T parse(InputStream inputStream);
}
