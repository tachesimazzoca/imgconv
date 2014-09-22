package com.github.tachesimazzoca.imgconv.fetcher;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;

import java.net.InetSocketAddress;

import com.github.tachesimazzoca.imgconv.fetcher.HttpFetcher;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.google.common.base.Optional;

import static org.apache.commons.io.IOUtils.copyLarge;
import org.apache.commons.io.FileUtils;

import com.github.tachesimazzoca.imgconv.storage.Storage;
import com.github.tachesimazzoca.imgconv.storage.MockStorage;

public class HttpFetcherTest {
    private static int MOCK_SERVER_PORT = 8880;

    private File getTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    private HttpFetcher createTestFetcher(byte[] bytes) {
        return new HttpFetcher(
                "http://localhost:" + MOCK_SERVER_PORT,
                ((Storage) new MockStorage(bytes)));
    }

    @Test
    public void testBadStatusCode() throws IOException {
        HttpFetcher fetcher = createTestFetcher(null);
        int[] statusCodes = { 302, 401, 403, 404, 503, 505 };

        for (int i = 0; i < statusCodes.length; i++) {
            HttpServer server = createHttpServer(statusCodes[i], new byte[0]);
            try {
                server.start();
                String path = "/deadbeef.jpg";
                assertEquals(Optional.<InputStream> absent(), fetcher.fetch(path));
            } finally {
                server.stop(0);
            }
        }
    }

    @Test
    public void testReadCache() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(getTestFile("/cmyk.gif"));
        HttpFetcher fetcher = createTestFetcher(bytes);

        HttpServer server = createHttpServer(500, new byte[0]);
        try {
            server.start();
            String path = "/cmyk.gif";
            Optional<InputStream> opt = fetcher.fetch(path);
            assertTrue(opt.isPresent());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copyLarge(opt.get(), baos);
            assertArrayEquals(bytes, baos.toByteArray());
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void testViaHttp() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(getTestFile("/cmyk.gif"));
        HttpFetcher fetcher = createTestFetcher(null);

        HttpServer server = createHttpServer(200, bytes);
        try {
            server.start();
            String path = "/cmyk.gif";
            Optional<InputStream> opt = fetcher.fetch(path);
            assertTrue(opt.isPresent());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copyLarge(opt.get(), baos);
            assertArrayEquals(bytes, baos.toByteArray());
        } finally {
            server.stop(0);
        }
    }

    private HttpServer createHttpServer(
            final int status,
            final byte[] bytes,
            final String... headers) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress("localhost", MOCK_SERVER_PORT), 5);
        final String[][] kvs = new String[headers.length][2];
        for (int i = 0; i < headers.length; i++) {
            String[] kv = headers[i].split(":", 2);
            kvs[i][0] = kv[0].trim();
            kvs[i][1] = kv[1].trim();
        }
        server.createContext("/", new HttpHandler() {
            public void handle(HttpExchange t) throws IOException {
                t.sendResponseHeaders(status, bytes.length);
                Headers res = t.getResponseHeaders();
                for (int i = 0; i < kvs.length; i++) {
                    res.set(kvs[i][0], kvs[i][1]);
                }
                OutputStream os = t.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });
        return server;
    }
}