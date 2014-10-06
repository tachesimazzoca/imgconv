package com.github.tachesimazzoca.imgconv.fetcher;

import java.io.InputStream;
import java.io.IOException;

import com.google.common.base.Optional;

/**
 * Instances of classes that implement this interface are used to fetch any
 * resources.
 */
public interface Fetcher {
    /**
     * Returns an {@code Optional} instance containing the resource as input
     * stream fetched by the relative URL.
     * 
     * @param path The relative URL to the resource
     * @return An {@code Optional} instance containing the resource
     */
    public Optional<InputStream> fetch(String path) throws IOException;
}
