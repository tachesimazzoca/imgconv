package com.github.tachesimazzoca.imgconv;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface Converter {
    /**
     * Converts an image stream.
     * 
     * @param input
     * @param output
     * @throws IOException
     */
    void convert(InputStream input, OutputStream output) throws IOException;
}
