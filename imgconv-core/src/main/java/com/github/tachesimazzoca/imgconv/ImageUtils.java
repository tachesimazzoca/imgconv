package com.github.tachesimazzoca.imgconv;

import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

public final class ImageUtils {
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

    private static void closeQuietly(Closeable closeable) {
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
            ConvertOption option,
            Converter... converters) throws IOException {

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
            String formatName = option.getFormat().getFormatName();
            if (!formatName.isEmpty())
                iw = createImageWriter(ios, formatName);
            else
                iw = createImageWriter(ios, ir);

            // prepare an array of IIOImage
            int N = ir.getNumImages(true);
            IIOImage[] imgs = new IIOImage[N];
            for (int i = 0; i < N; i++) {
                imgs[i] = ir.readAll(i, null);
            }

            // strip
            if (option.hasFlag(ConvertOption.Flag.STRIP)) {
                for (int i = 0; i < imgs.length; i++) {
                    imgs[i].setThumbnails(null);
                    imgs[i].setMetadata(null);
                }
            }

            // execute plug-ins
            for (int i = 0; i < converters.length; i++) {
                imgs = converters[i].convert(imgs);
            }
            if (imgs.length < 1)
                throw new IllegalArgumentException("No converted images");

            // resize
            if (option.hasGeometry()) {
                imgs = resize(imgs, option.getGeometry());
            }

            // write images
            if (formatName.isEmpty() || formatName.equals(ir.getFormatName())) {
                if (imgs.length == 1) {
                    iw.write(imgs[0]);
                } else {
                    iw.prepareWriteSequence(imgs[0].getMetadata());
                    for (int i = 0; i < imgs.length; i++) {
                        iw.writeToSequence(imgs[i], null);
                    }
                    iw.endWriteSequence();
                }
            } else {
                // convert file format
                BufferedImage bimg = ir.read(0);
                if (formatName.equals("png") || !bimg.getColorModel().hasAlpha()) {
                    iw.write(new IIOImage(bimg, null, null));
                } else {
                    BufferedImage buf = new BufferedImage(bimg.getWidth(), bimg.getHeight(),
                            BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = buf.createGraphics();
                    g2d.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(bimg, 0, 0, bimg.getWidth(), bimg.getHeight(), null);
                    g2d.dispose();
                    iw.write(new IIOImage(buf, null, null));
                }
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
    }

    public static IIOImage[] resize(IIOImage[] images, Geometry geometry) {
        IIOImage[] imgs = new IIOImage[images.length];
        for (int i = 0; i < images.length; i++) {
            BufferedImage bimg = (BufferedImage) images[i].getRenderedImage();
            Dimension dim = geometry.scale(bimg.getWidth(),
                    bimg.getHeight());
            int w = (int) dim.getWidth();
            int h = (int) dim.getHeight();
            ColorModel cm = bimg.getColorModel();
            boolean transparentGIF = cm.hasAlpha() && (cm instanceof IndexColorModel);
            // convert if the image is not a transparent GIF
            if (!transparentGIF && (w != bimg.getWidth() || h != bimg.getHeight())) {
                BufferedImage buf;
                if (cm instanceof IndexColorModel)
                    buf = new BufferedImage(w, h, bimg.getType(), (IndexColorModel) cm);
                else
                    buf = new BufferedImage(w, h, bimg.getType());
                Graphics2D g2d = buf.createGraphics();
                g2d.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(bimg, 0, 0, w, h, null);
                g2d.dispose();
                imgs[i] = new IIOImage(buf, null, null);
            } else {
                imgs[i] = images[i];
            }
        }
        return imgs;
    }
}
