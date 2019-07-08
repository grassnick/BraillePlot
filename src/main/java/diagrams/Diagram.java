package diagrams;

import parser.PointListList;
import parser.XType;

public class Diagram {
    public PointListList p;

    public double getMinX() {
        return p.getMinX();
    }

    public double getMaxX() {
        return p.getMaxX();
    }

    public double getMinY() {
        return p.getMinY();
    }

    public double getMaxY() {
        return p.getMaxY();
    }

    public XType getXType() {
        return p.getXType();
    }

    public PointListList.PointList getDataSet(int index) {
        return (PointListList.PointList) p.get(index);
    }

    public String getDataSetName(int index) {
        return p.get(index).getName();
    }
}
