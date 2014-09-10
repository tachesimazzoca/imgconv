package com.github.tachesimazzoca.imgconv;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageUtilsTest {
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
        String[][] ptn = {
                { "peacock", "jpg", "gif" },
                { "peacock", "jpg", "png" },
                { "loader", "gif", "jpg" },
                { "loader", "gif", "png" },
                { "cmyk", "gif", "jpg" },
                { "cmyk", "gif", "png" },
                { "desktop", "png", "jpg" },
                { "desktop", "png", "gif" } };
        for (int i = 0; i < ptn.length; i++) {
            FileInputStream fis = new FileInputStream(openTestFile(
                    "/" + ptn[i][0] + "." + ptn[i][1]));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageUtils.convert(fis, baos, ptn[i][2]);
            assertArrayEquals(TestUtils.readFileToByteArray(openTestFile(
                    "/" + ptn[i][0] + "." + ptn[i][2])), baos.toByteArray());
        }
    }
}
