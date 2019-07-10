package de.tudresden.inf.mci.brailleplot.rendering;

import java.util.Objects;

/**
 * Representation of a text field.
 */
public class Text implements Renderable {

    private String mContent;
    private Rectangle mArea;

    public Text(final String content, final Rectangle area) {
        setText(content);
        setArea(area);
    }

    public void setText(String content) {
        mContent = Objects.requireNonNull(content);
    }
    public String getText() {
        return mContent;
    }

    public void setArea(Rectangle area) {
        mArea = Objects.requireNonNull(area);
    }
    public Rectangle getArea() {
        return mArea;
    }
}
