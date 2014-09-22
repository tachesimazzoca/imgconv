package com.github.tachesimazzoca.imgconv.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.google.common.base.Optional;

import static org.apache.commons.io.IOUtils.copyLarge;
import static org.apache.commons.io.IOUtils.closeQuietly;

public class MockStorage implements Storage {
    private byte[] bytes;

    public MockStorage(byte[] bytes) {
        this.bytes = bytes;
    }

    public Optional<InputStream> read(String key) {
        if (bytes == null)
            return Optional.absent();
        else
            return Optional.<InputStream> of(new ByteArrayInputStream(bytes));
    }

    public void write(String key, InputStream input) {
        bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            copyLarge(input, baos);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(baos);
            closeQuietly(input);
        }
    }

    public void delete(String key) {
        bytes = null;
    }
}
