package com.github.tachesimazzoca.imgconv.converter;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

import com.github.tachesimazzoca.imgconv.Converter;
import com.github.tachesimazzoca.imgconv.ImageUtils;
import com.github.tachesimazzoca.imgconv.Writable;

public class FormatConverter implements Converter {
    private final String formatName;
    private final Writable func;

    public FormatConverter(String formatName) {
        this.formatName = formatName;
        func = createFunction(this.formatName);
    }

    @Override
    public void convert(InputStream input, OutputStream output) throws IOException {
        ImageUtils.withImageWriter(input, output, formatName, func);
    }

    private Writable createFunction(final String formatName) {
        return new Writable() {
            public void write(ImageReader reader, ImageWriter writer) throws IOException {
                if (reader.getNumImages(true) < 1) {
                    throw new IllegalArgumentException("No images");
                }
                BufferedImage bimg = reader.read(0);
                if (formatName.equals("png") || !bimg.getColorModel().hasAlpha()) {
                    writer.write(new IIOImage(bimg, null, null));
                } else {
                    BufferedImage buf = new BufferedImage(bimg.getWidth(), bimg.getHeight(),
                            BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = buf.createGraphics();
                    g2d.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(bimg, 0, 0, bimg.getWidth(), bimg.getHeight(), null);
                    g2d.dispose();
                    writer.write(new IIOImage(buf, null, null));
                }
            }
        };
    }
}
