package com.github.tachesimazzoca.imgconv.converter;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.github.tachesimazzoca.imgconv.Converter;
import com.github.tachesimazzoca.imgconv.ImageUtils;

import com.github.tachesimazzoca.imgconv.TestUtils;

public class StripConverterTest {
    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    @Test
    public void testConvertPNG() throws IOException {
        FileInputStream input = new FileInputStream(openTestFile("/desktop.png"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Converter converter = new StripConverter();
        ImageUtils.convert(input, output, null, converter);
        assertArrayEquals(TestUtils.readFileToByteArray(
                openTestFile("/desktop_strip.png")), output.toByteArray());
    }
}
