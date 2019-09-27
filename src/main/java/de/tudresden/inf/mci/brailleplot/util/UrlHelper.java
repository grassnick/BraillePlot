package de.tudresden.inf.mci.brailleplot.util;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * This class offers static helper methods for working wth {@link URL} objects.
 * @author Georg Graßnick
 * @version 2019.09.27
 */
public final class UrlHelper {

    private UrlHelper() { }

    /**
     * Get the string representation of the path of a {@link URL}.
     * For example, spaces are taken care of here.
     * @param url The {@link URL} to analyze.
     * @return The string representation of the path of the {@link URL}.
     */
    public static String getPathString(final URL url) {
        return URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
    }
}
