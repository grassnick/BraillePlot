package de.tudresden.inf.mci.brailleplot.rendering;


/**
 * A dummy class for BarCharts.
 * @author Leonard Kupper
 * @version 2019.06.28
 */
public class BarChart implements Renderable {

    //private double[] dummy = {0.21, 0.13, -0.3, 0.52, -1.02, 0.65, 0.77, -0.33};
    private double[] dummy = {500,36,357,473,75,220,356};

    final String getTitle() {
        return "I am a bar chart.";
    }

    final double[] getCategories() {
        return dummy;
    }

    final int getNumberOfCategories() {
        return dummy.length;
    }

    final double getValue(final int categoryIndex) {
        return dummy[categoryIndex];
    }

    public double getValueRangeSize() {
        return (getValueRangeMax() - getValueRangeMin());
    }

    public double getValueRangeMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (double value : dummy) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public double getValueRangeMin() {
        double min = Double.POSITIVE_INFINITY;
        for (double value : dummy) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

}
