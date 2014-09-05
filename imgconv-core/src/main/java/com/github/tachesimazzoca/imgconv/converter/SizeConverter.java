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
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

import com.github.tachesimazzoca.imgconv.Converter;
import com.github.tachesimazzoca.imgconv.Geometry;
import com.github.tachesimazzoca.imgconv.ImageUtils;
import com.github.tachesimazzoca.imgconv.Writable;

public class SizeConverter implements Converter {
    private final Writable func;

    /**
     * Creates a converter with the specified {@link Geometry} object.
     * 
     * @param geometry
     */
    public SizeConverter(Geometry geometry) {
        this.func = createFunction(geometry);
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

    @Override
    public void convert(InputStream input, OutputStream output) throws IOException {
        ImageUtils.withImageWriter(input, output, func);
    }

    private Writable createFunction(final Geometry geometry) {
        return new Writable() {
            public void write(ImageReader reader, ImageWriter writer) throws IOException {
                final int N = reader.getNumImages(true);
                IIOImage[] imgs = new IIOImage[N];
                for (int i = 0; i < N; i++) {
                    BufferedImage bimg = reader.read(i);
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
                    writer.write(imgs[0]);
                } else {
                    writer.prepareWriteSequence(reader.getStreamMetadata());
                    for (int i = 0; i < imgs.length; i++) {
                        writer.writeToSequence(imgs[i], null);
                    }
                    writer.endWriteSequence();
                }
            }
        };
    }
}
