package de.tudresden.inf.mci.brailleplot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * This class offers static methods for native library loading purposes.
 * @author Georg Graßnick
 * @version 2019.09.26
 */
public final class NativeLibraryHelper {

    private static final Logger LOG = LoggerFactory.getLogger(NativeLibraryHelper.class);
    private static final String LIB_PATH = calculateLibPath();

    private NativeLibraryHelper() { }

    /**
     * Loads a library from the resources according to the current system architecture and operating system.
     * @param libName The name of the library. Library prefixes ({@literal "}lib{@literal "}) are not treated separately.
     *                The system specific file ending is added automatically and must not be included in the parameter.
     * @return A File object representing the file of the library. The JNI is automatically set up to take of the library,
     * so there is most likely no need for this return value.
     * @throws NoSuchNativeLibraryException If the requested library could not be found or read.
     */
    public static synchronized File loadNativeLibrary(final String libName) throws NoSuchNativeLibraryException {
        File libFile;
        try {
            String libPath = "native/" + libName + "/" + LIB_PATH + "/" + libName + dynamicLibFileEnding();
            libFile = GeneralResource.getOrExportResourceFile(libPath).getAbsoluteFile();
            registerNewSystemLibPath(libFile.getParent());
        } catch (Exception e) {
            throw new NoSuchNativeLibraryException("Could not provide native library from java resources", e);
        }
        LOG.debug("Found and exported native library \"" + libFile + "\" for requested library \"" + libName + "\"");
        return libFile;
    }

    /**
     * Adds a path in the file system to the search path of the JNI.
     * @param path The path to add.
     */
    private static synchronized void registerNewSystemLibPath(final String path) {
        // TODO Check if path is already included and abort, if so
        String currentLibPath = System.getProperty("jna.library.path");
        String newLibPath = null;
        if (currentLibPath == null) {
            newLibPath = path;
        } else {
            newLibPath = currentLibPath + File.pathSeparator + path;
        }
        LOG.debug("Setting JNI library path property to \"" + newLibPath + "\"");
        System.setProperty("jna.library.path", newLibPath);
    }

    private static String calculateLibPath() {
        return getArch() + "/" + getOs();
    }

    private static String getArch() {
        String arch = System.getProperty("os.arch");
        // reference: https://stackoverflow.com/a/36926327
        switch (arch) {
            case "x86":
            case "i386":
            case "i486":
            case "i586":
            case "i686":
                return "x86_32";
            case "x86_64":
            case "amd64":
                return "x86_64";
            default:
                throw new RuntimeException("Operating System architecture \"" + arch + "\" is currently not supported");
        }
    }

    private static String getOs() {
        String name = System.getProperty("os.name");
        String nameLow = name.toLowerCase();
        if (nameLow.contains("linux")) {
            return "linux";
        } else if (nameLow.contains("mac")) {
            return "osx";
        } else if (nameLow.contains("win")) {
            return "win32";
        } else {
            throw new RuntimeException("Operating System \"" + name + "\" is currently not supported");
        }
    }

    private static String dynamicLibFileEnding() {
        switch (getOs()) {
            case "win32":
                return ".dll";
            case "linux":
                return ".so";
            case "osx":
                return ".dynlib";
            default:
                throw new IllegalStateException("If this exception was thrown, something is wrong with your code");
        }
    }
}
