package de.tudresden.inf.mci.brailleplot.svgexporter;

import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.point.Point2DValued;
import de.tudresden.inf.mci.brailleplot.printabledata.FloatingPointData;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Iterator;

import static tec.units.ri.unit.Units.METRE;

/**
 * SVG exporter class that supports {@link PlotCanvas} as input.
 * @author Georg Graßnick
 * @version 2019.08.26
 */
public class BoolFloatingPointDataSvgExporter extends AbstractSvgExporter<PlotCanvas, FloatingPointData<Boolean>> {

    public BoolFloatingPointDataSvgExporter(final PlotCanvas canvas) {
        super(canvas);
    }

    @Override
    protected void renderPage(final FloatingPointData<Boolean> points, final int dataIndex) {
        int dotDiameter = (int) (mCanvas.getDotDiameter() * SCALE_FACTOR);
        Iterator<Point2DValued<Quantity<Length>, Boolean>> it = points.getIterator();
        SVGGraphics2D svg = mSvgs.get(dataIndex);
        while (it.hasNext()) {
            Point2DValued<Quantity<Length>, Boolean> p = it.next();

            double x = p.getX().to(MetricPrefix.MILLI(METRE)).getValue().doubleValue();
            double y = p.getY().to(MetricPrefix.MILLI(METRE)).getValue().doubleValue();
            int xPos = (int) Math.round((x - dotDiameter / 2f + mCanvas.getFullConstraintLeft()) * SCALE_FACTOR);
            int yPos = (int) Math.round((y - dotDiameter / 2f + mCanvas.getFullConstraintTop()) * SCALE_FACTOR);
            svg.drawOval(xPos, yPos, dotDiameter, dotDiameter);
        }
    }
}
