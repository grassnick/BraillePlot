package de.tudresden.inf.mci.brailleplot;

import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Abstract configuration parser.
 * It implements file reading and property adding, but the method parse() needs to be implemented for a specific file
 * format by a concrete subclass.
 * @author Leonard Kupper
 * @version 04.06.19
 */

public abstract class ConfigurationParser {
    private String mConfigFilePath;
    private List<PrinterProperty> mPrinterProperties = new ArrayList<>();;
    private Map<String, List<FormatProperty>> mFormatProperties = new HashMap<>();
    protected FileInputStream mInput;
    protected Printer mPrinter;
    protected Map<String, Format> mFormats = new HashMap<>();
    protected ConfigurationValidator mValidator;
    private Printer mDefaultPrinter;
    private Format mDefaultFormat;

    /**
     * Implement the parse method by TODO.
     * @return
     */
    protected abstract Boolean parse();

    /**
     * Add a general printer property to the internal printer configuration representation.
     * @param property
     * @return
     */
    protected void addProperty(final PrinterProperty property) {
        mPrinterProperties.add(property);
    }

    /**
     * Add a specific format property to the internal list of format configuration representation.
     * @param property
     * @return
     */
    protected void addProperty(final FormatProperty property) {
        String formatName = property.getFormat();
        if (!mFormatProperties.containsKey(formatName)) {
            mFormatProperties.put(formatName, new ArrayList<>());
        }
        mFormatProperties.get(formatName).add(property);
    }

    private Boolean setConfigFile(final String filePath) {
        mConfigFilePath = filePath;
        try {
            mInput = new FileInputStream(mConfigFilePath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    protected final Boolean parseConfigFile(final String filePath) {
        // load and parse file
        Boolean isConfigFileSet = setConfigFile(filePath);
        Boolean isFileParsed = parse();
        // build printer object from added properties
        mPrinter = new Printer(mPrinterProperties);
        if (mDefaultPrinter != null) {
            mPrinter.setFallback(mDefaultPrinter);
        }
        // build format objects from added properties
        for (String formatName : mFormatProperties.keySet()) {
            Format newFormat = new Format(mFormatProperties.get(formatName));
            if (mDefaultFormat != null) {
                newFormat.setFallback(mDefaultFormat);
            }
            mFormats.put(formatName, newFormat);
        }
        return (isConfigFileSet && isFileParsed);
    }

    public final String getConfigFile() {
        return mConfigFilePath;
    }

    public final Printer getPrinter() {
        return mPrinter;
    }

    public final Set<String> getFormatNames() {
        return mFormats.keySet();
    }

    public final Format getFormat(final String formatName) {
        if (!mFormats.containsKey(formatName)) {
            throw new RuntimeException("Format does not exist: " + formatName);
        }
        return mFormats.get(formatName);
    }


    protected final void setDefaults(final Printer defaultPrinter, final Format defaultFormat) {
        mDefaultPrinter = defaultPrinter;
        mDefaultFormat = defaultFormat;
    }


    /**
     * Dummy method.
     */
    private void writeTestConfigFile() {
        try {
            // set dummy property object
            Properties dummyProp = new Properties();
            dummyProp.setProperty("general.name", "Printer Name");
            dummyProp.setProperty("general.number", "45");
            dummyProp.setProperty("general.nested.test", "I am a nested property");

            // save properties to config file
            OutputStream configOutput = new FileOutputStream(mConfigFilePath);
            dummyProp.store(configOutput, null);

            System.out.println(dummyProp.toString());

        } catch (IOException e) {
            e.getMessage();
        }
    }

}
