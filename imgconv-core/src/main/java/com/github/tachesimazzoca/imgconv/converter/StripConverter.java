package com.github.tachesimazzoca.imgconv.converter;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

import com.github.tachesimazzoca.imgconv.Converter;
import com.github.tachesimazzoca.imgconv.ImageUtils;
import com.github.tachesimazzoca.imgconv.Writable;

public class StripConverter implements Converter {
    private static final Writable func = new Writable() {
        public void write(ImageReader reader, ImageWriter writer) throws IOException {
            final int N = reader.getNumImages(true);
            IIOImage[] imgs = new IIOImage[N];
            for (int i = 0; i < N; i++) {
                imgs[i] = new IIOImage(reader.read(i), null, null);
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

    @Override
    public void convert(InputStream input, OutputStream output) throws IOException {
        ImageUtils.withImageWriter(input, output, func);
    }
}
