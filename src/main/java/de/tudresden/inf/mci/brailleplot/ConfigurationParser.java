package de.tudresden.inf.mci.brailleplot;

import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;
import java.util.Set;

/**
 * Currently a prototype class. Experimenting with java property files.
 * @author Leonard Kupper
 * @version 31.05.19
 */

public abstract class ConfigurationParser {
    private String mConfigFilePath;
    protected FileInputStream mInput;
    protected Printer mPrinter;
    protected Map<String, Format> mFormats;

    //private Properties mProperties = new Properties();

    /**
     * Construct a ConfigurationParser.
     */
    /*
    public ConfigurationParser(final String configFilePath) {
        mConfigFilePath = configFilePath;
        writeTestConfigFile();
    }


    public Map<String, String> getPrinterProperties() {
        return new HashMap<String, String>();
    }
    public List<String> getFormatList() {
        return new ArrayList<String>();
    }
    public Map<String, String> getFormatProperties(final String formatName) {
        return new HashMap<String, String>();
    }
     */

    /**
     * Implement the parse method. It creates the representation.
     * @return
     */
    protected abstract Boolean parse();

    private final Boolean setConfigFile(final String filePath) {
        mConfigFilePath = filePath;
        try {
            mInput = new FileInputStream(mConfigFilePath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public final Boolean parseConfigFile(final String filePath) {
        Boolean isConfigFileSet = setConfigFile(filePath);
        Boolean isFileParsed = parse();
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
        return mFormats.get(formatName);
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
