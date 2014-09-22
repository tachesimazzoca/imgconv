package com.github.tachesimazzoca.imgconv.storage;

import java.io.InputStream;

import com.google.common.base.Optional;

public interface Storage {
    public Optional<InputStream> read(String key);

    public void write(String key, InputStream input);

    public void delete(String key);
}
