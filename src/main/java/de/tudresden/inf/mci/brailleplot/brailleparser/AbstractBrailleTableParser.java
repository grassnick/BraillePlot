package de.tudresden.inf.mci.brailleplot.brailleparser;

import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printerbackend.NotSupportedFileExtensionException;

/**
 * Defines an interface which should be implemented in all parsers of braille tables.
 */
public abstract class AbstractBrailleTableParser {

    /**
     * Common method for querying the braille table.
     * @param key Braille cell, represented as string ("111000").
     * @return Byte, represented as int, corresponding to the given braille cell.
     */
    public abstract int getByteAsIntBackEnd(String key);
    public abstract String getCharToBraille(String key);

    public static AbstractBrailleTableParser getParser(final Printer printer, final String property) throws NotSupportedFileExtensionException {
        //read braille table path
        String brailleTablePath = printer.getProperty(property).toString();

        //read which kind of parser is needed (properties, json, xml,...)
        String[] parts = brailleTablePath.split("\\.");
        String fileEnding = parts[parts.length - 1]; // made safe for relative paths containing "./" or "../"
        switch (fileEnding) {
            case "properties":
                return new PropertiesParser(brailleTablePath);
            case "json":
                return new JsonParser(brailleTablePath);
            case "xml":
                return new XmlParser(brailleTablePath);
            default:
                throw new NotSupportedFileExtensionException("The Fileextension " + fileEnding + " is currently not supported.");
        }
    }

}
