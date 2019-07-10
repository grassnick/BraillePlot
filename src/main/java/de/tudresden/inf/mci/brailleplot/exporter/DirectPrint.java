package de.tudresden.inf.mci.brailleplot.exporter;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.ByteArrayOutputStream;


/**
 * This Class provides means to print. But poorly currently. Big TODO.
 * Deprecated, currently the PrintDirector should be used.
 * @author Andrey Ruzhanskiy, Leonard Kupper
 * @version 28.05.2019
 */

public class DirectPrint {

    private byte[] mData;

    public DirectPrint() {

    }

    /**
     * Method for printing the Document, represented as byte[].
     * @param input The Document, represented as byte[], that will be printed
     *
     */

    public void printString(final byte[] input) {
        this.mData = input;
        DocFlavor flavor = new DocFlavor("application/octet-stream", "[B");
        Doc braille = new SimpleDoc(this.mData, flavor, null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        /*aset.add(new PageRanges(1, 1));
        aset.add(new Copies(1));
        */
        //new copy paste
        PrintService services =
                PrintServiceLookup.lookupDefaultPrintService();
        //System.out.println(services.toString());
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

    public void prettyPrintCLI(final byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());

    }

    /**
     * Method for debug and printing purposes.
     * Prints and returns a String containing the supported Document Flavors of the default PrinterConfiguration
     * @return String with supported Document Flavor
     */
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

    /**
     * For Demo Purposes.
     * @param d Values from 1 to 3 whereas:
     *          1.. Diagram
     *          2.. Circle
     *          3.. BetterData
     * @return byte[] containing the bytes to be sent to the printer.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public byte[] buildDemo(final int d) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        byte[] diagramData = ("'   l\n'   l              .i\n'  :r            *a\n'   l          .i\n' ::r::      *a\n'   l\n'  :r\n'   l\n' ::r: +::;\n'   l  l  l\n'  :r  r::l\n'   l  l  l\n' ::r: r::l  +:::::;\n'   l  l  l  loooool    g\n'  :r  r::l  r:::::l\n'   l  l  l  loooool\n' ::r: r::l  r:::::l  g\n'   l  l  l  r:::::l\n'  :r  r::l  r:::::l\n'   l  l  l  l     l      .iccce'\n' ::r: r::l  r:::::l    .i\n'   l  l  l  l     l  .i  ;     l\n'  :r::r::r::r::r::r::r::r::r::r::r\n'   b     b     b     b     b     l\n'   '     '     '     '     '     '").getBytes();
        byte[] circleData = ("30.00:60.00\n31.90:59.90\n33.80:59.80\n35.60:59.50\n37.50:59.10\n39.30:58.50\n41.00:57.90\n42.80:57.10\n44.50:56.30\n46.10:55.30\n47.60:54.30\n49.10:53.10\n50.50:51.90\n51.90:50.50\n53.10:49.10\n54.30:47.60\n55.30:46.10\n56.30:44.50\n57.10:42.80\n57.90:41.00\n58.50:39.30\n59.10:37.50\n59.50:35.60\n59.80:33.80\n59.90:31.90\n60.00:30.00\n59.90:28.10\n59.80:26.20\n59.50:24.40\n59.10:22.50\n58.50:20.70\n57.90:19.00\n57.10:17.20\n56.30:15.50\n55.30:13.90\n54.30:12.40\n53.10:10.90\n51.90:9.50\n50.50:8.10\n49.10:6.90\n47.60:5.70\n46.10:4.70\n44.50:3.70\n42.80:2.90\n41.00:2.10\n39.30:1.50\n37.50:0.90\n35.60:0.50\n33.80:0.20\n31.90:0.10\n30.00:0.00\n28.10:0.10\n26.20:0.20\n24.40:0.50\n22.50:0.90\n20.70:1.50\n19.00:2.10\n17.20:2.90\n15.50:3.70\n13.90:4.70\n12.40:5.70\n10.90:6.90\n9.50:8.10\n8.10:9.50\n6.90:10.90\n5.70:12.40\n4.70:13.90\n3.70:15.50\n2.90:17.20\n2.10:19.00\n1.50:20.70\n0.90:22.50\n0.50:24.40\n0.20:26.20\n0.10:28.10\n0.00:30.00\n0.10:31.90\n0.20:33.80\n0.50:35.60\n0.90:37.50\n1.50:39.30\n2.10:41.00\n2.90:42.80\n3.70:44.50\n4.70:46.10\n5.70:47.60\n6.90:49.10\n8.10:50.50\n9.50:51.90\n10.90:53.10\n12.40:54.30\n13.90:55.30\n15.50:56.30\n17.20:57.10\n19.00:57.90\n20.70:58.50\n22.50:59.10\n24.40:59.50\n26.20:59.80\n28.10:59.90").getBytes();
        FloatingDotAreaBuilder fdaExample = new FloatingDotAreaBuilder(circleData, 5, 3, 6, -1);
        byte[] betterData = fdaExample.getDocument();
        switch (d) {
            case 1: return diagramData;
            case 2: return circleData;
            case 3: return betterData;
            default: return null;
        }
    }
}
