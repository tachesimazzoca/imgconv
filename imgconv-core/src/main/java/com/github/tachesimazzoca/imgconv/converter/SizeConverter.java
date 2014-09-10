package com.github.tachesimazzoca.imgconv.converter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import javax.imageio.IIOImage;

import com.github.tachesimazzoca.imgconv.Converter;
import com.github.tachesimazzoca.imgconv.Geometry;

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

    @Override
    public IIOImage[] convert(IIOImage[] images) {
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
