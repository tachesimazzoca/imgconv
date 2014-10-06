package com.github.tachesimazzoca.imgconv.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import com.google.common.base.Optional;

import org.apache.commons.io.IOUtils;

/**
 * A storage based on OS file system.
 */
public class FileStorage implements Storage {
    private File baseDirectory;

    /**
     * Creates a new {@code FileStorage} object with the specified path to the
     * cache directory.
     * 
     * @param baseDirectory
     */
    public FileStorage(File baseDirectory) {
        if (baseDirectory == null)
            throw new NullPointerException(
                    "The parameter baseDirectory must be not null.");
        if (baseDirectory.exists() && !baseDirectory.isDirectory())
            throw new IllegalArgumentException(
                    "The parameter baseDirectory must be a directory.");
        this.baseDirectory = baseDirectory;
    }

    @Override
    public Optional<InputStream> read(String key, long lastModified) throws IOException {
        if (!baseDirectory.exists())
            return Optional.absent();

        File f = new File(this.baseDirectory, key);
        if (!f.exists() || !f.isFile())
            return Optional.absent();
        if (f.lastModified() < lastModified) {
            delete(key);
            return Optional.absent();
        }

        return Optional.of((InputStream) new FileInputStream(f));
    }

    @Override
    public Optional<InputStream> read(String key) throws IOException {
        return read(key, -1);
    }

    @Override
    public void write(String key, InputStream input) throws IOException {
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }
        File f = new File(this.baseDirectory, key);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            IOUtils.copyLarge(input, out);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    @Override
    public void delete(String key) {
        if (!baseDirectory.exists())
            return;
        File f = new File(this.baseDirectory, key);
        if (!f.exists() || !f.isFile())
            return;
        f.delete();
    }
}
