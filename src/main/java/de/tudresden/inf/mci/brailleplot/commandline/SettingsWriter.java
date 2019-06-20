package de.tudresden.inf.mci.brailleplot.commandline;

/**
 * Implementing classes are guaranteed to support manipulating settings.
 * Currently, the implementation is not thread safe!
 * @author Georg Graßnick
 * @version 2019.06.01
 */
public interface SettingsWriter extends SettingsReader {

    void setSetting(SettingType setting, String value);
    void setSetting(SettingType setting, Boolean isTrue);
    void deleteSettingValue(SettingType setting);
}
