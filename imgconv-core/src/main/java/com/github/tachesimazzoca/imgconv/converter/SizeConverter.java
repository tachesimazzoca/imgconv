package com.github.tachesimazzoca.imgconv.converter;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import com.github.tachesimazzoca.imgconv.Converter;
import com.github.tachesimazzoca.imgconv.Geometry;
import com.github.tachesimazzoca.imgconv.ImageUtils;

public class SizeConverter implements Converter {
    private final Geometry geometry;

    /**
     * Creates a converter with the specified {@link Geometry} object.
     * 
     * @param geometry
     */
    public SizeConverter(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Creates a converter with the specified width and height.
     * 
     * @param width
     * @param height
     */
    public SizeConverter(int width, int height) {
        this(new Geometry(width, height, Geometry.ScalingStrategy.MAXIMUM));
    }

    /**
     * Creates a converter with the specified width, height and
     * {@link Geometry.ScalingStrategy}.
     * 
     * @param width
     * @param height
     * @param scalingStrategy
     */
    public SizeConverter(int width, int height, Geometry.ScalingStrategy scalingStrategy) {
        this(new Geometry(width, height, scalingStrategy));
    }

    public void convert(InputStream input, OutputStream output) throws IOException {
        ImageReader ir = null;
        ImageWriter iw = null;
        ImageOutputStream ios = null;

        try {
            // prepare Image(Reader|Writer)
            ImageIO.setUseCache(false);
            ImageInputStream iis = ImageIO.createImageInputStream(input);
            java.util.Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers != null && readers.hasNext()) {
                ir = readers.next();
                ir.setInput(iis);
            }
            if (ir == null)
                throw new IllegalArgumentException("No available image readers.");
            iw = ImageIO.getImageWriter(ir);
            if (iw == null)
                throw new IllegalArgumentException("No available image writers.");
            ios = ImageIO.createImageOutputStream(output);
            iw.setOutput(ios);

            final int N = ir.getNumImages(true);
            IIOImage[] imgs = new IIOImage[N];
            for (int i = 0; i < N; i++) {
                BufferedImage bimg = ir.read(i);
                Dimension dim = geometry.scale(bimg.getWidth(), bimg.getHeight());
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
                    imgs[i] = new IIOImage(bimg, null, null);
                }
            }
            // write images
            if (imgs.length == 1) {
                iw.write(imgs[0]);
            } else {
                iw.prepareWriteSequence(ir.getStreamMetadata());
                for (int i = 0; i < imgs.length; i++) {
                    iw.writeToSequence(imgs[i], null);
                }
                iw.endWriteSequence();
            }

        } catch (IOException e) {
            throw e;
        } finally {
            if (ir != null)
                ir.dispose();
            if (iw != null)
                iw.dispose();
            ImageUtils.flushQuietly(ios);
        }
    }
}
