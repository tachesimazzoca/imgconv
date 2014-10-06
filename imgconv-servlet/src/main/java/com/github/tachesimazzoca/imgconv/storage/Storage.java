package com.github.tachesimazzoca.imgconv.storage;

import java.io.InputStream;
import java.io.IOException;

import com.google.common.base.Optional;

/**
 * Instances of classes that implement this interface are used to manage a cache
 * storage.
 */
public interface Storage {
    /**
     * Returns an {@code Optional} instance containing the cache as input stream
     * read by the given key. If the last-modified time stamp of the cache is
     * older than the parameter {@code lastModified}, it will delete the cache
     * and return an {@code Optional.absent()}.
     * 
     * @param key The key of the cache
     * @param lastModified The last-modified time stamp to delete expired
     *        caches.
     * @return An {@code Optional} instance containing the cache
     * @throws IOException
     */
    public Optional<InputStream> read(String key, long lastModified) throws IOException;

    /**
     * Returns an {@code Optional} instance containing the cache as input stream
     * read by the given key.
     * 
     * @param key The key of the cache caches.
     * @return An {@code Optional} instance containing the cache
     * @throws IOException
     */
    public Optional<InputStream> read(String key) throws IOException;

    /**
     * Associates the input stream with the specified key in this storage.
     * 
     * @param key The key with which the specified input stream is to be
     *        associated
     * @param input The input stream to be associated with the specified key
     * @throws IOException
     */
    public void write(String key, InputStream input) throws IOException;

    /**
     * Removes the mapping for a key if it's present. This is an optional
     * operation, so any exceptions will never be thrown.
     * 
     * @param key The key whose mapping is to be removed from the storage
     */
    public void delete(String key);
}
