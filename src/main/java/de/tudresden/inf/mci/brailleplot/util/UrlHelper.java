package de.tudresden.inf.mci.brailleplot.util;

import de.tudresden.inf.mci.brailleplot.configparser.ConfigurationParsingException;

import java.net.MalformedURLException;
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

    /**
     * Returns the URL to the parent directory of a File / Resource.
     * @param resourcePath The URL to analyze.
     * @return The URL to the parent directory of the specified URL.
     * @throws RuntimeException if the generated URL is not a valid URL.
     */
    public static URL getParentUrl(final URL resourcePath) throws ConfigurationParsingException {
        String fileString = getPathString(resourcePath);
        String parentString = fileString.substring(0, fileString.lastIndexOf("/"));
        try {
            return new URL(resourcePath.getProtocol(), resourcePath.getHost(), parentString);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not create URL to parent path", e);
        }
    }
}
