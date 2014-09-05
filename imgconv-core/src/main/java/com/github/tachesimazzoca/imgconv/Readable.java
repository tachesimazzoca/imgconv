package com.github.tachesimazzoca.imgconv;

import java.io.IOException;
import javax.imageio.ImageReader;

public interface Readable<T> {
    T read(ImageReader reader) throws IOException;
}
