package com.github.tachesimazzoca.imgconv;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtils {
    private static final Readable<Image> INSPECT_FUNCTION = new Readable<Image>() {
        @Override
        public Image read(ImageReader reader) throws IOException {
            if (reader.getNumImages(true) < 1) {
                throw new IllegalArgumentException("The input has no images.");
            }
            IIOMetadata meta = reader.getImageMetadata(0);
            return new Image(Image.Format.fromNativeMetadataFormatName(
                    meta.getNativeMetadataFormatName()),
                    reader.getWidth(0),
                    reader.getHeight(0));
        }
    };

    private ImageUtils() {
        throw new UnsupportedOperationException();
    }

    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static <T> T withImageReader(
            InputStream input, Readable<T> func) throws IOException {
        return withImageReaderOrWriter(input, func, null, null);
    }

    public static void withImageWriter(
            InputStream input, OutputStream output, Writable func) throws IOException {
        withImageReaderOrWriter(input, null, output, func);
    }

    private static <T> T withImageReaderOrWriter(
            InputStream input,
            Readable<T> readFunc,
            OutputStream output,
            Writable writeFunc) throws IOException {
        ImageReader ir = null;
        ImageWriter iw = null;
        ImageOutputStream ios = null;
        T result = null;
        try {
            ImageIO.setUseCache(false);
            ImageInputStream iis = ImageIO.createImageInputStream(input);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers != null && readers.hasNext()) {
                ir = readers.next();
                ir.setInput(iis);
            }
            if (ir == null)
                throw new IllegalArgumentException("No available image readers.");

            if (output == null) {
                result = readFunc.read(ir);
            } else {
                iw = ImageIO.getImageWriter(ir);
                if (iw == null)
                    throw new IllegalArgumentException("No available image writers.");
                ios = ImageIO.createImageOutputStream(output);
                iw.setOutput(ios);
                writeFunc.write(ir, iw);
            }

        } catch (IOException e) {
            throw e;
        } finally {
            if (ir != null)
                ir.dispose();
            if (iw != null)
                iw.dispose();
            closeQuietly(ios);
        }
        return result;
    }

    public static Image inspect(InputStream input) {
        try {
            return ImageUtils.withImageReader(input, INSPECT_FUNCTION);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void convert(
            Iterable<Converter> converters,
            InputStream input,
            OutputStream output) throws IOException {
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            copyLarge(input, baos);
            bais = new ByteArrayInputStream(baos.toByteArray());
            baos = new ByteArrayOutputStream();
            for (Converter converter : converters) {
                converter.convert(bais, baos);
                bais = new ByteArrayInputStream(baos.toByteArray());
                baos = new ByteArrayOutputStream();
            }
            copyLarge(bais, output);
        } catch (IOException e) {
            throw e;
        } finally {
            closeQuietly(bais);
            closeQuietly(baos);
        }
    }
}
