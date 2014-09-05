package com.github.tachesimazzoca.imgconv;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Arrays;
import java.util.List;

import com.github.tachesimazzoca.imgconv.ImageUtils;

public class ImageUtilsTest {
    public class PassthruConverter implements Converter {
        public void convert(InputStream input, OutputStream output) throws IOException {
            ImageUtils.copyLarge(input, output);
        }
    }

    public class BlankConverter implements Converter {
        public void convert(InputStream input, OutputStream output) throws IOException {
            while (input.read() != -1) {
                output.write(' ');
            }
            output.close();
        }
    }

    @Test
    public void testConvert() throws IOException {
        Converter passthru = new PassthruConverter();
        Converter blank = new BlankConverter();

        List<Converter> converters = Arrays.asList(passthru, passthru, passthru);

        String source = "deadbeef";
        ByteArrayInputStream input;
        ByteArrayOutputStream output;
        input = new ByteArrayInputStream(source.getBytes());
        output = new ByteArrayOutputStream();
        ImageUtils.convert(converters, input, output);
        assertArrayEquals(source.getBytes(), output.toByteArray());

        converters = Arrays.asList(passthru, blank, passthru);
        input = new ByteArrayInputStream(source.getBytes());
        output = new ByteArrayOutputStream();
        ImageUtils.convert(converters, input, output);
        assertArrayEquals("        ".getBytes(), output.toByteArray());
    }
}
