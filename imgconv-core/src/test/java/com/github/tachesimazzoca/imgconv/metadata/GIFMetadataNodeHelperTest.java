package com.github.tachesimazzoca.imgconv.metadata;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import com.github.tachesimazzoca.imgconv.ImageUtils;
import com.github.tachesimazzoca.imgconv.Readable;

public class GIFMetadataNodeHelperTest {
    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    @Test
    public void testCommentNode() throws IOException {
        final GIFMetadataNodeHelper helper = new GIFMetadataNodeHelper();
        String comment = ImageUtils.withImageReader(new FileInputStream(
                openTestFile("/cmyk_copyguard.gif")),
                new Readable<String>() {
                    public String read(ImageReader reader) throws IOException {
                        IIOMetadata metadata = reader.readAll(0, null).getMetadata();
                        return helper.getComment((IIOMetadataNode) metadata.getAsTree(
                                metadata.getNativeMetadataFormatName()));
                    }
                });
        assertEquals("kddi_copyright=on,copy=\"NO\"", comment);
    }

    @Test
    public void testAccessComment() {
        IIOMetadataNode rnode = new IIOMetadataNode("root");
        GIFMetadataNodeHelper helper = new GIFMetadataNodeHelper();
        final String expected = "test comment";
        helper.setComment(rnode, expected);
        assertEquals(expected, helper.getComment(rnode));
        helper.removeComment(rnode);
        assertEquals("", helper.getComment(rnode));
    }
}
