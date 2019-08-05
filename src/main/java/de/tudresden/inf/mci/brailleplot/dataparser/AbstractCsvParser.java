package de.tudresden.inf.mci.brailleplot.dataparser;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Abstract parent class of all CSV parser classes.
 * Provides generic methods for all inheriting parser classes.
 * @param <T> The type of the {@link CsvElement}, that is parsed in this class.
 * @author Georg Graßnick
 * @version 2019.08.02
 */
public abstract class AbstractCsvParser<T extends CsvElement> {

    protected Configuration mConfig;
    protected Logger mLogger = LoggerFactory.getLogger(getClass());
    public static final Charset CHARACTER_ENCODING = StandardCharsets.UTF_8;

    protected AbstractCsvParser(final Configuration config) {
        mConfig = Objects.requireNonNull(config);
    }

    protected final List<T> genericParse(final InputStream inputStream, final Class<T> csvElementClass) {
        Objects.requireNonNull(inputStream);

        Reader reader = new InputStreamReader(inputStream, CHARACTER_ENCODING);
        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                .withSeparator(mConfig.separator)
                .withQuoteChar(mConfig.quotation)
                .withEscapeChar(mConfig.escape)
                .withSkipLines(mConfig.skipLines)
                .withType(csvElementClass)
                .build();

        List<T> csvElements = csvToBean.parse();

    return csvElements;
    }

    /**
     * Struct like class encapsulating the properties for the parser.
     */
    public static class Configuration {
        public char separator = ',';
        public char quotation = '\"';
        public char escape = '\\';
        protected int skipLines = 0;
    }
}
