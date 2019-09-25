package de.tudresden.inf.mci.brailleplot.configparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Abstract parser for configuration files. Interface for {@link Printer} and multiple {@link Format} configurations.
 * Must be extended to implement a concrete parsing algorithm for a specific file format.
 * @author Leonard Kupper
 * @version 2019.07.18
 */

public abstract class ConfigurationParser {

    private ConfigurationValidator mValidator;
    private Printer mPrinter;
    private Representation mRepresentation;
    private Map<String, Format> mFormats = new HashMap<>();
    private File mCurrentConfigFile;
    private List<PrinterProperty> mPrinterProperties = new ArrayList<>();
    private List<RepresentationProperty> mRepresentationProperties = new ArrayList<>();
    private Map<String, List<FormatProperty>> mFormatProperties = new HashMap<>();
    private Printer mDefaultPrinter;
    private Representation mDefaultRepresentation;
    private Format mDefaultFormat;

    ConfigurationParser() {

    };

    /**
     * Internal algorithm used for parsing of the configuration file.
     * Implement this method by parsing information from the given input stream, optionally validating it (see {@link ConfigurationValidator}),
     * constructing {@link PrinterProperty} and {@link FormatProperty} objects from this information and adding them with the methods
     * {@link #addProperty(PrinterProperty)} and {@link #addProperty(FormatProperty)}.
     * This method is called by ({@link #parseConfigFile(String, boolean)}).
     * @param input The input stream to read the configuration from.
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    protected abstract void parse(FileInputStream input) throws ConfigurationParsingException, ConfigurationValidationException;


    /**
     * Get the current configuration file.
     * @return A {@link File} object representing the configuration file.
     */
    public final File getConfigFile() {
        return mCurrentConfigFile;
    }

    /**
     * Get the representation configuration.
     * @return A {@link Representation} object, representing the representation properties.
     */
    public final Representation getRepresentation() {
        return mRepresentation;
    }

    /**
     * Get the printer configuration.
     * @return A {@link Printer} object, representing the printers properties.
     */
    public final Printer getPrinter() {
        return mPrinter;
    }

    /**
     * Get the names of all available format configurations.
     * @return A {@link Set}&lt;{@link String}&gt; containing the name of each format.
     */
    public final Set<String> getFormatNames() {
        return mFormats.keySet();
    }

    /**
     * Get a specific format configuration.
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
     * @param validator The {@link ConfigurationValidator} object.
     */
    protected void setValidator(final ConfigurationValidator validator) {
        mValidator = validator;
    }

    /**
     * Get the {@link ConfigurationValidator} for this parser.
     * @return A {@link ConfigurationValidator} object.
     */
    protected ConfigurationValidator getValidator() {
        return mValidator;
    }

    /**
     * Add a general printer property to the internal representation configuration.
     * @param property The property of the printer.
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
     * Parse the specified configuration file.
     * This method should be called inside the concrete parsers constructor after the optional default configurations
     * ({@link #setDefaults(Printer, Representation, Format)}) and the validator ({@link #setValidator(ConfigurationValidator)}) have been set.
     * @param filePath The configuration file to be parsed. The type depends on the concrete implementation of the parser.
     * @param assertCompleteness Signals whether to check for existence of all required properties or not.
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    protected final void parseConfigFile(final String filePath, final boolean assertCompleteness)
            throws ConfigurationParsingException, ConfigurationValidationException {
        // reset internal property buffer
        mPrinterProperties.clear();
        mFormatProperties.clear();
        // load and parse file
        mCurrentConfigFile = new File(filePath);
        FileInputStream input = openInputStream(filePath);
        parse(input);
        closeInputStream(input);
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
     * Opens the input stream for the given file path.
     * @param filePath The file to read from.
     * @return A {@link FileInputStream} for the given file path.
     * @throws ConfigurationParsingException On any error while opening the stream. (e.g. missing file)
     */
    final FileInputStream openInputStream(final String filePath) throws ConfigurationParsingException {
        try {
            return new FileInputStream(filePath);
        } catch (IOException e) {
            throw new ConfigurationParsingException("Unable to read configuration file", e);
        }
    }

    /**
     * Closes the given input stream.
     * @param input The {@link FileInputStream} to be closed.
     * @throws ConfigurationParsingException On any error while closing the stream.
     */
    final void closeInputStream(final FileInputStream input) throws ConfigurationParsingException {
        try {
            input.close();
        } catch (IOException e) {
            throw new ConfigurationParsingException("Unable to close input stream.", e);
        }
    }
}
