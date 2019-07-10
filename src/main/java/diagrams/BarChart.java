package diagrams;

import de.tudresden.inf.mci.brailleplot.rendering.Renderable;
import parser.CategorialPointListList;
import parser.PointListList;
import parser.XType;

import java.util.List;

public class BarChart implements Renderable {
    private CategorialPointListList p;

    public BarChart(CategorialPointListList p) {
        this.p = p;
        p.updateMinMax();
    }

    public int getCategoryCount() {
        return p.getCategoryCount();
    }

    public List<String> getCategoryNames() {
        return p.getCategoryNames();
    }

    public void addCategory(String name) {
        p.categoryNames.add(name);
    }

    public String getCategoryName(int index) {
        return p.getCategoryName(index);
    }

    public double getCategorySum(int index) {
        return p.getCategorySum(index);
    }

    public XType getXType() {
        return p.getXType();
    }

    public double getMaxYSum() {
        return p.getMaxYSum();
    }

    public double getMinY() {
        return p.getMinY();
    }

    public double getMaxY() {
        return p.getMaxY();
    }

    /** returns a list with x-y-Pairs: x is the index (always just counts from 0 up), y is the value
     *
     * @param index
     * @return
     */
    public PointListList.PointList getDataSet(int index) {
        return (PointListList.PointList) p.get(index);
    }

    public String getDataSetName(int index) {
        return p.get(index).getName();
    }

}
