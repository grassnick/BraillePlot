package diagrams;

import parser.PointListList;

public class Diagram {
    protected PointListList p;

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
}
