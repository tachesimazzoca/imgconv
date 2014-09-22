package com.github.tachesimazzoca.imgconv;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class ImageUtilsTest {
    private static class TestPattern {
        public final String source;
        public final String destination;
        public final ConvertOption convertOption;

        private TestPattern(String source, String destination, ConvertOption convertOption) {
            this.source = source;
            this.destination = destination;
            this.convertOption = convertOption;
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

    private void runTestPatterns(TestPattern... patterns)
            throws IOException {
        for (int i = 0; i < patterns.length; i++) {
            FileInputStream fis = new FileInputStream(openTestFile(patterns[i].source));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageUtils.convert(fis, baos, patterns[i].convertOption);
            assertArrayEquals("patterns[" + i + "]", FileUtils.readFileToByteArray(
                    openTestFile(patterns[i].destination)), baos.toByteArray());
        }
    }

    @Test
    public void testResizeJPEG() throws IOException {
        runTestPatterns(
                new TestPattern("/peacock.jpg", "/peacock_60x60_emphatic.jpg",
                        ConvertOption.builder().geometry(new Geometry(
                                60, 60, Geometry.ScalingStrategy.EMPHATIC)).build()),

                new TestPattern("/peacock.jpg", "/peacock_60x60_maximum.jpg",
                        ConvertOption.builder().geometry(new Geometry(
                                60, 60, Geometry.ScalingStrategy.MAXIMUM)).build()),

                new TestPattern("/peacock.jpg", "/peacock_200x200_minimum.jpg",
                        ConvertOption.builder().geometry(new Geometry(
                                200, 200, Geometry.ScalingStrategy.MINIMUM)).build()));
    }

    @Test
    public void testResizePNG() throws IOException {
        runTestPatterns(
                new TestPattern("/desktop.png", "/desktop_80x50_emphatic.png",
                        ConvertOption.builder().geometry(new Geometry(
                                80, 50, Geometry.ScalingStrategy.EMPHATIC)).build()),

                new TestPattern("/desktop.png", "/desktop_80x50_maximum.png",
                        ConvertOption.builder().geometry(new Geometry(
                                80, 50, Geometry.ScalingStrategy.MAXIMUM)).build()),

                new TestPattern("/desktop.png", "/desktop_80x50_minimum.png",
                        ConvertOption.builder().geometry(new Geometry(
                                80, 50, Geometry.ScalingStrategy.MINIMUM)).build()));
    }

    @Test
    public void testResizeGIF() throws IOException {
        runTestPatterns(
                new TestPattern("/cmyk.gif", "/cmyk_20x10_emphatic.gif",
                        ConvertOption.builder().geometry(new Geometry(
                                20, 10, Geometry.ScalingStrategy.EMPHATIC)).build()),

                new TestPattern("/cmyk.gif", "/cmyk_20x10_maximum.gif",
                        ConvertOption.builder().geometry(new Geometry(
                                20, 10, Geometry.ScalingStrategy.MAXIMUM)).build()),

                new TestPattern("/cmyk.gif", "/cmyk_80x60_minimum.gif",
                        ConvertOption.builder().geometry(new Geometry(
                                80, 60, Geometry.ScalingStrategy.MINIMUM)).build()));
    }

    @Test
    public void testResizeTransparentGIF() throws IOException {
        // skip resizing if the GIF image has a transparent index.
        runTestPatterns(
                new TestPattern("/loader.gif", "/loader.gif",
                        ConvertOption.builder().geometry(new Geometry(
                                20, 20, Geometry.ScalingStrategy.EMPHATIC)).build()),

                new TestPattern("/loader.gif", "/loader.gif",
                        ConvertOption.builder().geometry(new Geometry(
                                20, 20, Geometry.ScalingStrategy.MAXIMUM)).build()),

                new TestPattern("/loader.gif", "/loader.gif",
                        ConvertOption.builder().geometry(new Geometry(
                                20, 20, Geometry.ScalingStrategy.MINIMUM)).build()));
    }

    @Test
    public void testReformat() throws IOException {
        final ConvertOption JPG_FORMAT =
                ConvertOption.builder().format(ConvertOption.Format.JPEG).build();
        final ConvertOption PNG_FORMAT =
                ConvertOption.builder().format(ConvertOption.Format.PNG).build();
        final ConvertOption GIF_FORMAT =
                ConvertOption.builder().format(ConvertOption.Format.GIF).build();
        runTestPatterns(
                new TestPattern("/peacock.jpg", "/peacock.gif", GIF_FORMAT),
                new TestPattern("/peacock.jpg", "/peacock.png", PNG_FORMAT),
                new TestPattern("/loader.gif", "/loader.jpg", JPG_FORMAT),
                new TestPattern("/loader.gif", "/loader.png", PNG_FORMAT),
                new TestPattern("/cmyk.gif", "/cmyk.jpg", JPG_FORMAT),
                new TestPattern("/cmyk.gif", "/cmyk.png", PNG_FORMAT),
                new TestPattern("/desktop.png", "/desktop.jpg", JPG_FORMAT),
                new TestPattern("/desktop.png", "/desktop.gif", GIF_FORMAT));
    }

    @Test
    public void testStrip() throws IOException {
        final ConvertOption STRIP_ON =
                ConvertOption.builder().flag(ConvertOption.Flag.STRIP).build();
        runTestPatterns(new TestPattern("/desktop.png", "/desktop_strip.png", STRIP_ON));
    }
}
