package de.tudresden.inf.mci.brailleplot.commandline;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Encapsulates Settings for the Application from the command line.
 * @author Georg Graßnick
 * @version 2019.05.31
 */
public final class Settings implements SettingsReader, SettingsWriter {

    private Map<SettingType, String> mSettings;

    private final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    private static final String BOOL_TRUE = Boolean.toString(true);
    private static final String BOOL_FALSE = Boolean.toString(false);

    /**
     * Constructor.
     * @param cmdLine CommandLine object to take information from.
     */
    Settings(final CommandLine cmdLine) {
        mSettings = new HashMap<>();
        setup(cmdLine);
    }


    /**
     * Populates the Settings class with arguments from the command line.
     * @param cmdLine {@Link CommandLine} object to take take settings from.
     */
    private void setup(final CommandLine cmdLine) {
        for (Iterator<Option> it = cmdLine.iterator(); it.hasNext();) {
            Option o = it.next();
            if (o.hasArg()) {
                SettingType type = SettingType.fromString(o.getLongOpt());
                mSettings.put(type, o.getValue());
                mLogger.trace("Added string \"{}\" with value \"{}\"", type, o.getValue());
            } else {
                mSettings.put(SettingType.fromString(o.getLongOpt()), BOOL_TRUE);
                mLogger.trace("Added flag \"{}\" set to \"{}\"", SettingType.fromString(o.getLongOpt()), BOOL_TRUE);
            }
        }
    }

    /**
     * Check if a specific setting is set.
     * @param setting The requested setting.
     * @return true if the parameter is set, else false.
     */
    public boolean isPresent(final SettingType setting) {
        return mSettings.containsKey(setting);
    }

    /**
     * Get the value of a setting, if set.
     * @param setting The requested Setting to request the value for.
     * @return {@link Optional}{@literal <String>} if setting is set, or {@link Optional#empty()} if the requested setting
     * was not specified by the user.
     * Use {@link Settings#isPresent(SettingType)} to check the value of a boolean parameter.
     */
    public Optional<String> getSetting(final SettingType setting) {
        if (!mSettings.containsKey(setting)) {
            return Optional.empty();
        }
        return Optional.of(mSettings.get(setting));
    }

    /**
     * Get the boolean value of a setting, if set.
     * @param setting The requested Setting to request the value for.
     * @return {@link Optional}{@literal <Boolean>} if setting is set, or {@link Optional#empty()} if the requested setting
     * was not specified by the user.
     */
    public Optional<Boolean> isTrue(final SettingType setting) {
        if (!mSettings.containsKey(setting)) {
            return Optional.empty();
        }

        if (mSettings.get(setting).equals(BOOL_TRUE)) {
            return Optional.of(true);
        } else {
            return Optional.of(false);
        }
    }

    /**
     * Set the value of a setting.
     * @param setting {@Link SettingType} the setting to be changed.
     * @param value {@Link String} The value to set.
     */
    public void setSetting(final SettingType setting, final String value) {
        mSettings.replace(setting, value);
    }

    /**
     * Set the value of a setting to a specific {@Link Boolean}.
     * @param setting {@Link SettingType} the setting to be changed.
     * @param isTrue {@Link String} The value to set.
     */
    public void setSetting(final SettingType setting, final Boolean isTrue) {
        if (isTrue) {
            mSettings.replace(setting, BOOL_TRUE);
        } else {
            mSettings.replace(setting, BOOL_FALSE);
        }
    }

    /**
     * Delete the set value of a setting, if it is set.
     * @param setting {@Link SettingType} the setting to be removed.
     */
    public void deleteSettingValue(final SettingType setting) {
        if (mSettings.containsKey(setting)) {
            mSettings.remove(setting);
        }
    }

}
