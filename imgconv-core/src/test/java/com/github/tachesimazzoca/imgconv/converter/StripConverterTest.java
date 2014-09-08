package com.github.tachesimazzoca.imgconv.converter;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.github.tachesimazzoca.imgconv.ImageUtils;
import com.github.tachesimazzoca.imgconv.Converter;

public class StripConverterTest {
    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    @Test
    public void testConvertPNG() throws IOException {
        FileInputStream fis = new FileInputStream(openTestFile("/desktop.png"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Converter converter = new StripConverter();
        converter.convert(fis, baos);
        assertArrayEquals(readFileToByteArray(
                openTestFile("/desktop_strip.png")), baos.toByteArray());
    }

    public static byte[] readFileToByteArray(File f) throws IOException {
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
