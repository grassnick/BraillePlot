package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.Named;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of a bar chart with basic data functions. Implements Renderable.
 * @author Richard Schmidt, Georg Graßnick
 * @version 2019.07.29
 */
public class BarChart extends Diagram implements Renderable {

    public BarChart(final PointListContainer<PointList> data) {
        super(data);
    }

    /**
     * Getter for the total number of categories.
     *
     * @return int number of categories
     */
    public int getCategoryCount() {
        return mData.getSize();
    }

    /**
     * Getter for the category names in a list.
     *
     * @return list with category names as strings
     */
    public List<String> getCategoryNames() {
        return mData.stream()
                .map(Named::getName)
                .collect(Collectors.toUnmodifiableList());
    }

}

