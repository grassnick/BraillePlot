package de.tudresden.inf.mci.brailleplot;


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

    /**
     * Complex method for complex construction of an Document for the printer.
     * @param data Raw Data to be printed without any escapesequences
     * @return Fully build Document as byte[]
     */
    public byte[] assemble(final byte[] data) {
        return null;
    }

    /**
     * Interface for getting the final Document.
     * @return Document to be printed
     */
    public byte[] getDocument() {
        return mDocument;
    }
}
