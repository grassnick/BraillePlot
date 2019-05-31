package de.tudresden.inf.mci.brailleplot;

public class FormatPrint extends AbstractIndexV4Printer {

    /**
     * Complex method for complex construction of an Document for the printer.
     *
     * @param data Raw Data to be printed without any escapesequences
     * @return Fully build Document as byte[]
     */
    @Override
    public byte[] assemble(byte[] data) {
        return super.assemble(data);
    }

    /**
     * Interface for getting the final Document.
     *
     * @return DOcument to be printed
     */
    @Override
    public byte[] getDocument() {
        return super.getDocument();
    }
}
