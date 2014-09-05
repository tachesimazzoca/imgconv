package com.github.tachesimazzoca.imgconv;

import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

public interface Writable {
    void write(ImageReader reader, ImageWriter writer) throws IOException;
}
