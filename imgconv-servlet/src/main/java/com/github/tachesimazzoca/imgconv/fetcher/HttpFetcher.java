package com.github.tachesimazzoca.imgconv.fetcher;

import java.io.InputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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

    public Optional<InputStream> fetch(String path)
            throws IOException {
        Optional<InputStream> cache = storage.read(path);
        if (cache.isPresent())
            return cache;

        CloseableHttpClient cl = HttpClients.createDefault();
        CloseableHttpResponse resp = null;
        try {
            HttpGet req = new HttpGet(baseurl + path);
            resp = cl.execute(req);
            int status = resp.getStatusLine().getStatusCode();
            if (status != 200)
                return Optional.absent();

            HttpEntity entity = resp.getEntity();
            if (entity == null)
                throw new IOException("The entity is null: " + req.getURI());

            storage.write(path, entity.getContent());
            return storage.read(path);

        } finally {
            if (cl != null)
                cl.close();
            if (resp != null)
                resp.close();
        }
    }
}
