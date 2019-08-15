package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.diagrams.LineChart;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class LineChartRasterizer implements Rasterizer<LineChart> {
    private MatrixData mData;
    private LineChart mDiagram;
    private RasterCanvas mCanvas;
    private BrailleTextRasterizer mTextRasterizer;
    private LinearMappingAxisRasterizer mAxisRasterizer;

    // Layout Variables
    private String mDiagramTitle;
    private Rectangle mLineArea;

    public LineChartRasterizer() {
        mTextRasterizer = new BrailleTextRasterizer();
        mAxisRasterizer = new LinearMappingAxisRasterizer();
    }

    @Override
    public void rasterize(final LineChart data, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        if (data.equals(null)){
            throw new NullPointerException("The given data for the LineChartRasterizer was null!");
        }
        if (canvas.equals(null)){
            throw new NullPointerException("The given canvas for the LineChartRasterizer was null!");
        }
        PointListContainer list =  data.getData();
        mCanvas = canvas;
        mDiagram = data;
        mData = mCanvas.getCurrentPage();
        mLineArea = mCanvas.getCellRectangle();
        // Not good, this needs to be discussed
        mDiagramTitle = "I am a line chart";
        Rectangle titleArea = calculateTitle();
        Rectangle xAxisAres = calculateXAxis();
        addSpaceLeft();
        placeYAxis();

    }


    private Rectangle calculateTitle() throws InsufficientRenderingAreaException {
        if(mDiagramTitle.isEmpty()) {
            throw new IllegalArgumentException("The title in LineChartRasterizer was empty!");
        }
        int widthOfCompleteArea = mLineArea.intWrapper().getWidth() * mCanvas.getCellWidth();
        int titleBarHeight = mTextRasterizer.calculateRequiredHeight(mDiagramTitle, 0, 0, widthOfCompleteArea, mCanvas);
        try {
            return  mLineArea.removeFromTop(mCanvas.getCellYFromDotY(titleBarHeight));
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space to build the title area for the line chart!");
        }
    }

    private Rectangle calculateXAxis() throws InsufficientRenderingAreaException {
        try {
            return mLineArea.removeFromBottom(2);
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough space to build the X-Axis for the line chart!");
        }
    }

    private void addSpaceLeft() throws InsufficientRenderingAreaException {
        try {
            mLineArea.removeFromLeft(6);
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("Not enough enough space to the left for the line chart!");
        }
    }

    private void placeYAxis() {
        double minX = mDiagram.getMinX();
        double maxX = mDiagram.getMaxX();
        double minY = mDiagram.getMinY();
        double maxY = mDiagram.getMaxY();

        double xCoordOfYAxis;
        double valueRangeOfYAxis;


        //Needs testing
        if (minY>=0) {
            valueRangeOfYAxis = maxY -minY;
        } else {
            valueRangeOfYAxis = abs(maxY) + abs(minY);
        }

        if (minX == 0) {
            xCoordOfYAxis = 0;
        } else {
            xCoordOfYAxis = minX/maxX;
        }
    }
}
