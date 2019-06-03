package de.tudresden.inf.mci.brailleplot.CommandLine;

import java.util.Optional;

/**
 * Supplies an interface for reading settings.
 * @author Georg Graßnick
 * @version 2019.06.01
 */
public interface SettingsReader {

    Optional<String> getSetting(SettingType setting);
    boolean isPresent(SettingType setting);
    Optional<Boolean> isTrue(SettingType setting);
}
