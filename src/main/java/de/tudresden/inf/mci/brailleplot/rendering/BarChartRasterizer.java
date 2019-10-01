package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.diagrams.BarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.point.Point2DDouble;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.round;

/**
 * A rasterizer for instances of {@link CategoricalBarChart} which is able to display multiple bars per category.
 * The rasterizer is 'cell' based, restricted to 6-dot layouts.
 * @author Leonard Kupper
 * @version 2019.09.02
 */
public class BarChartRasterizer implements Rasterizer<CategoricalBarChart> {

    // Constants
    private static final int X_AXIS_UNIT_SIZE_DOTS = 4; // the size of one step on the x-axis in dots. Equals the amount of dots after which the used bar textures will repeat
    private static final int X_AXIS_TICK_SIZE_DOTS = 2; // the size of the x-axis tickmarks in dots
    private static final int HELP_LINE_MIN_LENGTH_DOTS = 3; // the min. distance in dots for which a help line from group caption to the groups first bar will appear
    private static final int LEGEND_TEXTURE_BAR_LENGTH = 3; // tells how big (in axis units) texture example bars in the legend are.

    private static final int DECIMAL_BASE = 10; // the base to which the magnitude of the axis scale will be calculated. change if you love headaches.

    // Rasterizer Properties
    private int mMaximumTitleHeightCells; // max. height of title area in cells
    private int mCaptionLengthCells; // width of the space reserved for category captions in cells
    private int mXAxisHeightCells; // height of the x-axis including ticks and labels in cells
    private double[] mUnitScalings; // valid multiples of order of magnitude (x value range) for the scaling of one x-axis step
    private int mMaxBarThicknessCells; // the maximum thickness of a single bar in cells
    private int mMinBarThicknessCells; // the minimum thickness of a single bar in cells
    private int mTitlePaddingCells; // the padding size between the title line(s) and the chart in cells
    private int mCaptionPaddingCells; // the padding size between the group captions on the left and the bar groups in cells
    private int mGroupPaddingCells; // the padding size between two neighbouring bar groups in cells
    private int mBarPaddingCells; // the padding size between two bars inside the same group in cells
    private String nonexistentDataText; // the text which is displayed for a missing datapoint
    private BrailleLanguage.Language mBrailleLanguage; // the preferred language & level for titles, captions, ...

    // Texture Management
    private List<Texture<Boolean>> mTextures = new ArrayList<>(); // A list of textures to differentiate bars inside a group
    // The alignment offsets for each texture ...
    private List<Integer> mPositiveTextureAlignments = new ArrayList<>(); // ... when used on a bar representing a positive value
    private List<Integer> mNegativeTextureAlignments = new ArrayList<>(); // ... or a negative value (it matters)

    // Sub rasterizers
    private LiblouisBrailleTextRasterizer mTextRasterizer;
    private LinearMappingAxisRasterizer mAxisRasterizer = new LinearMappingAxisRasterizer();
    private TextureRasterizer mTextureRasterizer = new TextureRasterizer();
    private LegendRasterizer mLegendRasterizer = new LegendRasterizer();

    // Intermediate variables
    // These will be calculated throughout the process and must be available between different functions
    private int mBarThickness;
    private Rectangle mFullChartCellArea, mPositiveChartCellArea, mNegativeChartCellArea, mCaptionCellArea;

    /**
     * Initialization of algorithm parameters.
     * @param canvas The {@link RasterCanvas} on which the rasterizer will work.
     */
    private void initConfig(final RasterCanvas canvas) {

        // This method will currently set the properties to some default values,
        // but we could change it to load them from somewhere else. Please comment in review.

        final double tenth = 0.1, fifth = 0.2, quarter = 0.25, half = 0.5, full = 1.0;
        mUnitScalings = new double[]{tenth, fifth, quarter, half, full};

        mMaximumTitleHeightCells = canvas.getRepresentation().getProperty("general.maxTitleHeight").toInt();
        mCaptionLengthCells = 1;
        mXAxisHeightCells = 2;

        mMaxBarThicknessCells = canvas.getRepresentation().getProperty("rasterize.barChart.maxBarThickness").toInt();
        mMinBarThicknessCells = canvas.getRepresentation().getProperty("rasterize.barChart.minBarThickness").toInt();

        mTitlePaddingCells = canvas.getRepresentation().getProperty("rasterize.barChart.padding.title").toInt();
        mCaptionPaddingCells = canvas.getRepresentation().getProperty("rasterize.barChart.padding.caption").toInt();
        mGroupPaddingCells = canvas.getRepresentation().getProperty("rasterize.barChart.padding.groups").toInt();
        mBarPaddingCells = canvas.getRepresentation().getProperty("rasterize.barChart.padding.bars").toInt();

        nonexistentDataText = canvas.getRepresentation().getProperty("general.nonexistentDataText").toString();

        mBrailleLanguage = BrailleLanguage.Language.valueOf(canvas.getRepresentation().getProperty("general.brailleLanguage").toString());

        // Load textures
        double[] rotate90 = {0, 0, 0, 1, 1, 0};
        registerTexture(new Texture<>(TexturedArea.BOTTOM_T_PATTERN).applyAffineTransformation(rotate90), 0, 0);
        registerTexture(new Texture<>(TexturedArea.LINE_PATTERN).applyAffineTransformation(rotate90), 0, 0);
        registerTexture(new Texture<>(TexturedArea.LETTER_Y_PATTERN).applyAffineTransformation(rotate90), 1, 1);
        registerTexture(new Texture<>(TexturedArea.DASHED_PATTERN).applyAffineTransformation(rotate90), 0, 2);
        registerTexture(new Texture<>(TexturedArea.GRID_PATTERN).applyAffineTransformation(rotate90), 1, 1);
    }

    private int registerTexture(final Texture<Boolean> texture, final int posAlign, final int negAlign) {
        mTextures.add(texture);
        mPositiveTextureAlignments.add(posAlign);
        mNegativeTextureAlignments.add(negAlign);
        return mTextures.size();
    }

    /**
     * Rasterizes a {@link BarChart} instance onto a {@link RasterCanvas}.
     * @param diagram A instance of {@link BarChart} representing the bar chart diagram.
     * @param canvas A instance of {@link RasterCanvas} representing the target for the rasterizer output.
     * @throws InsufficientRenderingAreaException If too few space is available on the {@link RasterCanvas}
     * to display the given diagram.
     */
    public void rasterize(final CategoricalBarChart diagram, final RasterCanvas canvas) throws InsufficientRenderingAreaException {

        mTextRasterizer = new LiblouisBrailleTextRasterizer(canvas.getPrinter());
        if (!canvas.isSixDotBrailleRaster()) {
            throw new InsufficientRenderingAreaException("This rasterizer can only work with a 6-dot braille grid.");
        }

        initConfig(canvas);
        drawDiagram(diagram, canvas);
    }

    private void drawDiagram(final CategoricalBarChart diagram, final RasterCanvas canvas) throws InsufficientRenderingAreaException {
        try {
            Rectangle referenceCellArea = canvas.getCellRectangle();

            // PHASE 1 - LAYOUT: The following calculations will divide the canvas area to create the basic chart layout.
            // Diagram Title
            String title = diagram.getTitle();
            int titleLength = mTextRasterizer.getBrailleStringLength(title, mBrailleLanguage);
            int titleHeight = (int) Math.ceil(titleLength / referenceCellArea.getWidth());
            if (titleHeight > mMaximumTitleHeightCells) {
                throw new InsufficientRenderingAreaException("Title is too long. (Exceeds maximum height)");
            }
            Rectangle titleDotArea = canvas.toDotRectangle(referenceCellArea.removeFromTop(titleHeight));
            referenceCellArea.removeFromTop(mTitlePaddingCells);
            Rectangle yAxisNameDotArea = canvas.toDotRectangle(referenceCellArea.removeFromTop(1)); // Y-Axis Name
            Rectangle xAxisNameDotArea = canvas.toDotRectangle(referenceCellArea.removeFromBottom(1)); // X-Axis Name
            // X-Axis Area & Origin Y Coordinate
            Rectangle xAxisDotArea = canvas.toDotRectangle(referenceCellArea.removeFromBottom(mXAxisHeightCells));
            int originYDotCoordinate = xAxisDotArea.intWrapper().getY(); // top edge below chart area
            // Save full space between title and X-Axis for later
            mFullChartCellArea = new Rectangle(referenceCellArea);
            // Caption Area
            mCaptionCellArea = referenceCellArea.removeFromLeft(mCaptionLengthCells);
            referenceCellArea.removeFromLeft(mCaptionPaddingCells);
            // X-Axis Scaling
            double negativeValueRangeSize = findNegativeValueRangeSize(diagram);
            double positiveValueRangeSize = findPositiveValueRangeSize(diagram);
            double valueRangeSize = negativeValueRangeSize + positiveValueRangeSize;
            referenceCellArea.removeFromLeft(1); // Reserve for Y-Axis
            int availableUnits = findAvailableUnits(referenceCellArea, canvas);
            double xAxisScaling = findAxisScaling(valueRangeSize, availableUnits);
            double xAxisScalingMagnitude = pow(DECIMAL_BASE, floor(log10(xAxisScaling)));
            // Bar Area with positive and negative partition
            int negativeAvailableUnits = (int) round((negativeValueRangeSize / valueRangeSize) * availableUnits);
            int positiveAvailableUnits = availableUnits - negativeAvailableUnits;
            int negativeCells = (negativeAvailableUnits * X_AXIS_UNIT_SIZE_DOTS) / canvas.getCellWidth();
            int positiveCells = (positiveAvailableUnits * X_AXIS_UNIT_SIZE_DOTS) / canvas.getCellWidth();
            mNegativeChartCellArea = referenceCellArea.removeFromLeft(negativeCells);
            mPositiveChartCellArea = referenceCellArea.removeFromLeft(positiveCells);
            // Re-append the reserved space for Y-Axis to the left.
            mNegativeChartCellArea.setX(mNegativeChartCellArea.getX() - 1);
            mNegativeChartCellArea.setWidth(mNegativeChartCellArea.getWidth() + 1);
            // Y-Axis Area & Origin X Coordinate
            Rectangle yAxisDotArea;
            yAxisDotArea = canvas.toDotRectangle(mNegativeChartCellArea).fromRight(1);
            int originXDotCoordinate = yAxisDotArea.intWrapper().getX(); // right edge between negative and positive chart area
            // Bar Groups (Categories)
            int amountOfGroups = diagram.getDataSet().getSize(); // Count the total amount of groups
            int amountOfBars = countTotalBarAmount(diagram); // and bars
            if (amountOfBars >= 1) {
                if (amountOfGroups == amountOfBars) {       // If each group only contains a single bar
                    mGroupPaddingCells = mBarPaddingCells;  // this is done because there are no 'real' groups.
                }
                int availableSizeCells = mFullChartCellArea.intWrapper().getHeight();
                int baseSizeCells = baseSize(amountOfGroups, mGroupPaddingCells, amountOfBars, mBarPaddingCells);
                mBarThickness = Math.min((int) floor((availableSizeCells - baseSizeCells) / amountOfBars), mMaxBarThicknessCells);
                if (mBarThickness < mMinBarThicknessCells) {
                    throw new InsufficientRenderingAreaException("Not enough space to rasterize all bar groups.");
                }
                // Remove space from top which is not needed
                int requiredSizeCells = baseSizeCells + amountOfBars * mBarThickness;
                mFullChartCellArea.removeFromTop(mFullChartCellArea.intWrapper().getHeight() - requiredSizeCells);
            }

            // PHASE 2 - RASTERIZING: Now, every element of the chart will be drawn onto the according area.
            // Diagram Title
            mTextRasterizer.rasterize(new BrailleText(title, titleDotArea, mBrailleLanguage), canvas);
            // Y-Axis: no units, no tickmarks
            Axis yAxis = new Axis(Axis.Type.Y_AXIS, originXDotCoordinate, originYDotCoordinate, 1, 0);
            yAxis.setBoundary(yAxisDotArea);
            mAxisRasterizer.rasterize(yAxis, canvas);
            // Y-Axis name
            mTextRasterizer.rasterize(new BrailleText(diagram.getYAxisName(), yAxisNameDotArea, mBrailleLanguage), canvas);
            // X-Axis: units and labels
            Axis xAxis = new Axis(Axis.Type.X_AXIS, originXDotCoordinate, originYDotCoordinate, X_AXIS_UNIT_SIZE_DOTS, X_AXIS_TICK_SIZE_DOTS);
            xAxis.setBoundary(xAxisDotArea);
            xAxis.setLabels(generateNumericAxisLabels(xAxisScaling, xAxisScalingMagnitude, negativeAvailableUnits, positiveAvailableUnits));
            mAxisRasterizer.rasterize(xAxis, canvas);
            // X-Axis name
            mTextRasterizer.rasterize(new BrailleText(diagram.getXAxisName(), xAxisNameDotArea, mBrailleLanguage), canvas);
            // The actual groups and bars:
            // This is done by iterating through the diagram data set and drawing borders with the respective padding based on whether switched
            // from one bar to another or a group to another. In between, the bars are rasterized as textured areas, with a line on the bars top.
            Rectangle borderBeforeCellArea, barCellArea, borderAfterCellArea;
            Map<String, String> groupNameExplanations = new LinkedHashMap<>();
            Map<Texture<Boolean>, String> textureExplanations = new LinkedHashMap<>();
            borderBeforeCellArea = mFullChartCellArea.removeFromTop(1); // Reserve first line for first border.
            char groupCaptionLetter = 'a';
            int group = 0;
            for (PointList pointList : diagram.getDataSet()) { // For each group:
                String groupName = pointList.getName(); // Save group name for legend
                groupNameExplanations.put(Character.toString(groupCaptionLetter), groupName);
                int amountOfBarsInGroup = amountOfBars / amountOfGroups;
                Iterator<Point2DDouble> points = pointList.getListIterator();
                for (int bar = 0; bar < amountOfBarsInGroup; bar++) {
                    barCellArea = mFullChartCellArea.removeFromTop(mBarThickness);
                    if (bar < (amountOfBarsInGroup - 1)) {
                        borderAfterCellArea = mFullChartCellArea.removeFromTop(mBarPaddingCells); // If another bar in the same group follows, use bar padding.
                    } else if (group < (amountOfGroups - 1)) {
                        borderAfterCellArea = mFullChartCellArea.removeFromTop(mGroupPaddingCells); // If a new group follows, use group padding.
                    } else {
                        borderAfterCellArea = mFullChartCellArea.removeFromTop(1); // If nothing follows, use single line for last border.
                    }
                    // the actual bar
                    int barLength;
                    if (points.hasNext()) {
                        Point2DDouble point = points.next();
                        barLength = (int) round(X_AXIS_UNIT_SIZE_DOTS * point.getY() / xAxisScaling);
                        int textureID = bar % mTextures.size();
                        drawBar(barLength, originXDotCoordinate, borderBeforeCellArea, barCellArea, borderAfterCellArea, textureID, canvas);
                        // Save used texture and corresponding name for explanation in legend
                        Texture<Boolean> exampleTexture = mTextures.get(textureID).setAffineTransformation(new double[]{mPositiveTextureAlignments.get(textureID), 0});
                        textureExplanations.put(exampleTexture, diagram.getCategoryName(bar));
                    } else {
                        barLength = 0; // nonexistent data point
                        Rectangle hintTextCellArea = new Rectangle(barCellArea);
                        hintTextCellArea.removeFromLeft(1 + ceil(originXDotCoordinate / (double) canvas.getCellWidth())); // move to left side of y axis
                        BrailleText nonexistentDataHint = new BrailleText(nonexistentDataText, canvas.toDotRectangle(hintTextCellArea));
                        mTextRasterizer.rasterize(nonexistentDataHint, canvas);
                    }
                    borderBeforeCellArea = borderAfterCellArea;
                    if (bar == (amountOfBarsInGroup - 1) / 2) { // the group caption for each first bar in a group
                        Rectangle groupCaptionDotArea = canvas.toDotRectangle(mCaptionCellArea.intersectedWith(barCellArea));
                        BrailleText captionName = new BrailleText(Character.toString(groupCaptionLetter), groupCaptionDotArea.translatedBy(0, mBarThickness * canvas.getCellHeight() * (1 - (amountOfBarsInGroup % 2))));
                        mTextRasterizer.rasterize(captionName, canvas);
                        groupCaptionLetter++;
                        int dashedLineStartX = canvas.toDotRectangle(mNegativeChartCellArea).intWrapper().getX(); // dashed caption help line
                        int dashedLineStartY = canvas.toDotRectangle(barCellArea).intWrapper().getY() + 1;
                        int dashedLineLength = max(0, (originXDotCoordinate - 1 + min(0, barLength)) - dashedLineStartX - 1);
                        if (dashedLineLength >= HELP_LINE_MIN_LENGTH_DOTS) { // omit short help lines
                            Rasterizer.dashedLine(dashedLineStartX, dashedLineStartY, dashedLineLength, true, canvas.getCurrentPage(), 1);
                        }
                    }
                }
                group++;
            }

            // PHASE 3 - DIAGRAM LEGEND: Symbols and textures are explained in the legend which will be created by the LegendRasterizer
            Legend diagramLegend = new Legend(title, mBrailleLanguage); // Create a legend container
            diagramLegend.addSymbolExplanation("Achsenskalierung:", "X-Achse", "Faktor " + xAxisScalingMagnitude); // Explain axis scaling
            diagramLegend.addSymbolExplanationGroup("Kategorien:", groupNameExplanations); // Explain bar group single character captions
            if (textureExplanations.size() > 1) { // Explain textures (if multiple of them were used)
                diagramLegend.addTextureExplanationGroup("Reihen:", textureExplanations);
                diagramLegend.setTextureExampleSize(X_AXIS_UNIT_SIZE_DOTS * LEGEND_TEXTURE_BAR_LENGTH / canvas.getCellWidth(), mBarThickness);
            }
            mLegendRasterizer.rasterize(diagramLegend, canvas); // Rasterize legend
        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("The layout for the amount of given data can not be fitted on the format.", e);
        }
    }

    private void drawBar(
            final int barLength,
            final int originXDotCoordinate,
            final Rectangle topBorderCellArea,
            final Rectangle barTextureCellArea,
            final Rectangle bottomBorderCellArea,
            final int textureID,
            final RasterCanvas canvas
    ) throws Rectangle.OutOfSpaceException, InsufficientRenderingAreaException {

        MatrixData<Boolean> page = canvas.getCurrentPage();

        // First draw the top border of the top bar
        int topBorderYDotCoordinate = canvas.toDotRectangle(topBorderCellArea).intWrapper().getBottom();
        Rasterizer.line(originXDotCoordinate, topBorderYDotCoordinate, barLength, true, page, true);

        // Then draw the texture and line at bars end
        Rectangle barTextureDotArea, endBorderLine;
        int textureAlignment;
        if (barLength >= 0) {
            textureAlignment = mPositiveTextureAlignments.get(textureID);
            barTextureDotArea = canvas.toDotRectangle(barTextureCellArea
                    .intersectedWith(mPositiveChartCellArea))
                    .fromLeft(barLength);
            endBorderLine = barTextureDotArea.removeFromRight(min(barLength, 1)); // min required for the case barLength=0
        } else {
            barTextureDotArea = canvas.toDotRectangle(barTextureCellArea
                    .intersectedWith(mNegativeChartCellArea))
                    .fromRight(abs(barLength)).translatedBy(-1, 0);
            endBorderLine = barTextureDotArea.removeFromLeft(1);
            textureAlignment = mNegativeTextureAlignments.get(textureID) + barLength % X_AXIS_UNIT_SIZE_DOTS;
        }
        if (barLength != 0) {
            Rasterizer.rectangle(endBorderLine, page, true);
            mTextureRasterizer.rasterize(
                    new TexturedArea(
                            mTextures.get(textureID).setAffineTransformation(new double[]{textureAlignment, 0}),
                            barTextureDotArea),
                    canvas
            );
        }

        // Lastly draw the bottom border of the top bar
        int bottomBorderYDotCoordinate = canvas.toDotRectangle(bottomBorderCellArea).intWrapper().getY();
        Rasterizer.line(originXDotCoordinate, bottomBorderYDotCoordinate, barLength, true, page, true);
    }

    //  HELP METHODS
    //  ============

    private double findNegativeValueRangeSize(final BarChart diagram) {
        return abs(min(diagram.getMinY(), 0));
    }
    private double findPositiveValueRangeSize(final BarChart diagram) {
        return max(diagram.getMaxY(), 0);
    }

    private int findAvailableUnits(final Rectangle cellArea, final RasterCanvas canvas) {
        int availableCells = cellArea.intWrapper().getWidth();
        double cellsPerXAxisUnit = ((double) X_AXIS_UNIT_SIZE_DOTS / canvas.getCellWidth());
        return (int) Math.floor(availableCells / cellsPerXAxisUnit);
    }

    private double findAxisScaling(final double valueRangeSize, final int availableUnits) {
        double minRangePerUnit = valueRangeSize / availableUnits; // this range must fit into one 'axis step'
        double orderOfMagnitude = pow(DECIMAL_BASE, ceil(log10(minRangePerUnit)));
        double scaledRangePerUnit = 0;
        for (double scaling : mUnitScalings) {
            scaledRangePerUnit = (scaling * orderOfMagnitude);
            if (scaledRangePerUnit >= minRangePerUnit) {
                break;
            }
        }
        return scaledRangePerUnit;
    }

    private int countTotalBarAmount(final BarChart diagram) throws InsufficientRenderingAreaException {
        int amountOfBars = 0;

        int barsPerGroup = 0;
        for (PointList group : diagram.getDataSet()) {
            if (group.getSize() > barsPerGroup) {
                barsPerGroup = group.getSize();
            }

        }
        if (barsPerGroup > mTextures.size()) {
            throw new InsufficientRenderingAreaException("The maximum amount of bars in a group is " + mTextures.size());
        }
        amountOfBars = diagram.getDataSet().getSize() * barsPerGroup;

        /*
        for (PointList group : diagram.getDataSet()) {
            int barsInGroup = group.getSize();
            if (barsInGroup > mTextures.size()) {
                throw new InsufficientRenderingAreaException("The maximum amount of bars in a group is " + mTextures.size());
            }
            amountOfBars += barsInGroup;
        }
         */

        return amountOfBars;
    }

    private int baseSize(final int groups, final int groupPad, final int bars, final int barPad) {
        return (groups * (groupPad - barPad) + (bars * barPad) + 2 - groupPad);
    }

    /*
    This commented method is a different approach for labeling the axis, but I don't know if we should keep it.
    The idea is to label the tickmarks with a running index like "a, b, c, ...." and explaining every
    single index charater on the legend. But I didn't found any example of this being done in practice.

    private Map<Integer, String> generateSingleCharAxisLabels(final int negativeUnits, final int positiveUnits) {
        Map<Integer, String> labels = new HashMap<>();
        char labelLetter = 1;
        for (int axisTick = negativeUnits * -1; axisTick <= positiveUnits; axisTick += 1) {
            labels.put(axisTick, Character.toString(labelLetter));
            labelLetter++;
        }
        return labels;
    }
     */

    private Map<Integer, String> generateNumericAxisLabels(final double scaling, final double orderOfMagnitude, final int negativeUnits, final int positiveUnits) {
        Map<Integer, String> labels = new HashMap<>();
        for (int axisTick = negativeUnits * -1; axisTick <= positiveUnits; axisTick += 1) {
            int value = (int) ((axisTick * scaling) / orderOfMagnitude);
            String label = Integer.toString(value);
            labels.put(axisTick, label);
        }
        return labels;
    }

}
