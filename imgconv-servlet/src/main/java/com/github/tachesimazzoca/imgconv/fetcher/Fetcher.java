package com.github.tachesimazzoca.imgconv.fetcher;

import java.io.InputStream;
import java.io.IOException;

import com.google.common.base.Optional;

public interface Fetcher {
    public Optional<InputStream> fetch(String path) throws IOException;
}
