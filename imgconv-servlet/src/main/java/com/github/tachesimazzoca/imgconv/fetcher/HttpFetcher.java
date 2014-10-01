package com.github.tachesimazzoca.imgconv.fetcher;

import java.io.InputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.base.Optional;

import com.github.tachesimazzoca.imgconv.storage.Storage;

public class HttpFetcher implements Fetcher {
    private final String baseurl;
    private final Storage storage;

    public HttpFetcher(String baseurl, Storage storage) {
        this.baseurl = baseurl;
        this.storage = storage;
    }

    public Optional<InputStream> fetch(final String path)
            throws IOException {
        Optional<Long> ts = withHttpResponse(
                new HttpHead(baseurl + path),
                new IOFunction<CloseableHttpResponse, Optional<Long>>() {
                    @Override
                    public Optional<Long> apply(CloseableHttpResponse resp) {
                        int status = resp.getStatusLine().getStatusCode();
                        if (status != 200) {
                            storage.delete(path);
                            return Optional.absent();
                        }
                        Header header = resp.getFirstHeader("Last-Modified");
                        Date dt = null;
                        if (header != null)
                            dt = DateUtils.parseDate(header.getValue());
                        if (dt != null)
                            return Optional.of(dt.getTime());
                        else
                            return Optional.of(System.currentTimeMillis());
                    }
                });
        if (!ts.isPresent())
            return Optional.absent();

        Optional<InputStream> cache = storage.read(path, ts.get());
        if (cache.isPresent())
            return cache;

        return withHttpResponse(
                new HttpGet(baseurl + path),
                new IOFunction<CloseableHttpResponse, Optional<InputStream>>() {
                    @Override
                    public Optional<InputStream> apply(CloseableHttpResponse resp)
                            throws IOException {
                        int status = resp.getStatusLine().getStatusCode();
                        if (status != 200)
                            return Optional.absent();
                        HttpEntity entity = resp.getEntity();
                        if (entity == null)
                            throw new IOException("The entity is null: " + baseurl + path);
                        storage.write(path, entity.getContent());
                        return storage.read(path);
                    }
                });
    }

    private interface IOFunction<T1, T2> {
        public T2 apply(T1 t1) throws IOException;
    }

    private static <T> T withHttpResponse(
            HttpUriRequest req,
            IOFunction<CloseableHttpResponse, T> func)
            throws IOException {
        CloseableHttpClient cl = HttpClients.createDefault();
        CloseableHttpResponse resp = null;
        try {
            resp = cl.execute(req);
            return func.apply(resp);
        } finally {
            if (cl != null)
                cl.close();
            if (resp != null)
                resp.close();
        }
    }
}
