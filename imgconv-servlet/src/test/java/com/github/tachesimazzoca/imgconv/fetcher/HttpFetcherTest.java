package com.github.tachesimazzoca.imgconv.fetcher;

import com.github.tachesimazzoca.imgconv.storage.FileStorage;
import com.github.tachesimazzoca.imgconv.storage.MockStorage;
import com.google.common.base.Optional;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import static org.apache.commons.io.IOUtils.copyLarge;
import static org.junit.Assert.*;

public class HttpFetcherTest {
    private static int MOCK_SERVER_PORT = 9000;

    @Rule
    public TemporaryFolder tmpdir = new TemporaryFolder();

    @BeforeClass
    public static void assignMockServerPort() {
        for (int n = 9000; n < 9100; n++) {
            if (isAvailablePort(n)) {
                MOCK_SERVER_PORT = n;
                return;
            }
        }
    }

    private File getTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    private HttpFetcher createMockFetcher(byte[] bytes) {
        return createMockFetcher(bytes, System.currentTimeMillis());
    }

    private HttpFetcher createMockFetcher(byte[] bytes, long timestamp) {
        return new HttpFetcher(
                "http://localhost:" + MOCK_SERVER_PORT,
                new MockStorage(bytes, timestamp));
    }

    @Test
    public void testBadStatusCode() throws IOException {
        HttpFetcher fetcher = createMockFetcher(null);
        int[] statusCodes = { 302, 401, 403, 404, 503, 505 };

        for (int i = 0; i < statusCodes.length; i++) {
            HttpServer server = null;
            try {
                server = createHttpServer(statusCodes[i], new byte[0]);
                server.start();
                String path = "/deadbeef.jpg";
                assertEquals(Optional.<InputStream> absent(), fetcher.fetch(path));
            } finally {
                if (server != null) {
                    server.stop(1);
                }
            }
        }
    }

    @Test
    public void testReadCache() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(getTestFile("/cmyk.gif"));
        HttpFetcher fetcher = createMockFetcher(bytes);

        HttpServer server = null;
        try {
            server = createHttpServer(200, new byte[0],
                    "Last-Modified: Tue, 30 Sep 2014 01:23:45 GMT");
            server.start();
            String path = "/cmyk.gif";
            Optional<InputStream> opt = fetcher.fetch(path);
            assertTrue(opt.isPresent());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copyLarge(opt.get(), baos);
            assertArrayEquals(bytes, baos.toByteArray());
        } finally {
            if (server != null) {
                server.stop(0);
            }
        }
    }

    @Test
    public void testCacheMiss() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(getTestFile("/cmyk.gif"));
        HttpFetcher fetcher = createMockFetcher(bytes, 0L);

        HttpServer server = null;
        try {
            server = createHttpServer(200, bytes,
                    "Last-Modified: Tue, 30 Sep 2014 01:23:45 GMT");
            server.start();
            String path = "/cmyk.gif";
            Optional<InputStream> opt = fetcher.fetch(path);
            assertTrue(opt.isPresent());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copyLarge(opt.get(), baos);
            assertArrayEquals(bytes, baos.toByteArray());
        } finally {
            if (server != null) {
                server.stop(0);
            }
        }
    }

    @Test
    public void testWithMockStorage() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(getTestFile("/cmyk.gif"));
        HttpFetcher fetcher = createMockFetcher(null);

        HttpServer server = null;
        try {
            server = createHttpServer(200, bytes);
            server.start();
            String path = "/cmyk.gif";
            Optional<InputStream> opt = fetcher.fetch(path);
            assertTrue(opt.isPresent());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copyLarge(opt.get(), baos);
            assertArrayEquals(bytes, baos.toByteArray());
        } finally {
            if (server != null) {
                server.stop(0);
            }
        }
    }

    @Test
    public void testWithFileStorage() throws IOException {
        HttpFetcher fetcher = new HttpFetcher(
                "http://localhost:" + MOCK_SERVER_PORT,
                new FileStorage(tmpdir.newFolder()));

        byte[] bytes = FileUtils.readFileToByteArray(getTestFile("/cmyk.gif"));
        HttpServer server = null;
        try {
            server = createHttpServer(200, bytes);
            server.start();
            String path = "/cmyk.gif";
            Optional<InputStream> opt = fetcher.fetch(path);
            assertTrue(opt.isPresent());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copyLarge(opt.get(), baos);
            assertArrayEquals(bytes, baos.toByteArray());
        } finally {
            if (server != null) {
                server.stop(0);
            }
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
                Headers res = t.getResponseHeaders();
                for (int i = 0; i < kvs.length; i++) {
                    res.add(kvs[i][0], kvs[i][1]);
                }
                if (t.getRequestMethod().equals("HEAD"))
                    t.sendResponseHeaders(status, -1);
                else
                    t.sendResponseHeaders(status, bytes.length);
                OutputStream os = t.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });
        return server;
    }

    public static boolean isAvailablePort(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            IOUtils.closeQuietly(ds);
            IOUtils.closeQuietly(ss);
        }
        return false;
    }
}
