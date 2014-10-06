package com.github.tachesimazzoca.imgconv.converter;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.github.tachesimazzoca.imgconv.Converter;
import com.github.tachesimazzoca.imgconv.ConvertOption;
import com.github.tachesimazzoca.imgconv.ImageUtils;

import org.apache.commons.io.FileUtils;

public class KtaiCopyguardConverterTest {
    private static final ConvertOption DEFAULT_CONVERT_OPTION =
            ConvertOption.builder().build();

    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    @Test
    public void testConvertJPEG() throws IOException {
        Converter converter = new KtaiCopyguardConverter();
        String[][] ptns = {
                { "peacock.jpg", "peacock_copyguard.jpg" },
                { "desktop.png", "desktop_copyguard.png" },
                { "cmyk.gif", "cmyk_copyguard.gif" } };
        for (int i = 0; i < ptns.length; i++) {
            FileInputStream input = new FileInputStream(openTestFile(ptns[i][0]));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageUtils.convert(input, output, DEFAULT_CONVERT_OPTION, converter);
            assertArrayEquals(FileUtils.readFileToByteArray(
                    openTestFile(ptns[i][1])), output.toByteArray());
        }
    }
}
