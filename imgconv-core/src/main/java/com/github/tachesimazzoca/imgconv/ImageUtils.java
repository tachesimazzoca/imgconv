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

    private static ImageReader createImageReader(ImageInputStream input) throws IOException {
        ImageReader ir;
        Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
        if (readers != null && readers.hasNext()) {
            ir = readers.next();
            ir.setInput(input);
        } else {
            throw new IllegalArgumentException("No available image readers.");
        }
        return ir;
    }

    private static ImageWriter createImageWriter(
            ImageOutputStream output, ImageReader reader) throws IOException {
        ImageWriter iw = ImageIO.getImageWriter(reader);
        if (iw == null)
            throw new IllegalArgumentException("No available image writers.");
        iw.setOutput(output);
        return iw;
    }

    private static ImageWriter createImageWriter(
            ImageOutputStream output, String formatName) throws IOException {
        ImageWriter iw;
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
        if (writers != null && writers.hasNext()) {
            iw = writers.next();
            iw.setOutput(output);
        } else {
            throw new IllegalArgumentException("No available image writers.");
        }
        return iw;
    }

    public static <T> T withImageReader(
            InputStream input, Readable<T> func) throws IOException {

        ImageReader ir = null;
        T result = null;

        try {
            ImageIO.setUseCache(false);
            // reader
            ImageInputStream iis = ImageIO.createImageInputStream(input);
            ir = createImageReader(iis);
            // apply function
            result = func.read(ir);

        } catch (IOException e) {
            throw e;
        } finally {
            if (ir != null)
                ir.dispose();
        }
        return result;
    }

    public static void withImageWriter(InputStream input, OutputStream output,
            Writable func) throws IOException {
        withImageWriter(input, output, null, func);
    }

    public static void withImageWriter(
            InputStream input, OutputStream output,
            String formatName, Writable func) throws IOException {
        ImageReader ir = null;
        ImageWriter iw = null;
        ImageOutputStream ios = null;
        try {
            ImageIO.setUseCache(false);
            // reader
            ImageInputStream iis = ImageIO.createImageInputStream(input);
            ir = createImageReader(iis);
            // writer
            ios = ImageIO.createImageOutputStream(output);
            if (formatName != null)
                iw = createImageWriter(ios, formatName);
            else
                iw = createImageWriter(ios, ir);
            // apply function
            func.write(ir, iw);

        } catch (IOException e) {
            throw e;
        } finally {
            if (ir != null)
                ir.dispose();
            if (iw != null)
                iw.dispose();
            closeQuietly(ios);
        }
    }

    public static Image inspect(InputStream input) {
        try {
            return ImageUtils.withImageReader(input, INSPECT_FUNCTION);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void convert(
            InputStream input,
            OutputStream output,
            Converter... converters) throws IOException {
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
