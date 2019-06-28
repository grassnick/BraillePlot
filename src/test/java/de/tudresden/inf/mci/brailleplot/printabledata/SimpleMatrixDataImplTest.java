package de.tudresden.inf.mci.brailleplot.printabledata;

import de.tudresden.inf.mci.brailleplot.configparser.Format;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

class SimpleMatrixDataImplTest {


    private final Integer mTestInt = 42;
    private final Integer mDefaultTestInt = 0;

    private MatrixData<Integer> simpleIntMat() {
        return new SimpleMatrixDataImpl<>(new Printer(), new Format(),3, 3, mDefaultTestInt);
    }

    private MatrixData<Integer> iteratorIntMat() {
        SimpleMatrixDataImpl<Integer> matrix = new SimpleMatrixDataImpl<>(new Printer(), new Format(), 6, 4, 0);
        matrix.setValue(0, 0, 1);
        matrix.setValue(1, 0, 2);
        matrix.setValue(2, 0, 3);
        matrix.setValue(0, 1, 4);
        matrix.setValue(1, 1, 5);
        matrix.setValue(2, 1, 6);
        matrix.setValue(0, 2, 7);
        matrix.setValue(1, 2, 8);
        matrix.setValue(2, 2, 9);
        matrix.setValue(0, 3, 10);
        matrix.setValue(1, 3, 11);
        matrix.setValue(2, 3, 12);
        matrix.setValue(3, 0, 13);
        matrix.setValue(4, 0, 14);
        matrix.setValue(5, 0, 15);
        matrix.setValue(3, 1, 16);
        matrix.setValue(4, 1, 17);
        matrix.setValue(5, 1, 18);
        matrix.setValue(3, 2, 19);
        matrix.setValue(4, 2, 20);
        matrix.setValue(5, 2, 21);
        matrix.setValue(3, 3, 22);
        matrix.setValue(4, 3, 23);
        matrix.setValue(5, 3, 24);

        return matrix;
    }

    @Test
    void getSetValueTestPosZero() {
        MatrixData<Integer> matrixData = simpleIntMat();
        matrixData.setValue(0, 0, mTestInt);
        Assertions.assertEquals(matrixData.getValue(0,0), mTestInt);
    }

    @Test
    void getSetValueTestInner() {
        MatrixData<Integer> matrixData = simpleIntMat();
        matrixData.setValue(matrixData.getRowCount() - 1, matrixData.getColumnCount() - 1, mTestInt);
        Assertions.assertEquals(matrixData.getValue(matrixData.getRowCount() - 1,matrixData.getColumnCount() - 1), mTestInt);
    }

    @Test
    void getSetValueTestInvalidPos() {
        MatrixData<Integer> matrixData = simpleIntMat();
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            matrixData.setValue(matrixData.getRowCount(), matrixData.getColumnCount(), mTestInt);
        });
    }

    @Test
    void defaultValuetest() {
        MatrixData<Integer> matrixData = simpleIntMat();
        Assertions.assertEquals(matrixData.getValue(0,0), mDefaultTestInt);
    }

    @Test
    void getDotIteratortest() {
        MatrixData<Integer> matrixData = iteratorIntMat();
        int referenceVaule = 0;
        Iterator<Integer> it = matrixData.getDotIterator(2, 3);

        while (it.hasNext()) {
            referenceVaule++;
            Integer current = it.next();
            Assertions.assertEquals(current, referenceVaule);
        }
    }

    @Test
    void getColumnCount() {
        Assertions.assertEquals(simpleIntMat().getColumnCount(), 3);
    }

    @Test
    void getRowCount() {
        Assertions.assertEquals(simpleIntMat().getRowCount(), 3);
    }
}