package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.rendering.language.BrailleLanguage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simple representation of a legend.
 * @author Leonard Kupper, Andrey Ruzhanskiy
 * @version 2019.09.25
 */
public class Legend implements Renderable {

    private String mTitle;
    private BrailleLanguage.Language mLanguage;
    private Map<String, Map<String, String>> mStringExplanationLists = new LinkedHashMap<>();
    private Map<String, Map<Texture<Boolean>, String>> mTextureExplanationLists = new LinkedHashMap<>();
    private String mColumnViewTitle;

    private Map<String, Map<String, String>> mColumnView = new LinkedHashMap<>();
    private int mTextureExampleWidthCells = 1;
    private int mTextureExampleHeightCells = 1;

    // diagram type
    // Indicates which type of diagram a legend is plotted for.
    // TODO: Make legend semantically independent from diagrams
    private int mType;

    /**
     * Constructor. Creates a legend.
     */
    public Legend() {

    }

    /**
     * Constructor. Creates a legend with default language (DE_BASISSCHRIFT).
     * @param title The title of the legend.
     */
    public Legend(final String title) {
        setTitle(title);
        setLanguage(BrailleLanguage.Language.DE_BASISSCHRIFT);
    }

    /**
     * Constructor. Creates a legend with a defined language.
     * @param title The title of the legend.
     * @param language A {@link BrailleLanguage.Language}.
     */
    public Legend(final String title, final BrailleLanguage.Language language) {
        setTitle(title);
        setLanguage(language);
    }

    /**
     * Sets a new title.
     * @param title The new title for the legend.
     */
    public void setTitle(final String title) {
        mTitle = Objects.requireNonNull(title);
    }

    /**
     * Sets the braille language and level.
     * @param language The new language.
     */
    public void setLanguage(final BrailleLanguage.Language language) {
        mLanguage = Objects.requireNonNull(language);
    }

    /**
     * Gets the current title of the legend.
     * @return A {@link String} containing the title.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Gets the current braille language and level.
     * @return A {@link BrailleLanguage.Language} determining the language and braille level.
     */
    public BrailleLanguage.Language getLanguage() {
        return mLanguage;
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
     * Add a column to the columnview.
     * @param columnName Name of column.
     * @param explanations {@link Map} of symbols and descriptions inside the column.
     */
    public void addColumn(final String columnName, final Map<String, String> explanations) {
            mColumnView.put(columnName, explanations);
    }

    /**
     * Set the columnview title.
     * @param columnViewTitle The title for the columnview.
     */
    public void setColumnViewTitle(final String columnViewTitle) {
        this.mColumnViewTitle = columnViewTitle;
    }

    /**
     * Getter for the column-view.
     * @return {@link Map} representing the columnview.
     */
    public Map<String, Map<String, String>> getColumnView() {
        return mColumnView;
    }

    /**
     * Getter for the columnview-title.
     * @return {@link String} representing the columnview-title.
     */
    public String getColumnViewTitle() {
        return mColumnViewTitle;
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
    public final Map<String, Map<String, String>> getSymbolExplanationGroups() {
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

    /**
     * See source code comment to mType.
     * @param type The type to set.
     */
    public final void setType(final int type) {
        mType = type;
    }

    /**
     * See source code comment to mType.
     * @return The type of the legend.
     */
    public final int getType() {
        return mType;
    }

}
