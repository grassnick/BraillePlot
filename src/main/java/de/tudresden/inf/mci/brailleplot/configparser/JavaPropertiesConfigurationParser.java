package de.tudresden.inf.mci.brailleplot.configparser;

import de.tudresden.inf.mci.brailleplot.util.UrlHelper;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/**
 * Concrete parser for configuration files in Java Property File format.
 * @author Leonard Kupper, Georg Graßnick
 * @version 2019.09.23
 */
public final class JavaPropertiesConfigurationParser extends ConfigurationParser {

    private static final String INCLUDE_FILE_EXTENSION = ".properties";

    /**
     * Constructor.
     *
     * Parse the configuration from a Java Property File (.properties) with a given default configuration.
     * @param filePath The path of the Java Property File.
     * @param defaultPath The path to the Java Property File containing the default properties.
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */

    public JavaPropertiesConfigurationParser(final Path filePath, final URL defaultPath) throws ConfigurationParsingException, ConfigurationValidationException {
        setup();
        parseConfigFileFromResource(defaultPath, false);
        setDefaults(getPrinter(), getRepresentation(), getFormat("default"));
        parseConfigFileFromFileSystem(filePath, true);
    }

    public JavaPropertiesConfigurationParser(final Path filePath, final Path defaultPath) throws ConfigurationParsingException, ConfigurationValidationException {
        setup();
        parseConfigFileFromFileSystem(defaultPath, false);
        setDefaults(getPrinter(), getRepresentation(), getFormat("default"));
        parseConfigFileFromFileSystem(filePath, true);
    }

    public JavaPropertiesConfigurationParser(final URL filePath, final Path defaultPath) throws ConfigurationParsingException, ConfigurationValidationException {
        setup();
        parseConfigFileFromFileSystem(defaultPath, false);
        setDefaults(getPrinter(), getRepresentation(), getFormat("default"));
        parseConfigFileFromResource(filePath, true);
    }

    public JavaPropertiesConfigurationParser(final URL filePath, final URL defaultPath) throws ConfigurationParsingException, ConfigurationValidationException {
        setup();
        parseConfigFileFromResource(defaultPath, false);
        setDefaults(getPrinter(), getRepresentation(), getFormat("default"));
        parseConfigFileFromResource(filePath, true);
    }

    private void setup() {
        setValidator(new JavaPropertiesConfigurationValidator());
    }

    /**
     * Concrete internal algorithm used for parsing the Java Property File.
     * This method is called by ({@link ConfigurationParser#parseConfigFileFromFileSystem(Path, boolean)} (InputStream, boolean)})
     * or {@link ConfigurationParser#parseConfigFileFromResource(URL, boolean)} where it was called to include other configurations.
     * @param inStream The fileToParse stream to read the configuration properties from.
     * @param path The URL identifying the location of the source of the {@link InputStream}. Required the inclusion of configurations from relative paths.
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    protected void parse(final InputStream inStream, final URL path) throws ConfigurationParsingException, ConfigurationValidationException {
        Objects.requireNonNull(inStream);
        Objects.requireNonNull(path);
        // Create property instance for current recursion level
        Properties properties = new Properties();

        try {
            // Load properties from the .properties file
            properties.load(inStream);
        } catch (IOException e) {
            throw new ConfigurationParsingException("Unable to load properties from file \"" + inStream + "\"", e);
        }
        // Iterate over all properties as key -> value pairs
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            // check for special property key: 'include'
            if (key.equalsIgnoreCase("include")) {
                includeResources(value, path);
            } else if (key.equalsIgnoreCase("include-file")) {
                includeFiles(value, path);
            } else {
                parseProperty(key, value);
            }
        }
    }

    private void parseProperty(final String key, final String value) throws ConfigurationValidationException {
        mLogger.trace("Parsed property \"{}\" with value \"{}\"", key, value);
        ValidProperty property = getValidator().validate(key, value);
        if (property instanceof FormatProperty) {
            addProperty((FormatProperty) property);
        }
        if (property instanceof PrinterProperty) {
            addProperty((PrinterProperty) property);
        }
        if (property instanceof RepresentationProperty) {
            addProperty((RepresentationProperty) property);
        }
    }

    /**
     * Recursively parses the configuration file.
     * This method handles files on the local file system.
     * @param fileList The string representations of the paths to include.
     * @param parentUrl The URL identifying the context from where the method was called. Required to construct relative paths.
     * @throws ConfigurationParsingException If something went wrong while reading from the included files.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    private void includeFiles(final String fileList, final URL parentUrl) throws ConfigurationParsingException, ConfigurationValidationException {
        for (String s : fileList.split(",")) {

            Path parentPath = null;
            try {
                parentPath = Path.of(parentUrl.toURI());
            } catch (URISyntaxException e) {
                throw new ConfigurationParsingException("Could not generate URI", e);
            }
            Path newPath = parentPath.resolve(s.trim() + INCLUDE_FILE_EXTENSION);
            String newPathString = newPath.toAbsolutePath().toString();

            mLogger.debug("Prepare recursive parsing of properties file in the file system for file \"{}\"", newPathString);

            try (InputStream is = new BufferedInputStream(new FileInputStream(newPathString))) {
                parse(is, UrlHelper.getParentUrl(newPath.toUri().toURL()));
            } catch (IOException e) {
                throw new ConfigurationParsingException("Could not open include file", e);
            }
        }
    }

    /**
     * Recursively parse configuration from a java resource.
     * @param fileList The string representations of the paths to include.
     * @param parentUrl The URL identifying the context from where the method was called. Required to construct relative paths.
     * @throws ConfigurationParsingException If errors occurred while reading from a resource.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    private void includeResources(final String fileList, final URL parentUrl) throws ConfigurationParsingException, ConfigurationValidationException {
        for (String s : fileList.split(",")) {
            s = s.trim();
            boolean isAbsolutePath = s.startsWith("/");

            URL newUrl = null;
            // If the value begins with a "/", treat path as absolute path in resources
            if (isAbsolutePath) {
                newUrl = getClass().getClassLoader().getResource(s.substring(1) + INCLUDE_FILE_EXTENSION);
                // else treat relative
            } else {
                try {
                    newUrl = new URL(parentUrl.getProtocol(), parentUrl.getHost(), UrlHelper.getPathString(parentUrl) + "/" + s.trim() + INCLUDE_FILE_EXTENSION);
                } catch (MalformedURLException e) {
                    throw new ConfigurationParsingException("Could not create URL to relative resource", e);
                }
            }

            mLogger.debug("Prepare recursive parsing of properties file in the java resources at \"{}\"", UrlHelper.getString(newUrl));

            try (InputStream is = newUrl.openStream()) {
                parse(is, UrlHelper.getParentUrl(newUrl));
            } catch (IOException e) {
                throw new ConfigurationParsingException("Could not open include resource", e);
            }
        }
    }
}
