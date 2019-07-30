package de.tudresden.inf.mci.brailleplot.printerbackend;


import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.Doc;

import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import java.util.Objects;
/**
 * Implements a variation of the GoF Design pattern Builder. This class is used for setting the printerconfiguration and
 * for printing.
 * @author Andrey Ruzhanskiy
 * @version 17.07.2019
 */
public class PrintDirector {

    private AbstractDocumentBuilder mBuilder;
    private final PrinterCapability mPrinter;
    private PrintService mService;
    private String mPrinterName;
    private DocFlavor mDocflavor;
    public DocPrintJob mPrintJob;


    /**
     * Constructor for the PrintDirector. Main Class for printing. The class takes care of the complex Protocol to build
     * the document for the given configuration.
     * @param printerCap Which printerconfiguration should be used. Normalprinter assumes that no special Features like
     *                GraphicMode or FloatindDotArea will be used.
     * @param printerConfig The Printer object, used for extracting the name of the printer.
     */
    public PrintDirector(final PrinterCapability printerCap, final Printer printerConfig) {
        Objects.requireNonNull(printerCap);
        Objects.requireNonNull(printerConfig);
        this.mPrinter = printerCap;
        mPrinterName = printerConfig.getProperty("name").toString();
        switch (mPrinter) {
            case NORMALPRINTER:
                mBuilder = new NormalBuilder(); break;
            case INDEX_EVEREST_D_V4_GRAPHIC_PRINTER:
                mBuilder = new GraphicPrintBuilder();
                break;
            case INDEX_EVEREST_D_V4_FLOATINGDOT_PRINTER:
                mBuilder = new FloatingDotAreaBuilder();
                break;
            default: throw new IllegalArgumentException();
        }

    }

    /**
     * Public method for printing the given Document with the given data.
     * @param data Data to be printed.
     */

    /*
        Needed if someone tries to use a normalbuilder with something that is not a boolean.

     */
    @SuppressWarnings("unchecked")
    public <T> void print(final MatrixData<T> data)  {
        Objects.requireNonNull(data);
        setUpDoc();
        setUpService();
        byte[] result;
        try {
            result = mBuilder.assemble(data);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        print(result);
    }

    /**
     * Method for setting up the DocFlavor for printing. Currently, not parameterised because the printer can
     * (hopefully) understand raw bytes with an octet stream.
     */
    private void setUpDoc() {
        mDocflavor = new DocFlavor("application/octet-stream", "[B");
    }


    /**
     * Method for setting the correct printer Service for the Printername.
     * @throws RuntimeException if the System cant find the service.
     */

    private void setUpService() {
        Objects.requireNonNull(mPrinterName);
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService service: services) {
            if (service.getName().equals(mPrinterName) || mPrinterName.equals("Dummy Printer")) {
                mService = service;
                return;
            }
        }
        throw new RuntimeException("Cant register Printerservice for the printername : " + mPrinterName);
    }

    /**
     * Private Method for sending the data to the printer. Separated from the public method so that the assemble process
     * and the printing process are separated logically, but from outside it looks like it all happens in one method.
     * @param data to be printed.
     * @throws PrintException If the printingjob could not be completed.
     */

    private void print(final byte[] data) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(mService);
        Objects.requireNonNull(mDocflavor);
        Doc doc = new SimpleDoc(data, mDocflavor, null);
        PrintRequestAttributeSet asset = new HashPrintRequestAttributeSet();
        DocPrintJob job = mService.createPrintJob();
        asset.add(new JobName("Braille Printing", null));
        try {
            job.print(doc, asset);
            mPrintJob = job;
        } catch (PrintException pe) {
            throw new RuntimeException(pe);
        }

    }

    public static boolean isPrintServiceOn() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        if (services.length == 0) {
            return false;
        }
        return true;
    }
}
