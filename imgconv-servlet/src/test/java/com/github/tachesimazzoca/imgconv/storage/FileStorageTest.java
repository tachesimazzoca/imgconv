package com.github.tachesimazzoca.imgconv.storage;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import com.google.common.base.Optional;

import org.apache.commons.io.IOUtils;

public class FileStorageTest {
    @Rule
    public TemporaryFolder tmpdir = new TemporaryFolder();

    @Test(expected = java.lang.NullPointerException.class)
    public void testNullBaseDirectory() throws IOException {
        new FileStorage(null);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testInvalidBaseDirectory() throws IOException {
        new FileStorage(tmpdir.newFile());
    }

    @Test
    public void testRead() throws IOException {
        File dir = tmpdir.newFolder();
        Storage storage = new FileStorage(dir);
        Optional<InputStream> opt = storage.read("deadbeef.jpg");
        assertFalse(opt.isPresent());
    }

    @Test
    public void testWriteReadAndDelete() throws IOException {
        File dir = tmpdir.newFolder();
        Storage storage = new FileStorage(dir);
        String path = "a.txt";
        String expected = "testWriteReadAndDelete" + System.currentTimeMillis();

        ByteArrayInputStream input = new ByteArrayInputStream(expected.getBytes());
        try {
            storage.write(path, input);
        } finally {
            IOUtils.closeQuietly(input);
        }

        Optional<InputStream> opt = storage.read(path);
        assertTrue(opt.isPresent());
        assertEquals(expected, IOUtils.toString(opt.get()));

        storage.delete(path);
        opt = storage.read(path);
        assertFalse(opt.isPresent());
    }
}
