package de.tudresden.inf.mci.brailleplot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is used to use files & resources independent from the application running from packed jar or not.
 * @author Leonard Kupper
 * @version 2019-09-10
 */
public final class GeneralResource {

    private String mResourcePath;
    private boolean validExternalFile = false;
    private boolean validPackedResource = false;
    Logger mLogger = LoggerFactory.getLogger(this.getClass());

    /**
     * Create a resource from a file path or resource classpath.
     * @param resourcePath Relative or local path to a file (e.g. C:\example.txt) in the filesystem or classpath pointing to (packed) resource location (e.g. /config/default.properties)
     * @throws IOException If the given path neither determines a valid external file, nor a valid resource.
     */
    public GeneralResource(final String resourcePath) throws IOException {
        this(resourcePath, null);
    }

    /**
     * Create a resource from a file path or resource classpath.
     * @param resourcePath Relative or local path to a file (e.g. C:\example.txt) in the filesystem or classpath pointing to (packed) resource location (e.g. /config/default.properties)
     * @param searchPath Relative or local path to be used as base for interpreting the resourcePath relatively to additionally.
     * @throws IOException If the given path neither determines a valid external file, nor a valid resource.
     */
    public GeneralResource(final String resourcePath, final String searchPath) throws IOException {
        File checkFile = new File(resourcePath);
        mLogger.info("checking referenced path: " + checkFile);
        if (checkFile.isFile()) {
            mLogger.info("interpreting path as file: " + checkFile.getCanonicalPath());
            mResourcePath = checkFile.getCanonicalPath();
            validExternalFile = true;
        }
        checkFile = checkFile.getAbsoluteFile();
        mLogger.info("checking referenced path as absolute path: " + checkFile);
        if (checkFile.isFile()) {
            mLogger.info("interpreting path as absolute file: " + checkFile.getCanonicalPath());
            mResourcePath = checkFile.getCanonicalPath();
            validExternalFile = true;
        }
        if (Objects.nonNull(searchPath)) {
            checkFile = new File(searchPath + File.separator + resourcePath);
            mLogger.info("looking for referenced path in search path: " + checkFile);
            if (checkFile.isFile()) {
                mLogger.info("interpreting path as search path relative file: " + checkFile.getCanonicalPath());
                mResourcePath = checkFile.getCanonicalPath();
                validExternalFile = true;
            }
        }
        String resourceClassPath = resourcePath.replace("\\", "/"); // classpaths are always separated by forward slash
        InputStream checkStream = getClass().getResourceAsStream(resourceClassPath);
        mLogger.info("checking referenced path as resource: " + resourceClassPath);
        if (Objects.nonNull(checkStream)) {
            mLogger.info("interpreting path as resource stream: " + resourceClassPath);
            mResourcePath = resourceClassPath;
            validPackedResource = true;
        }
        if (Objects.nonNull(searchPath)) {
            String relativeResourcePath = new File(searchPath + File.separator + resourceClassPath).toPath().normalize().toString();
            relativeResourcePath = relativeResourcePath.replace("\\", "/");
            checkStream = getClass().getResourceAsStream(relativeResourcePath);
            mLogger.info("checking referenced path as search path relative resource: " + relativeResourcePath);
            if (Objects.nonNull(checkStream)) {
                mLogger.info("interpreting path as resource stream: " + relativeResourcePath);
                mResourcePath = relativeResourcePath;
                validPackedResource = true;
            }
        }
        if (!(isValidExternalFile() || isValidPackedResource())) {
            throw new FileNotFoundException("Not recognized as valid file or resource: " + resourcePath);
        }
    }

    public boolean isValidExternalFile() {
        return validExternalFile;
    }

    public boolean isValidPackedResource() {
        return validPackedResource;
    }

    public InputStream asInputStream() {
        try {
            if (isValidExternalFile()) {
                return new FileInputStream(getResourcePath());
            }
            return getClass().getResourceAsStream(mResourcePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while opening resource as stream: ", e);
        }
    }

    public File asValidExternalFile() {
        if (!isValidExternalFile()) {
            throw new IllegalArgumentException("Not a valid external file: " + getResourcePath());
        }
        return new File(getResourcePath());
    }

    public String getResourcePath() {
        return mResourcePath;
    }

    public File getFileOrExportResource() {
        if (!isValidPackedResource()) {
            return asValidExternalFile();
        } else {
            try {
                File tmpOut = File.createTempFile("resource_" + UUID.randomUUID(), "tmp");
                byte[] content = asInputStream().readAllBytes();
                FileOutputStream outputStream = new FileOutputStream(tmpOut);
                outputStream.write(content);
                outputStream.close();
                return tmpOut;
            } catch (IOException e) {
                throw new RuntimeException("Error while exporting resource: ", e);
            }
        }
    }

}
