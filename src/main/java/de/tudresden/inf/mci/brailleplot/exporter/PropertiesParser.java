package de.tudresden.inf.mci.brailleplot.exporter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Properties;


public class PropertiesParser implements AbstractBrailleTableParser {
    private Properties mProperties = new Properties();

    PropertiesParser(final String path){
        FileInputStream stream;
        try {
            stream = new FileInputStream(path);
            mProperties.load(stream);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties getProperties(){
        if (Objects.isNull(mProperties)){
            throw new NullPointerException();
        }
        if (mProperties.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return mProperties;
    }

    //BIG TODO explain or as leo. My fucking head hurts....
    //BIG TODO add null check(contains)

    @Override
    public int getValue(String bitString) {
        return Integer.parseInt(mProperties.getProperty(bitString));
    }
}
