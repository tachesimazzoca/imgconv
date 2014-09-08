package com.github.tachesimazzoca.imgconv.converter;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.github.tachesimazzoca.imgconv.ImageUtils;
import com.github.tachesimazzoca.imgconv.Converter;

public class FormatConverterTest {
    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
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
            Converter converter = new FormatConverter(ptn[i][2]);
            converter.convert(fis, baos);
            assertArrayEquals(readFileToByteArray(openTestFile(
                    "/" + ptn[i][0] + "." + ptn[i][2])), baos.toByteArray());
        }
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
