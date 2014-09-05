package com.github.tachesimazzoca.imgconv.converter;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.github.tachesimazzoca.imgconv.ImageUtils;
import com.github.tachesimazzoca.imgconv.Converter;
import com.github.tachesimazzoca.imgconv.Geometry;

public class SizeConverterTest {
    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    private void assertConvertion(Converter converter, File source, File expected)
            throws IOException {
        FileInputStream input = new FileInputStream(source);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        converter.convert(input, output);
        assertArrayEquals(readFileToByteArray(expected), output.toByteArray());
    }

    @Test
    public void testConvertJPEG() throws IOException {
        assertConvertion(
                new SizeConverter(60, 60, Geometry.ScalingStrategy.EMPHATIC),
                openTestFile("/peacock.jpg"),
                openTestFile("/peacock_60x60_emphatic.jpg"));
        assertConvertion(
                new SizeConverter(60, 60, Geometry.ScalingStrategy.MAXIMUM),
                openTestFile("/peacock.jpg"),
                openTestFile("/peacock_60x60_maximum.jpg"));
        assertConvertion(
                new SizeConverter(200, 200, Geometry.ScalingStrategy.MINIMUM),
                openTestFile("/peacock.jpg"),
                openTestFile("/peacock_200x200_minimum.jpg"));
    }

    @Test
    public void testConvertPNG() throws IOException {
        assertConvertion(
                new SizeConverter(80, 50, Geometry.ScalingStrategy.EMPHATIC),
                openTestFile("/desktop.png"),
                openTestFile("/desktop_80x50_emphatic.png"));
        assertConvertion(
                new SizeConverter(80, 50, Geometry.ScalingStrategy.MAXIMUM),
                openTestFile("/desktop.png"),
                openTestFile("/desktop_80x50_maximum.png"));
        assertConvertion(
                new SizeConverter(80, 50, Geometry.ScalingStrategy.MINIMUM),
                openTestFile("/desktop.png"),
                openTestFile("/desktop_80x50_minimum.png"));
    }

    @Test
    public void testConvertGIF() throws IOException {
        assertConvertion(
                new SizeConverter(20, 10, Geometry.ScalingStrategy.EMPHATIC),
                openTestFile("/cmyk.gif"),
                openTestFile("/cmyk_20x10_emphatic.gif"));
        assertConvertion(
                new SizeConverter(20, 10, Geometry.ScalingStrategy.MAXIMUM),
                openTestFile("/cmyk.gif"),
                openTestFile("/cmyk_20x10_maximum.gif"));
        assertConvertion(
                new SizeConverter(80, 60, Geometry.ScalingStrategy.MINIMUM),
                openTestFile("/cmyk.gif"),
                openTestFile("/cmyk_80x60_minimum.gif"));
    }

    @Test
    public void testConvertTransparentGIF() throws IOException {
        // skip resizing if the GIF image has a transparent index.
        assertConvertion(
                new SizeConverter(20, 20, Geometry.ScalingStrategy.EMPHATIC),
                openTestFile("/loader.gif"),
                openTestFile("/loader.gif"));
        assertConvertion(
                new SizeConverter(20, 20, Geometry.ScalingStrategy.MAXIMUM),
                openTestFile("/loader.gif"),
                openTestFile("/loader.gif"));
        assertConvertion(
                new SizeConverter(20, 20, Geometry.ScalingStrategy.MINIMUM),
                openTestFile("/loader.gif"),
                openTestFile("/loader.gif"));
    }

    private byte[] readFileToByteArray(File f) throws IOException {
        FileInputStream input = new FileInputStream(f);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ImageUtils.copyLarge(input, output);
        } catch (IOException e) {
            throw e;
        } finally {
            ImageUtils.closeQuietly(input);
            ImageUtils.closeQuietly(output);
        }
        return output.toByteArray();
    }
}
