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

public class AnimationConverterTest {
    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    @Test
    public void testConvertSingleGIF() throws IOException {
        FileInputStream input = new FileInputStream(openTestFile("/loader.gif"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Converter converter = new AnimationConverter(1);
        ImageUtils.convert(input, output, null, converter);
        assertArrayEquals(TestUtils.readFileToByteArray(
                openTestFile("/loader_1time.gif")), output.toByteArray());
    }

    @Test
    public void testConvertAnimationGIF() throws IOException {
        FileInputStream input = new FileInputStream(openTestFile("/loader.gif"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Converter converter = new AnimationConverter(3);
        ImageUtils.convert(input, output, null, converter);
        assertArrayEquals(TestUtils.readFileToByteArray(
                openTestFile("/loader_3times.gif")), output.toByteArray());
    }
}
