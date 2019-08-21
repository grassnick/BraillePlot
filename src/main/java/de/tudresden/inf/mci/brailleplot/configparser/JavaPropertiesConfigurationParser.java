package de.tudresden.inf.mci.brailleplot.configparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Stack;

/**
 * Concrete parser for configuration files in Java Property File format.
 * @author Leonard Kupper
 * @version 2019.07.18
 */
public final class JavaPropertiesConfigurationParser extends ConfigurationParser {

    //ArrayList<String> mInclusionStack;
    Stack<File> mInclusionStack = new Stack<>();

    /**
     * Constructor.
     *
     * Parse the configuration from a Java Property File (.properties) with a given default configuration.
     * @param filePath The path of the Java Property File.
     * @param defaultPath The path to the Java Property File containing the default properties.
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    public JavaPropertiesConfigurationParser(
            final String filePath,
            final String defaultPath
    ) throws ConfigurationParsingException, ConfigurationValidationException {
        setValidator(new JavaPropertiesConfigurationValidator());
        parseConfigFile(defaultPath, false);
        setDefaults(getPrinter(), getFormat("default"));
        parseConfigFile(filePath, true);
    }

    /**
     * Concrete internal algorithm used for parsing the Java Property File.
     * This method is called by ({@link #parseConfigFile(String, boolean)}) and will call itself recursively for every included file.
     * @param input The input stream to read the configuration properties from.
     * @throws ConfigurationParsingException On any error while accessing the configuration file or syntax.
     * @throws ConfigurationValidationException On any error while checking the parsed properties validity.
     */
    protected void parse(final FileInputStream input) throws ConfigurationParsingException, ConfigurationValidationException {
        // Create property instance for current recursion level
        Properties properties = new Properties();
        try {
            // Load properties from the .properties file
            properties.load(input);
        } catch (IOException e) {
            throw new ConfigurationParsingException("Unable to load properties from file.", e);
        }
        // Iterate over all properties as key -> value pairs
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            // check for special property key: 'include'
            if (("include").equals(key.toLowerCase())) {
                includeFiles(value);
            } else {
                parseProperty(key, value);
            }
        }
    }

    private void parseProperty(final String key, final String value) throws ConfigurationValidationException {
        ValidProperty property = getValidator().validate(key, value);
        if (property instanceof FormatProperty) {
            addProperty((FormatProperty) property);
        }
        if (property instanceof PrinterProperty) {
            addProperty((PrinterProperty) property);
        }
    }

    private void includeFiles(final String fileList) throws ConfigurationParsingException, ConfigurationValidationException {
        for (String includeName : fileList.split(",")) {
            if (mInclusionStack.empty()) {
                mInclusionStack.push(getConfigFile());
            }
            File includeFile, parentFile = mInclusionStack.peek().getParentFile();
            try {
                String findIncludePath = parentFile.getAbsolutePath() + File.separator + includeName.trim();
                File abstractPath = new File(findIncludePath);
                if (!abstractPath.exists()) {
                    abstractPath = new File(findIncludePath + ".properties");
                }
                includeFile = abstractPath.getCanonicalFile();
            } catch (IOException e) {
                throw new ConfigurationParsingException("Can not find include file.", e);
            }
            if (mInclusionStack.contains(includeFile)) {
                continue;
            }
            FileInputStream includeInput = openInputStream(includeFile.getAbsolutePath());
            mInclusionStack.push(includeFile);
            parse(includeInput);
            mInclusionStack.pop();
            closeInputStream(includeInput);
        }
    }
}
