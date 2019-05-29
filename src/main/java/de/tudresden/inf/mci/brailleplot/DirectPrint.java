package de.tudresden.inf.mci.brailleplot;

import java.awt.print.PrinterJob;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PageRanges;
import java.io.ByteArrayOutputStream;


/**
 * This Class provides means to print. But poorly currently. Big TODO.
 * @author Andrey Ruzhanskiy
 * @version 28.05.2019
 */

public class DirectPrint {

    private PrintService[] printService;
    private byte[] data;

    public DirectPrint() {
        this.printService = PrinterJob.lookupPrintServices();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public static void main(final String[] args) {

        DirectPrint lt = new DirectPrint();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] betterData = new byte[] {
                0x1B, 0x09,
                0x1B, 0x0B, 0x04, 0x04, 0x04, 0x0F,
                0x1B, 0x5C, 0x03, 0x00, 0x49, (byte) 0x92, 0x24, 0x0D, 0x0A,
                0x1B, 0x5C, 0x03, 0x00, 0x49, (byte) 0x92, 0x24, 0x0D, 0x0A,
                0x1B, 0x5C, 0x03, 0x00, 0x09, (byte) 0x92, 0x20, 0x0D, 0x0A,
                0x1B, 0x5C, 0x02, 0x00, 0x01, 0x02, 0x0D, 0x0A,
                0x1B, 0x5C, 0x02, 0x00, 0x49, 0x02, 0x0D, 0x0A,
                0x1B, 0x0A
        };

        byte[] normalDoc = ("Hallo i bims 1 veraldedes gereht").getBytes();



        prettyPrintCLI(betterData);


        lt.printString(normalDoc);
        lookUpAcceptedTypes();
    }

    /**
     * Method for printing the Document, represented as byte[].
     * @param input The Document, represented as byte[], that will be printed
     *
     */

    public void printString(final byte[] input) {

        this.data = input;
        DocFlavor flavor = new DocFlavor("application/octet-stream", "[B");
        Doc braille = new SimpleDoc(this.data, flavor, null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(new PageRanges(1, 1));
        aset.add(new Copies(1));

        //new copy paste
        PrintService services =
                PrintServiceLookup.lookupDefaultPrintService();
        System.out.println(services.toString());
        DocFlavor[] array = services.getSupportedDocFlavors();


        DocPrintJob job = services.createPrintJob();
        try {
            job.print(braille, aset);
            } catch (PrintException pe) {
            System.out.println(pe.getMessage());
        }


    }

    /**
     * Method for printing the byte[] as a byte seperated String.
     * @param data Data to be printed on the Command Line
     */

    public static void prettyPrintCLI(final byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());

    }

    public static String lookUpAcceptedTypes() {
        PrintService services =
                PrintServiceLookup.lookupDefaultPrintService();
        StringBuilder result = new StringBuilder();
        DocFlavor[] doc = services.getSupportedDocFlavors();
        for (int i = 0; i < services.getSupportedDocFlavors().length; i++) {
            result.append(doc[i]);
            System.out.println(doc[i]);
        }

        return result.toString();
    }
}
