package com.github.tachesimazzoca.imgconv;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    @Test
    public void testInspectJPEG() throws IOException {
        Image image = ImageUtils.inspect(new FileInputStream(
                openTestFile("/peacock_60x60_emphatic.jpg")));
        assertEquals(Image.Format.JPEG, image.getFormat());
        assertEquals(60, image.getWidth());
        assertEquals(60, image.getHeight());
    }

    @Test
    public void testInspectPNG() throws IOException {
        Image image = ImageUtils.inspect(new FileInputStream(
                openTestFile("/desktop_80x50_emphatic.png")));
        assertEquals(Image.Format.PNG, image.getFormat());
        assertEquals(80, image.getWidth());
        assertEquals(50, image.getHeight());
    }

    @Test
    public void testInspectGIF() throws IOException {
        Image image = ImageUtils.inspect(new FileInputStream(
                openTestFile("/cmyk_20x10_emphatic.gif")));
        assertEquals(Image.Format.GIF, image.getFormat());
        assertEquals(20, image.getWidth());
        assertEquals(10, image.getHeight());
    }

    @Test
    public void testInspectAnimationGIF() throws IOException {
        Image image = ImageUtils.inspect(new FileInputStream(
                openTestFile("/loader.gif")));
        assertEquals(Image.Format.GIF, image.getFormat());
        assertEquals(32, image.getWidth());
        assertEquals(32, image.getHeight());
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
