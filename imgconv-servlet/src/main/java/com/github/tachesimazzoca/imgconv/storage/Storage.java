package com.github.tachesimazzoca.imgconv.storage;

import java.io.InputStream;
import java.io.IOException;

import com.google.common.base.Optional;

public interface Storage {
    public Optional<InputStream> read(String key, long lastModified) throws IOException;

    public Optional<InputStream> read(String key) throws IOException;

    public void write(String key, InputStream input) throws IOException;

    public void delete(String key);
}
