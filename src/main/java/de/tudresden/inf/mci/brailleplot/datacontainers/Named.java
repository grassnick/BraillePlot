package de.tudresden.inf.mci.brailleplot.datacontainers;

/**
 * Interface for objects, that are identified by a name.
 * @author Georg Graßnick
 * @version 2019.07.29
 */
public interface Named {

    String getName();
    void setName(String newName);
}
