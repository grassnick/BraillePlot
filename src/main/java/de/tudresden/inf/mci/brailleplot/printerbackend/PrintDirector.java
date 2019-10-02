package de.tudresden.inf.mci.brailleplot.printerbackend;


import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PrinterState;
import javax.print.event.PrintJobEvent;
import java.util.Objects;
/**
 * Implements a variation of the GoF design pattern Builder. This class is used for setting the printer configuration and
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
    private final Logger mLogger = LoggerFactory.getLogger(PrintDirector.class);
    private DocPrintJob mPrintJob;


    /**
     * Constructor for the PrintDirector. Main class for printing. The class takes care of the complex Protocol to build
     * the document for the given configuration.
     * @param printerCap Which {@link PrinterCapability} should be used. Normal printer assumes that no special features like
     *                GraphicMode or FloatindDotArea will be used.
     * @param printerConfig The {@link Printer} object, used for extracting the name of the printer.
     */
    public PrintDirector(final PrinterCapability printerCap, final Printer printerConfig) {
        Objects.requireNonNull(printerCap);
        Objects.requireNonNull(printerConfig);
        this.mPrinter = printerCap;
        mPrinterName = printerConfig.getProperty("name").toString();
        mLogger.trace("Using following printercapability {}", printerCap.toString(), " loaded");
        mLogger.info("Using the following printer: {}", mPrinterName);
        switch (mPrinter) {
            case NORMALPRINTER:
                mBuilder = new NormalBuilder();
                mLogger.trace("Using NormalBuilder as protocol");
                break;
            case INDEX_EVEREST_D_V4_GRAPHIC_PRINTER:
                mBuilder = new GraphicPrintBuilder();
                mLogger.trace("Using Index Everest-D V4 graphic print as protocol");
                break;
            case INDEX_EVEREST_D_V4_FLOATINGDOT_PRINTER:
                mBuilder = new FloatingDotAreaBuilder();
                mLogger.trace("Using Index Everest-D V4 floatingdot as protocol");
                break;
            default: throw new IllegalArgumentException();
        }

    }

    /**
     * Public method for printing the given document with the given data.
     * @param data {@link de.tudresden.inf.mci.brailleplot.printabledata.MatrixData} to be printed.
     */


    // Needed if someone tries to use a normal builder with something that is not a boolean.

    @SuppressWarnings("unchecked")
    public void print(final PrintableData data)  {
        mLogger.info("Starting with print process");
        Objects.requireNonNull(data);
        mLogger.info("Setting up docflavour and service");
        setUpDoc();
        setUpService();
        byte[] result;
        mLogger.info("Finished setting up doc and service");
        try {
            mLogger.trace("Assembling the data according to protocol: {}", mBuilder.getClass().getCanonicalName());
            result = mBuilder.assemble(data);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        mLogger.info("Finished assembling data");
        print(result);
    }

    /**
     * Method for creating the byte array form the printing process.
     * @param data Data to be later dumped.
     * @return Byte array containing the sequence for the printer.
     */
    @SuppressWarnings("unchecked")
    public byte[] byteDump(final PrintableData data) {
        mLogger.info("Starting with textdump process");
        byte[] result;
        try {
            mLogger.trace("Assembling the data according to protocol: {}", mBuilder.getClass().getCanonicalName());
            result = mBuilder.assemble(data);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        mLogger.info("Finished with creating textdump");
        return result;
    }
    /**
     * Method for setting up the DocFlavor for printing. Currently, not parameterised because the printer can
     * (hopefully) understand raw bytes with an octet stream.
     */
    private void setUpDoc() {
        mDocflavor = new DocFlavor("application/octet-stream", "[B");
    }


    /**
     * Method for setting the correct printer service for the printer name.
     * @throws RuntimeException If the system cant find the service.
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
     * Private method for sending the data to the printer. Separated from the public method so that the assemble process
     * and the printing process are separated logically, but from outside it looks like it all happens in one method.
     * @param data Data to be printed.
     */

    private void print(final byte[] data) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(mService);
        Objects.requireNonNull(mDocflavor);
        mLogger.info("Setting up doc, asset and job");
        Doc doc = new SimpleDoc(data, mDocflavor, null);
        PrintRequestAttributeSet asset = new HashPrintRequestAttributeSet();
        DocPrintJob job = mService.createPrintJob();
        mLogger.trace("Finished setting up doc, asset and job");
        PrinterState state;
        asset.add(new JobName("Braille Printing", null));
        try {
            mLogger.trace("Adding job to the PrintJobListener");
            PrintJobListener listener = new PrintJobListener();
            job.addPrintJobListener(listener);
            mLogger.trace("Starting printing");
            //PrinterIsAcceptingJobs set = mService.getAttribute(PrinterIsAcceptingJobs.class);
            //PrinterStateReasons reasons = mService.getAttribute(PrinterStateReasons.class);
            job.print(doc, asset);
            //set = mService.getAttribute(PrinterIsAcceptingJobs.class);
            //reasons = mService.getAttribute(PrinterStateReasons.class);
            listener.waitForDone();
            mPrintJob = job;
        } catch (PrintException pe) {
            throw new RuntimeException(pe);
        }

    }

    /**
     * Static method to verify if the print service is activated on the system.
     * @return true, if activated, false if not.
     */
    public static boolean isPrintServiceOn() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        if (services.length == 0) {
            return false;
        }
        return true;
    }

    /**
     * Eventlistener which receives updates regarding printing.
     * Because of enormous shortcoming in the implementation of printing in java, some events are never received.
     */
    private class PrintJobListener implements javax.print.event.PrintJobListener {
        boolean done = false;

        @Override
        public void printDataTransferCompleted(final PrintJobEvent pje) {
            mLogger.info("Data transfer to printer complete");
        }

        @Override
        public void printJobCompleted(final PrintJobEvent pje) {
            mLogger.info("Printjob completed");
            synchronized (PrintJobListener.this) {
                done = true;
                PrintJobListener.this.notify();
            }
        }

        @Override
        public void printJobFailed(final PrintJobEvent pje) {
            mLogger.info("Printjob failed");
            synchronized (PrintJobListener.this) {
                done = true;
                PrintJobListener.this.notify();
            }
        }

        @Override
        public void printJobCanceled(final PrintJobEvent pje) {
            mLogger.info("Printjob was canceled");
            synchronized (PrintJobListener.this) {
                done = true;
                PrintJobListener.this.notify();
            }
        }

        @Override
        public void printJobNoMoreEvents(final PrintJobEvent pje) {
            mLogger.info("Printjob has no more events");
            synchronized (PrintJobListener.this) {
                done = true;
            }
        }

        @Override
        public void printJobRequiresAttention(final PrintJobEvent pje) {
            mLogger.info("Printjob requires attention");
            PrintJobListener.this.notify();
        }
        public synchronized void waitForDone() {
            try {
                // Not busy waiting, sleeping as long as not notified.
                while (!done) {
                    wait();
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

}
