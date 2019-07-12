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


        Iterator<BrailleCell6<Boolean>> iter = data.getBrailleCell6Iterator();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        mParser = new PropertiesParser(data.getPrinterConfig().getProperty("brailletable").toString());

    //    data.getFormatConfig().getProperty()
        int width = data.getColumnCount() / 2;
        int i = 0;
        while (iter.hasNext()) {
            stream.write(mParser.getValue(iter.next().toShortString()));
            i++;
            if (i == width) {
                i = 0;
                stream.write(0x0D);
                stream.write(0x0A);
            }


        }


        return stream.toByteArray();
    }

    protected NormalBuilder(){

    }

}
