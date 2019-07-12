package de.tudresden.inf.mci.brailleplot.exporter;
import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printabledata.SimpleMatrixDataImpl;

/**
 * This Class provides an Extension Point for further implementation
 * and Protocol Building for Documents that need to be send to the printer.
 * The common Interface is the getDocument() and assemble method.
 * Its usable for all Braille printers.
 * @author Andrey Ruzhanskiy
 * @version 28.05.2019
 */

public abstract class AbstractDocumentBuilder {
    /**
     * MemberVariable for the final Document. Readable via getDocument
     */

    protected byte[] mDocument;

    private MatrixData data;


    AbstractBrailleTableParser mParser;

    /**
     * Complex method for complex construction of an Document for the printer.
     * @param data Raw Data to be printed without any escapesequences
     * @return Fully build Document as byte[]
     */
    public byte[] assemble(final MatrixData data) {
        return null;
    }

    /**
     * Interface for getting the final Document.
     * @return Document to be printed
     */
    public byte[] getDocument() {
        return mDocument;
    }

    protected void setParser() throws NotSupportedFileExtension {
        //read brailletablepath
        Printer printer = data.getPrinterConfig();
        String brailleTablePath = printer.getProperty("brailletable").toString();

        //read which kind of parser is needed (properties, json, xml,...)
        String fileEnding = brailleTablePath.split("\\.")[1];
        switch (fileEnding) {
            case "properties": mParser = new PropertiesParser();
            case "json": mParser = new JsonParser();
            case "xml": mParser = new XmlParser();
            default: throw new NotSupportedFileExtension();
        }


    }

}
