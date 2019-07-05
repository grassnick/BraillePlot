package diagrams;

import parser.PointListList;

public class ScatterPlot extends Diagram {

    public ScatterPlot(PointListList p) {
        this.p = p;
        p.updateMinMax();
    }
}
