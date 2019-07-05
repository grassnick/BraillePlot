package diagrams;

import parser.PointListList;

public class LinePlot extends Diagram {

    public LinePlot(PointListList p) {
        this.p = p;
        p.updateMinMax();
    }
}
