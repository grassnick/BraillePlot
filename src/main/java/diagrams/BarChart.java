package diagrams;

import parser.PointListList;

public class BarChart extends Diagram {

    public BarChart(PointListList p) {
        this.p = p;
        p.updateMinMax();
    }

}
