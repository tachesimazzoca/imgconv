package com.github.tachesimazzoca.imgconv.metadata;

import static org.junit.Assert.*;

import org.junit.Test;

import javax.imageio.metadata.IIOMetadataNode;

public class JPEGMetadataNodeHelperTest {
    @Test
    public void testComment() {
        IIOMetadataNode rnode = new IIOMetadataNode("root");
        JPEGMetadataNodeHelper helper = new JPEGMetadataNodeHelper();
        final String expected = "test comment";
        helper.setComment(rnode, expected);
        assertEquals(expected, helper.getComment(rnode));
        helper.removeComment(rnode);
        assertEquals(null, helper.getComment(rnode));
    }
}
