package com.github.tachesimazzoca.imgconv.storage;

import com.google.common.base.Optional;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class FileStorageTest {
    @Rule
    public TemporaryFolder tmpdir = new TemporaryFolder();

    @Test(expected = java.lang.NullPointerException.class)
    public void testNullBaseDirectory() {
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
    public void testWriteReadAndDelete() {
        File dir;
        try {
            dir = tmpdir.newFolder();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        Storage storage = new FileStorage(dir);
        String path = "a.txt";
        String expected = "testWriteReadAndDelete" + System.currentTimeMillis();

        ByteArrayInputStream in0 = new ByteArrayInputStream(expected.getBytes());
        try {
            storage.write(path, in0);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(in0);
        }

        InputStream in1 = null;
        try {
            Optional<InputStream> opt = storage.read(path);
            assertTrue(opt.isPresent());
            in1 = opt.get();
            assertEquals(expected, IOUtils.toString(in1));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(in1);
        }

        InputStream in2 = null;
        try {
            storage.delete(path);
            Optional<InputStream> opt = storage.read(path);
            if (opt.isPresent()) {
                in2 = opt.get();
            }
            assertFalse(opt.isPresent());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(in2);
        }
    }
}
