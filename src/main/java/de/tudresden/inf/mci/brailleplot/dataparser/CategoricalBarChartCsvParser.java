package de.tudresden.inf.mci.brailleplot.dataparser;

import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimpleCategoricalPointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimplePointListImpl;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * An implementation of {@link DataParser} for CSV documents containing data for categorical bar charts.
 * @author Georg Graßnick
 * @version 2019.08.02
 */
public class CategoricalBarChartCsvParser extends AbstractCsvParser<CategoricalBarChartElement> implements DataParser<CategoricalPointListContainer<PointList>> {

    public CategoricalBarChartCsvParser() {
        this(new Configuration());
    }

    public CategoricalBarChartCsvParser(final Configuration config) {
        super(config);
    }

    @Override
    public CategoricalPointListContainer<PointList> parse(final InputStream inputStream) {
        mConfig.skipLines = 1;
        List<CategoricalBarChartElement> csvElements = super.genericParse(inputStream, CategoricalBarChartElement.class);

        CategoricalPointListContainer<PointList> container = new SimpleCategoricalPointListContainerImpl();
        int numberOfLists = csvElements.size();
        for (int i = 0; i < numberOfLists; i++) {
            container.pushBack(new SimplePointListImpl());
        }

        for (CategoricalBarChartElement e : csvElements) {
            mLogger.trace("Parsed category name \"{}\"", e.category);
            int catPos = container.pushBackCategory(e.category);
            Iterator<PointList> it = container.iterator();
            for (Double d: e.xVals.values()) {
                if (!it.hasNext()) {
                    throw new RuntimeException("An internal error occurred");
                }
                mLogger.trace("Parsed Value \"{}\" with category \"{}\"", d.toString(), container.getCategory(catPos));
                PointList pl = it.next();
                pl.pushBack(new Point2DDouble(catPos, d));
            }
        }
        container.calculateExtrema();
        return container;
    }

}
