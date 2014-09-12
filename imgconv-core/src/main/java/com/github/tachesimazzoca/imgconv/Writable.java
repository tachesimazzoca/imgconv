package com.github.tachesimazzoca.imgconv;

import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

/**
 * Instances of classes that implement this interface are used to read and write
 * images with {@code Image(Reader|Writer)}.
 * 
 * @see javax.imageio.ImageReader
 * @see javax.imageio.ImageWriter
 * @see ImageUtils#withImageWriter
 */
public interface Writable {
    /**
     * Do something with {@code Image(Reader|Writer)}.
     */
    void write(ImageReader reader, ImageWriter writer) throws IOException;
}
