package de.tudresden.inf.mci.brailleplot.exporter;

import de.tudresden.inf.mci.brailleplot.configparser.Printer;
import de.tudresden.inf.mci.brailleplot.configparser.ValidProperty;
import de.tudresden.inf.mci.brailleplot.printabledata.BrailleCell6;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Properties;


/**
 * Class representing a normal Document (for example a .txt) to print without
 * any Escapesequences.
 * @author Andrey Ruzhanskiy
 * @version
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class NormalBuilder extends AbstractDocumentBuilder {

    @Override
    public byte[] assemble(final MatrixData data) {
        if (data == null) {
            throw new NullPointerException();
        }

        try {
            setParser();
        } catch (NotSupportedFileExtension e) {
            throw new RuntimeException();
        }




        Iterator<BrailleCell6<Boolean>> iter = data.getBrailleCell6Iterator();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();



    //    data.getFormatConfig().getProperty()
        while (iter.hasNext()) {
            stream.write(mParser.getValue(iter.next().toShortString()));
        }

        return stream.toByteArray();
    }

    protected NormalBuilder(){

    }

}
