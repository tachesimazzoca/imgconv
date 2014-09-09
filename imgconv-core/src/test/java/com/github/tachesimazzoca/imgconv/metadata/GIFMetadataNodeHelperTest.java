package com.github.tachesimazzoca.imgconv.metadata;

import static org.junit.Assert.*;

import org.junit.Test;

import javax.imageio.metadata.IIOMetadataNode;

public class GIFMetadataNodeHelperTest {
    @Test
    public void testComment() {
        IIOMetadataNode rnode = new IIOMetadataNode("root");
        GIFMetadataNodeHelper helper = new GIFMetadataNodeHelper();
        final String expected = "test comment";
        helper.setComment(rnode, expected);
        assertEquals(expected, helper.getComment(rnode));
        helper.removeComment(rnode);
        assertEquals("", helper.getComment(rnode));
    }
}
