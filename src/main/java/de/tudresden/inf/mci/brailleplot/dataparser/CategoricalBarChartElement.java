package de.tudresden.inf.mci.brailleplot.dataparser;

import com.opencsv.bean.CsvBindAndJoinByPosition;
import com.opencsv.bean.CsvBindByPosition;
import org.apache.commons.collections4.MultiValuedMap;


/**
 * POJ class used for bar chart elements (basically one object per line of the CSV file).
 * @author Georg Graßnick
 * @version 2019.08.02
 */
public class CategoricalBarChartElement extends CsvElement {

    public CategoricalBarChartElement() { }

    @CsvBindByPosition(required = true, position = 0)
    public String category;


    @CsvBindAndJoinByPosition(required = true, elementType = Double.class, position = "1-", locale = "de-DE")
    public MultiValuedMap<String, Double> xVals;

}
