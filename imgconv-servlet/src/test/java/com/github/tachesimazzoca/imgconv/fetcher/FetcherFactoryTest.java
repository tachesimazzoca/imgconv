package com.github.tachesimazzoca.imgconv.fetcher;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FetcherFactoryTest {
    private File getTestFile(String path) {
        return new File(getClass().getResource("/test" + path).getPath());
    }

    @Test
    public void testCreateFetcherMap() throws IOException {
        Map<String, Fetcher> m = FetcherFactory.createFetcherMap(getTestFile("/conf"));
        assertEquals(2, m.size());
        assertTrue(m.containsKey("a"));
        assertTrue(m.containsKey("b"));
    }
}
