package com.github.tachesimazzoca.imgconv;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.IOUtils;

public class ImageUtils {
    private ImageUtils() {
        throw new UnsupportedOperationException();
    }

    public static void convert(
            Iterable<Converter> converters,
            InputStream input,
            OutputStream output) throws IOException {
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            IOUtils.copyLarge(input, baos);
            bais = new ByteArrayInputStream(baos.toByteArray());
            baos = new ByteArrayOutputStream();
            for (Converter converter : converters) {
                converter.convert(bais, baos);
                bais = new ByteArrayInputStream(baos.toByteArray());
                baos = new ByteArrayOutputStream();
            }
            IOUtils.copyLarge(bais, output);
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(bais);
            IOUtils.closeQuietly(baos);
        }
    }

    public static void flushQuietly(ImageInputStream stream) {
        if (stream != null) {
            try {
                stream.flush();
            } catch (IOException e) {
                // flush quietly
            }
        }
    }
}
