package de.tudresden.inf.mci.brailleplot.dataparser;

import com.opencsv.bean.CsvBindAndJoinByPosition;
import com.opencsv.bean.CsvBindByPosition;
import org.apache.commons.collections4.MultiValuedMap;

/**
 * POJ class used for bar generic elements, that consist of a name, followed by a list of {@link Double}.
 * @author Georg Graßnick
 * @version 2019.08.05
 */
public class GenericNamedDoubleListElement extends CsvElement {

    public GenericNamedDoubleListElement() { }

    @CsvBindByPosition(required = false, position = 0)
    public String name;


    /**
     * Currently, there arise multiple problems using the Bean parser.
     * The current implementation does seem to evaluate the number of columns on the first row only,
     * and thus ignores elements on the second line, if they extend the number of elements on the first row.
     * Additionally, parsing doubles with leading whitespaces does seem to be a problem.
     */
    @CsvBindAndJoinByPosition(required = true, elementType = Double.class, position = "1-", locale = "de-DE")
    public MultiValuedMap<Integer, Double> vals;
}
