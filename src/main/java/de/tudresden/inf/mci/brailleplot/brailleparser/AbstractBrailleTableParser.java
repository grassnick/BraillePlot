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
    public abstract int getByteAsInt(String key);


    /**
     * Common method for retrieving the Braillecell to the given byte. Usable only for one char at a time.
     * @param value Byte, as String represented (property files knows only Strings)
     * @return Braillecell, as String encoded : 123456.
     */
    public abstract String getDots(String value);

    public static AbstractBrailleTableParser getParser(final Printer printer) throws NotSupportedFileExtensionException {
        //read braille table path
        String brailleTablePath = printer.getProperty("brailletable").toString();

        //read which kind of parser is needed (properties, json, xml,...)
        String fileEnding = brailleTablePath.split("\\.")[1];
        switch (fileEnding) {
            case "properties":
                return new PropertiesParser(printer.getProperty("brailletable").toString());
            case "json":
                return new JsonParser(printer.getProperty("brailletable").toString());
            case "xml":
                return new XmlParser(printer.getProperty("brailletable").toString());
            default:
                throw new NotSupportedFileExtensionException("The Fileextension " + fileEnding + "is currently not supported.");
        }
    }

}
