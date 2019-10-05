package de.tudresden.inf.mci.brailleplot.diagrams;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;

import java.util.Iterator;

/**
 * Representation of a bar chart with basic data functions. Extends {@link Diagram}.
 * @author Richard Schmidt, Georg Graßnick
 * @version 2019.09.02
 */
public class BarChart extends Diagram {

    public BarChart(final PointListContainer<PointList> data) {
        super(data);
    }

    /**
     * Adds all y-values of one data series.
     * @return double maximum y-value
     */
    public double getCumulatedMaxY() {
        double maxY = 0;

        for (PointList list : mData) {
            Iterator<Point2DDouble> smallIt = list.getListIterator();
            double current = 0;
            while (smallIt.hasNext()) {
                Point2DDouble point = smallIt.next();
                if (point.getY() >= 0) {
                    current += point.getY();
                }
            }

            if (current > maxY) {
                maxY = current;
            }
        }
        return maxY;
    }
}
