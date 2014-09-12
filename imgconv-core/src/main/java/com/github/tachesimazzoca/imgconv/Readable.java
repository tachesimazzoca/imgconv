package com.github.tachesimazzoca.imgconv;

import java.io.IOException;
import javax.imageio.ImageReader;

/**
 * Instances of classes that implement this interface are used to read images
 * with {@code ImageReader}.
 * 
 * @see javax.imageio.ImageReader
 * @see ImageUtils#withImageReader
 */
public interface Readable<T> {
    /**
     * Returns something with {@code ImageReader}.
     */
    T read(ImageReader reader) throws IOException;
}
