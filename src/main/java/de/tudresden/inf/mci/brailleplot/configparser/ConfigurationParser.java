package de.tudresden.inf.mci.brailleplot.configparser;

import de.tudresden.inf.mci.brailleplot.util.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract parser for configuration files. Interface for {@link Printer} and multiple {@link Format} configurations.
 * Must be extended to implement a concrete parsing algorithm for a specific file format.
 * @author Leonard Kupper, Georg Graßnick
 * @version 2019.09.23
 */

public abstract class ConfigurationParser {

    private ConfigurationValidator mValidator;
    private Printer mPrinter;
    private Representation mRepresentation;
    private Map<String, Format> mFormats = new HashMap<>();
    private List<PrinterProperty> mPrinterProperties = new ArrayList<>();
    private List<RepresentationProperty> mRepresentationProperties = new ArrayList<>();
    private Map<String, List<FormatProperty>> mFormatProperties = new HashMap<>();
    private Printer mDefaultPrinter;
    private Representation mDefaultRepresentation;
    private Format mDefaultFormat;

    protected final Logger mLogger = LoggerFactory.getLogger(getClass());

    ConfigurationParser() {
    }

    /**
     * Internal algorithm used for parsing of the configuration file.
     * Implement this method by parsing information from the given input stream, optionally validating it (see {@link ConfigurationValidator}),
     * constructing {@link PrinterProperty} and {@link FormatProperty} objects from this information and adding them with the methods
     * {@link #addProperty(PrinterProperty)} and {@link #addProperty(FormatProperty)}.
     * This method is called by ({@link #parseConfigFile(InputStream, URL, boolean)}).
     * @param inStream The input stream to read the configuration from.
     * @param path The URL identifying the location of the source of the {@link InputStream}. Required the inclusion of configurations from relative paths.
     * @throws ConfigurationParsingException    On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    protected abstract void parse(InputStream inStream, URL path) throws ConfigurationParsingException, ConfigurationValidationException;

    /**
     * Get the representation configuration.
     * @return A {@link Representation} object, representing the representation properties.
     */
    public final Representation getRepresentation() {
        return mRepresentation;
    }

    /**
     * Get the printer configuration.
     *
     * @return A {@link Printer} object, representing the printers properties.
     */
    public final Printer getPrinter() {
        return mPrinter;
    }

    /**
     * Get the names of all available format configurations.
     *
     * @return A {@link Set}&lt;{@link String}&gt; containing the name of each format.
     */
    public final Set<String> getFormatNames() {
        return mFormats.keySet();
    }

    /**
     * Get a specific format configuration.
     *
     * @param formatName The name of the format.
     * @return A {@link Format} object, representing the formats properties.
     * @throws NoSuchElementException If no format has the specified name.
     */
    public final Format getFormat(final String formatName) {
        if (!mFormats.containsKey(formatName)) {
            throw new NoSuchElementException("Format does not exist: " + formatName);
        }
        return mFormats.get(formatName);
    }


    /**
     * Set a {@link ConfigurationValidator} for this parser.
     * This method should be called inside the concrete parsers constructor
     *
     * @param validator The {@link ConfigurationValidator} object.
     */
    protected void setValidator(final ConfigurationValidator validator) {
        mValidator = validator;
    }

    /**
     * Get the {@link ConfigurationValidator} for this parser.
     *
     * @return A {@link ConfigurationValidator} object.
     */
    protected ConfigurationValidator getValidator() {
        return mValidator;
    }

    /**
     * Add a general printer property to the internal printer configuration representation.
     *
     * @param property The represented property of the printer.
     */
    protected void addProperty(final PrinterProperty property) {
        mPrinterProperties.add(property);
    }

    /**
     * Add a general representation property to the internal printer configuration.
     * @param property The property of the representation.
     */
    protected void addProperty(final RepresentationProperty property) {
        mRepresentationProperties.add(property);
    }

    /**
     * Add a specific format property to the internal list of format configuration.
     * @param property The property of a specific format.
     */
    protected void addProperty(final FormatProperty property) {
        String formatName = property.getFormat();
        if (!mFormatProperties.containsKey(formatName)) {
            mFormatProperties.put(formatName, new ArrayList<>());
        }
        mFormatProperties.get(formatName).add(property);
    }

    /**
     * Set the optional default configurations for {@link Printer} and {@link Format} objects created by this parser.
     * This method should be called inside the concrete parsers constructor.
     *
     * @param defaultPrinter A {@link Printer} object containing the default properties or null for no default to be set.
     * @param defaultRepresentation A {@link Representation} object containing the default properties or null for no default to be set.
     * @param defaultFormat A {@link Format} object containing the default properties or null for no default to be set.
     */
    protected final void setDefaults(final Printer defaultPrinter, final Representation defaultRepresentation, final Format defaultFormat) {
        mDefaultPrinter = defaultPrinter;
        mDefaultRepresentation = defaultRepresentation;
        mDefaultFormat = defaultFormat;
    }

    /**
     * Parse a configuration file from the resource folder.
     * @param resource The URL identifying the configuration file.
     * @param assertCompleteness Signals whether to check for existence of all required properties or not
     * @throws ConfigurationParsingException if an error occurred reading from the resource identified by the URL resource parameter
     * @throws ConfigurationValidationException if an exception occurred while calling the {@link JavaPropertiesConfigurationValidator}
     */
    protected final void parseConfigFileFromResource(final URL resource, final boolean assertCompleteness) throws ConfigurationParsingException, ConfigurationValidationException {
        Objects.requireNonNull(resource);

        mLogger.debug("Starting parsing properties file from java resources: \"{}\"", resource);

        try {
            parseConfigFile(resource.openStream(), UrlHelper.getParentUrl(resource), assertCompleteness);
        } catch (IOException e) {
            throw new ConfigurationParsingException("Could not open resource at \"" + resource.toString() + "\"", e);
        }
    }

    /**
     * Parse a configuration file from the file system.
     * @param filePath The location of the file to parse.
     * @param assertCompleteness Signals whether to check for existence of all required properties or not
     * @throws ConfigurationParsingException if the file could not be read correctly
     * @throws ConfigurationValidationException if an exception occurred while calling the {@link JavaPropertiesConfigurationValidator}
     */
    protected final void parseConfigFileFromFileSystem(final Path filePath, final boolean assertCompleteness) throws ConfigurationParsingException, ConfigurationValidationException {
        Objects.requireNonNull(filePath);

        mLogger.debug("Starting parsing properties file from file system: \"{}\"", filePath);

        try {
            parseConfigFile(new FileInputStream(filePath.toFile()), UrlHelper.getParentUrl(filePath.toFile().toURI().toURL()), assertCompleteness);
        } catch (FileNotFoundException | MalformedURLException e) {
            throw new ConfigurationParsingException("Configuration file could not be read at \"" + filePath.toString() + "\"");
        }
    }

    /**
     * Parse the specified configuration file.
     * This method should be called inside the concrete parsers constructor after the optional default configurations
     * ({@link #setDefaults(Printer, Representation, Format)}) and the validator ({@link #setValidator(ConfigurationValidator)}) have been set.
     * @param config             The {@link InputStream} to be parsed
     * @param assertCompleteness Signals whether to check for existence of all required properties or not.
     * @throws ConfigurationParsingException    On any error while accessing the configuration file or syntax
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    private void parseConfigFile(final InputStream config, final URL path, final boolean assertCompleteness)
            throws ConfigurationParsingException, ConfigurationValidationException {
        // reset internal property buffer
        mPrinterProperties.clear();
        mRepresentationProperties.clear();
        mFormatProperties.clear();
        mValidator.setSearchPath(getPathNoFilePrefix(path));
        // load and parse file
        parse(config, path);
        // build printer object from added properties
        mPrinter = new Printer(mPrinterProperties);
        mRepresentation = new Representation(mRepresentationProperties);
        if (mDefaultPrinter != null) {
            mPrinter.setFallback(mDefaultPrinter);
        }
        if (mDefaultRepresentation != null) {
            mRepresentation.setFallback(mDefaultRepresentation);
        }
        if (assertCompleteness) {
            mValidator.checkPrinterConfigComplete(mPrinter);
            mValidator.checkRepresentationConfigComplete(mRepresentation);
        }
        // build format objects from added properties
        for (String formatName : mFormatProperties.keySet()) {
            Format newFormat = new Format(mFormatProperties.get(formatName), formatName);
            if (mDefaultFormat != null) {
                newFormat.setFallback(mDefaultFormat);
            }
            if (assertCompleteness) {
                mValidator.checkFormatConfigComplete(newFormat);
            }
            mFormats.put(formatName, newFormat);
        }
    }

    /**
     * Return a String representation of the path of a {@link URL}.
     * Strips the {@literal "}file:{@literal "} prefix from an URL, if it exist.
     * @param url The URL that needs to be stripped
     * @return The String representation of the path of a URL where the leading {@literal "}file:{@literal "} prefix is stripped.
     */
    private static String getPathNoFilePrefix(final URL url) throws ConfigurationParsingException {
        String urlString = UrlHelper.getPathString(url);
        return urlString.replaceAll("^file:", "");
    }
}
