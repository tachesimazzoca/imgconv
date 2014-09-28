package com.github.tachesimazzoca.imgconv.fetcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.ImmutableMap;

import com.github.tachesimazzoca.imgconv.storage.FileStorage;

public class FetcherFactory {
    public static Map<String, Fetcher> createFetcherMap(File confPath)
            throws IOException {
        final String[] extensions = { "properties" };
        Iterator<File> fs = FileUtils.iterateFiles(confPath, extensions, false);
        ImmutableMap.Builder<String, Fetcher> builder = ImmutableMap.builder();
        while (fs.hasNext()) {
            File f = fs.next();
            String key = f.getName().replaceAll(".properties$", "");
            if (key.isEmpty())
                continue;
            Properties prop = new Properties();
            prop.load(new FileInputStream(f));
            FileStorage storage = new FileStorage(
                    new File(prop.getProperty("cacheDirectory")));
            HttpFetcher fetcher = new HttpFetcher(prop.getProperty("baseURL"), storage);
            builder.put(key, fetcher);
        }
        return builder.build();
    }
}
