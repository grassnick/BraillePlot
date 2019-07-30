package de.tudresden.inf.mci.brailleplot.rendering;

import java.util.Objects;

/**
 * Simple representation of a braille text field.
 * @author Leonard Kupper
 * @version 2019.07.22
 */
public class BrailleText implements Renderable {

    private String mContent;
    private Rectangle mArea;

    /**
     * Constructor. Creates a braille text field.
     * @param content The actual text of the text field.
     * @param area The desired area for the text to be rendered on.
     */
    public BrailleText(final String content, final Rectangle area) {
        setText(content);
        setArea(area);
    }

    /**
     * Sets a new text content.
     * @param content The new content for the text field.
     */
    public void setText(final String content) {
        mContent = Objects.requireNonNull(content);
    }

    /**
     * Gets the current text content of the text field.
     * @return A {@link String} containing the text.
     */
    public String getText() {
        return mContent;
    }

    /**
     * Sets a new area for the text field.
     * @param area The new area for the text field.
     */
    public void setArea(final Rectangle area) {
        mArea = Objects.requireNonNull(area);
    }

    /**
     * Gets the current area of the text field.
     * @return The area of the text field.
     */
    public Rectangle getArea() {
        return mArea;
    }
}
