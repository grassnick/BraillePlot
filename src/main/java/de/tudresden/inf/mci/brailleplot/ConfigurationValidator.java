package de.tudresden.inf.mci.brailleplot;

/**
 * ConfigurationValidator.
 * @author Leonard Kupper
 * @version 04.06.19
 */
public interface ConfigurationValidator {
    ValidProperty validate(String key, String value);
}
