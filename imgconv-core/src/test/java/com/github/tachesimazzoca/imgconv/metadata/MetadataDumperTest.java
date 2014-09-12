package com.github.tachesimazzoca.imgconv.metadata;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;

import com.github.tachesimazzoca.imgconv.ImageUtils;
import com.github.tachesimazzoca.imgconv.Readable;
import com.github.tachesimazzoca.imgconv.TestUtils;

public class MetadataDumperTest {
    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    private static String inspect(final InputStream input) throws IOException {
        return ImageUtils.withImageReader(input, new Readable<String>() {
            @Override
            public String read(ImageReader ir) throws IOException {
                MetadataDumper dumper = new MetadataDumper();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final int N = ir.getNumImages(true);
                for (int i = 0; i < N; i++) {
                    IIOMetadata meta = ir.getImageMetadata(i);
                    String[] names = meta.getMetadataFormatNames();
                    for (int j = 0; j < names.length; j++) {
                        dumper.dump(meta, baos);
                    }
                }
                return baos.toString();
            }
        });
    }

    @Test
    public void testInspectJPEG() throws IOException {
        String expected = TestUtils.readFileToString(openTestFile("/peacock.jpg.dump"));
        String actual = inspect(new FileInputStream(openTestFile("/peacock.jpg")));
        assertEquals(expected, actual);
    }
}
