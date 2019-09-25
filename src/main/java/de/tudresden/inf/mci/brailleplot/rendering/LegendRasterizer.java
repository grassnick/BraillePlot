package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.layout.Rectangle;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import java.util.Map;
import static java.lang.Integer.max;
import static java.lang.StrictMath.min;

/**
 * A rasterizer that is able to draw a legend on a new page.
 * @author Leonard Kupper, Andrey Ruzhanskiy
 * @version 2019.09.25
 */
public class LegendRasterizer implements Rasterizer<Legend> {

    private RasterCanvas mCanvas;
    private Legend mLegend;

    private static final int MIN_TEXT_WIDTH_CELLS = 10; // how much space should be available for an explanation text at least. (To avoid excessive line breaking)
    private static final int EXPLANATION_TEXT_INDENTATION_CELLS = 1; // indentation for explanation texts.
    private static final String LEGEND_KEYWORD = "Legende:"; // title for the legend

    // Sub rasterizers
    private LiblouisBrailleTextRasterizer mTextRasterizer;
    private TextureRasterizer mTextureRasterizer = new TextureRasterizer();

    /**
     * Rasterizes a {@link Legend} instance onto a {@link RasterCanvas}. Important: This creates a new page on the canvas!
     * @param legend An instance of {@link Legend} containing the legend contents.
     * @param canvas An instance of {@link RasterCanvas} representing the target for the rasterizer output.
     */
    @Override
    public void rasterize(final Legend legend, final RasterCanvas canvas) throws InsufficientRenderingAreaException {

        mTextRasterizer = new LiblouisBrailleTextRasterizer(canvas.getPrinter());
        mCanvas = canvas;
        mLegend = legend;

        // Create a fresh page on the canvas.
        canvas.getNewPage();
        Rectangle referenceCellArea = canvas.getCellRectangle();

        try {

            // Write "Legend" keyword + title
            writeLine(LEGEND_KEYWORD + " " + legend.getTitle(), referenceCellArea);

            // String explanation lists
            for (Map.Entry<String, Map<String, String>> list : legend.getSymbolExplanationGroups().entrySet()) {
                String groupName = list.getKey();
                writeLine(groupName + ":", referenceCellArea);
                moveIndentation(referenceCellArea, EXPLANATION_TEXT_INDENTATION_CELLS); // set indentation
                for (Map.Entry<String, String> explanation : list.getValue().entrySet()) {
                    String symbol = explanation.getKey();
                    String description = explanation.getValue();
                    writeLine(symbol + " - " + description, referenceCellArea);
                }
                moveIndentation(referenceCellArea, -1 * EXPLANATION_TEXT_INDENTATION_CELLS); // reset indentation
            }

            // Texture explanation lists
            for (Map.Entry<String, Map<Texture<Boolean>, String>> list : legend.getTextureExplanationGroups().entrySet()) {
                String groupName = list.getKey();
                writeLine(groupName + ":", referenceCellArea);
                moveIndentation(referenceCellArea, EXPLANATION_TEXT_INDENTATION_CELLS); // set indentation
                for (Map.Entry<Texture<Boolean>, String> explanation : list.getValue().entrySet()) {
                    Texture<Boolean> texture = explanation.getKey();
                    String description = explanation.getValue();
                    drawTextureExample(referenceCellArea, texture, description);
                }
                moveIndentation(referenceCellArea, -1 * EXPLANATION_TEXT_INDENTATION_CELLS); // reset indentation
            }

            // Columnview
            if (legend.getColumnView().size() > 0) {
                writeLine(legend.getColumnViewTitle(), referenceCellArea);
                // int columnWidthCells = referenceCellArea.intWrapper().getWidth() / legend.getColumnView().size();
                for (Map.Entry<String, Map<String, String>> list : legend.getColumnView().entrySet()) {
                    //Rectangle columnCellArea = referenceCellArea.removeFromLeft(columnWidthCells);
                    Rectangle columnCellArea = new Rectangle(referenceCellArea);
                    //moveIndentation(columnCellArea, EXPLANATION_TEXT_INDENTATION_CELLS);
                    writeLine(list.getKey(), columnCellArea);
                    int maxWidth = 0;
                    for (Map.Entry<String, String> explanation : list.getValue().entrySet()) {
                        String symbol = explanation.getKey();
                        String description = explanation.getValue();
                        String textToWrite = symbol + " - " + description;
                        try {
                            int usedWidth = writeLine(textToWrite, columnCellArea);
                            if (usedWidth > maxWidth) {
                                maxWidth = usedWidth;
                            }
                        } catch (Rectangle.OutOfSpaceException e) {
                            referenceCellArea.removeFromLeft(maxWidth + 1);
                            maxWidth = 0;
                            columnCellArea = new Rectangle(referenceCellArea);
                            columnCellArea.removeFromTop(1);
                            int usedWidth = writeLine(textToWrite, columnCellArea);
                            if (usedWidth > maxWidth) {
                                maxWidth = usedWidth;
                            }
                        }

                    }
                    referenceCellArea.removeFromLeft(maxWidth + 1 + EXPLANATION_TEXT_INDENTATION_CELLS);
                    //moveIndentation(columnCellArea, -1 * EXPLANATION_TEXT_INDENTATION_CELLS); // reset indentation
                }
            }

        } catch (Rectangle.OutOfSpaceException e) {
            throw new InsufficientRenderingAreaException("The amount of data in the legend does not fit on the format.", e);
        }

    }

    private void drawTextureExample(
            final Rectangle referenceCellArea,
            final Texture<Boolean> texture,
            final String description
    ) throws Rectangle.OutOfSpaceException, InsufficientRenderingAreaException {

        MatrixData<Boolean> page = mCanvas.getCurrentPage();

        // add padding between previous content and example
        referenceCellArea.removeFromTop(1);

        // reserve the overall area for the inner texture and description text
        int textureExampleHeightCells = mLegend.getTextureExampleHeightCells();
        int textureExampleWidthCells = mLegend.getTextureExampleWidthCells();
        Rectangle exampleCellArea = referenceCellArea.removeFromTop(textureExampleHeightCells);
        exampleCellArea.removeFromLeft(1); // space for left border
        Rectangle texturedDotArea = mCanvas.toDotRectangle(exampleCellArea.removeFromLeft(textureExampleWidthCells));
        referenceCellArea.fromTop(1); // just to make sure there is enough space for the bottom border

        // create surrounding rectangle for border of textured area
        Rectangle textureDotBorder = texturedDotArea.translatedBy(-1, -1);
        textureDotBorder.setHeight(textureDotBorder.getHeight() + 2);
        textureDotBorder.setWidth(textureDotBorder.getWidth() + 1);

        // reserve space for the texture description text
        Rectangle textCellArea = exampleCellArea;
        textCellArea.removeFromLeft(1); // padding between textured area and text
        // check if description height will fit
        int descriptionHeightCells = (int) Math.ceil(mTextRasterizer.getBrailleStringLength(description) / textCellArea.getWidth());
        if (descriptionHeightCells > textureExampleHeightCells) {
            int missingLines = descriptionHeightCells - textureExampleHeightCells;
            // in this case, description text needs more lines than texture, so we need to reserve the missing space
            referenceCellArea.removeFromTop(missingLines);
            textCellArea.setHeight(textCellArea.getHeight() + missingLines); // expand height
        }

        // draw textured area
        mTextureRasterizer.rasterize(new TexturedArea(texture, texturedDotArea), mCanvas);

        // draw border
        Rasterizer.rectangle(textureDotBorder, mCanvas.getCurrentPage(), true);

        // draw text
        mTextRasterizer.rasterize(new BrailleText(description, mCanvas.toDotRectangle(textCellArea)), mCanvas);
    }

    private void moveIndentation(final Rectangle cellArea, final int indent) {
        cellArea.setX(cellArea.getX() + indent);
        cellArea.setWidth(cellArea.getWidth() - indent);
    }

    private int writeLine(final String text, final Rectangle cellArea) throws InsufficientRenderingAreaException, Rectangle.OutOfSpaceException {
        if (cellArea.getWidth() < MIN_TEXT_WIDTH_CELLS) {
            throw new InsufficientRenderingAreaException("Not enough space for legend text.");
        }
        // write text lines
        int textLength = mTextRasterizer.getBrailleStringLength(text);
        int textHeight = max(1, (int) Math.ceil(textLength / cellArea.getWidth()));
        Rectangle textLineDotArea = mCanvas.toDotRectangle(cellArea.removeFromTop(textHeight));
        mTextRasterizer.rasterize(new BrailleText(text, textLineDotArea), mCanvas);
        return min(textLength, cellArea.intWrapper().getWidth());
    }
}
