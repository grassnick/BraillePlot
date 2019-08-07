package de.tudresden.inf.mci.brailleplot.dataparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import org.apache.commons.collections4.MultiValuedMap;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * POJ class used for bar chart elements (basically one object per line of the CSV file).
 * @author Georg Graßnick
 * @version 2019.08.05
 */
public class GenericNamedDoubleListCsvParser extends AbstractCsvParser<GenericNamedDoubleListElement> implements DataParser<PointListContainer<PointList>> {

    public GenericNamedDoubleListCsvParser() {
        this(new Configuration());
    }

    public GenericNamedDoubleListCsvParser(final Configuration config) {
        super(config);
    }

    @Override
    public PointListContainer<PointList> parse(final InputStream inputStream) {

        List<GenericNamedDoubleListElement> csvElements = super.genericParse(inputStream, GenericNamedDoubleListElement.class);

        PointListContainer<PointList> container = new SimplePointListContainerImpl();

        // As the values for one data set are split in two consecutive lines, we need to keep track of the current state of the parser
        boolean parsedTwoLines = false;
        PointList pl = null;
        MultiValuedMap<Integer, Double> xVals = null;

        for (GenericNamedDoubleListElement e : csvElements) {
            // We are currently working with the first line, thus have access to the name of the data set, and have to instantiate a new PointList
            if (!parsedTwoLines) {
                if (e == null) {
                    throw new MalformedCsvException("Dataset does not contain a name.");
                }
                pl = new SimplePointListImpl(e.name);
                xVals = e.vals;
                // We have parsed two lines.
                // This means, we currently have access to the y values of a data set and can submit the final PointList
            } else {
                if (xVals.values().size() != e.vals.values().size()) {
                    throw new MalformedCsvException("The number of x positions (" + xVals.values().size() + ") differs from the number of y positions (" + e.vals.values().size() + ")");
                }
                int idx = 1;
                for (Double d : e.vals.values()) {
                    Collection<Double> c = xVals.get(idx++);
                    pl.pushBack(new Point2DDouble(c.iterator().next(), d));
                }
                container.pushBack(pl);
            }
            parsedTwoLines = !parsedTwoLines;
        }
        return container;
    }
}
