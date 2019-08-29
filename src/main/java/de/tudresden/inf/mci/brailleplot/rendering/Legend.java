package de.tudresden.inf.mci.brailleplot.rendering;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simple representation of a legend.
 * @author Leonard Kupper
 * @version 2019.08.29
 */
public class Legend implements Renderable {

    private String mTitle;
    private Map<String, Map<String, String>> mStringExplanationLists = new LinkedHashMap<>();
    private Map<String, Map<Texture<Boolean>, String>> mTextureExplanationLists = new LinkedHashMap<>();

    private int mTextureExampleWidthCells = 1;
    private int mTextureExampleHeightCells = 1;

    /**
     * Constructor. Creates a legend.
     * @param title The title of the legend.
     */
    public Legend(final String title) {
        setTitle(title);
    }

    /**
     * Sets a new text content.
     * @param title The new title for the legend.
     */
    public void setTitle(final String title) {
        mTitle = Objects.requireNonNull(title);
    }

    /**
     * Gets the current title of the legend.
     * @return A {@link String} containing the title.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Add a text symbol and the associated description text to the legend.
     * @param groupName The name of the header under which explanations of this group will be placed. (e.g. "Categories")
     * @param symbol The actual symbol to be explained, which appears in the diagram. (e.g. "A")
     * @param descriptionText A text describing the meaning of the symbol/abbreviation. (e.g. a long name of a category)
     */
    public void addSymbolExplanation(final String groupName, final String symbol, final String descriptionText) {
        if (!mStringExplanationLists.containsKey(groupName)) {
            mStringExplanationLists.put(groupName, new LinkedHashMap<>());
        }
        mStringExplanationLists.get(groupName).put(symbol, descriptionText);
    }

    /**
     * Add a texture and the associated description text to the legend.
     * @param groupName The name of the header under which explanations of this group will be placed. (e.g. "Series")
     * @param texture The actual texture to be explained, which is used in the diagram.
     * @param descriptionText A text describing the meaning of the texture. (e.g. "Series 1")
     */
    public void addTextureExplanation(final String groupName, final Texture<Boolean> texture, final String descriptionText) {
        if (!mTextureExplanationLists.containsKey(groupName)) {
            mTextureExplanationLists.put(groupName, new LinkedHashMap<>());
        }
        mTextureExplanationLists.get(groupName).put(texture, descriptionText);
    }

    /**
     * Add a complete group of text symbol explanations to the legend.
     * @param groupName The name of the header under which explanations of this group will be placed. (e.g. "Categories")
     * @param explanationGroup A map listing the text symbols and their associated description texts.
     */
    public void addSymbolExplanationGroup(final String groupName, final Map<String, String> explanationGroup) {
        mStringExplanationLists.put(groupName, explanationGroup);
    }

    /**
     * Add a complete group of texture explanations to the legend.
     * @param groupName The name of the header under which explanations of this group will be placed. (e.g. "Categories")
     * @param explanationGroup A map listing the textures and their associated description texts.
     */
    public void addTextureExplanationGroup(final String groupName, final Map<Texture<Boolean>, String> explanationGroup) {
        mTextureExplanationLists.put(groupName, explanationGroup);
    }

    /**
     * Set the size (in cells) of the example rectangles displaying textures from the texture explanation groups.
     * @param widthCells The new width in cells.
     * @param heightCells The new height in cells.
     */
    public void setTextureExampleSize(final int widthCells, final int heightCells) {
        mTextureExampleWidthCells = widthCells;
        mTextureExampleHeightCells = heightCells;
    }

    /**
     * Get all text symbol explanation groups from the legend.
     * @return A map associating every group name with a map listing the text symbols and their associated description texts.
     */
    final Map<String, Map<String, String>> getSymbolExplanationGroups() {
        return mStringExplanationLists;
    }

    /**
     * Get all texture explanation groups from the legend.
     * @return A map associating every group name with a map listing the textures and their associated description texts.
     */
    final Map<String, Map<Texture<Boolean>, String>> getTextureExplanationGroups() {
        return mTextureExplanationLists;
    }

    final int getTextureExampleWidthCells() {
        return mTextureExampleWidthCells;
    }

    final int getTextureExampleHeightCells() {
        return mTextureExampleHeightCells;
    }
}
