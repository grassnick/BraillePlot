package de.tudresden.inf.mci.brailleplot.exporter;


import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

/**
 * Implements a variation of the GoF Design pattern Builder. This class is used for setting the printerconfiguration and
 * for printing.
 * @author Andrey Ruzhanskiy
 */
public class PrintDirector {
    private AbstractDocumentBuilder mBuilder;
    private final PrinterConfiguration mPrinter;
    private PrintService mService;
    private String mPrinterName;
    private DocFlavor mDocflavor;


    /**
     * Constructor for the PrintDirector. Main Class for printing. The class takes care of the complex Protocol to build
     * the document for the given configuration.
     * @param printer Which printerconfiguration should be used. Normalprinter assumes that no special Features like
     *                GraphicMode or FloatindDotArea will be used.
     */

    public PrintDirector(final PrinterConfiguration printer) {
        this.mPrinter = printer;

        switch (mPrinter) {
            case NORMALPRINTER: mBuilder = new NormalBuilder();
            case INDEX_EVEREST_D_V4_GRAPHIC_PRINTER: mBuilder = new GraphicPrintBuilder();
            case INDEX_EVEREST_D_V4_FLOATINGDOT_PRINTER: mBuilder = new FloatingDotAreaBuilder();
            default: mBuilder = new NormalBuilder();
        }
    }

    /**
     * Static method for checking if the printer, which was given, exists in the Printer System Dialog.
     * @param printerName The name of the printer to check.
     * @return
     */

    public static boolean printerExists(final String printerName) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService service: services) {
            if (service.getName().equals(printerName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for setting the Printer.
     * @param printerName
     * @throws IllegalArgumentException if the printer is not found.
     */
    private void setPrinter(final String printerName) {
        if (printerExists(printerName)) {
            mPrinterName = printerName;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Stub.
     * @param printerName
     */

    @SuppressWarnings("checkstyle:MagicNumber")
    public <T> void print(final String printerName, final MatrixData<T> data)  {
        if (printerName == null || data == null){
            throw new NullPointerException();
        }
        setUpDoc();
        setPrinter(printerName);

        byte[] result = mBuilder.assemble(data);

        // Printing the Document

        print(result);
    }

    /**
     * Method for setting up the DocFlavor fir printing. Currently, not parameterised because the printer can (hopefully
     * understand raw bytes with an octet stream.
     */
    private void setUpDoc() {
        mDocflavor = new DocFlavor("application/octet-stream", "[B");
    }

    /**
     * Private Method for sendind the data to the printer.
     * @param data
     */

    private void print(final byte[] data) {
        if(data == null){
            throw new NullPointerException();
        }
        Doc doc = new SimpleDoc(data, mDocflavor, null);
        PrintRequestAttributeSet asset = new HashPrintRequestAttributeSet();
        DocPrintJob job = mService.createPrintJob();
        try {
            job.print(doc, asset);
        } catch (PrintException pe) {
            System.out.println(pe.getMessage());
        }

    }
}
