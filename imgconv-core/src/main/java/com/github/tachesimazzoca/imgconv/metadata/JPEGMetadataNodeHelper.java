package com.github.tachesimazzoca.imgconv.metadata;

import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NodeList;

/**
 * The class helps to access any attributes in the
 * <code>javax_imageio_jpeg_image_1.0</code> metadata (JPEG) format.
 * 
 * For a comment text, the specified value will be set at
 * <code>markerSequence/com@comment</code>.
 */
public class JPEGMetadataNodeHelper implements MetadataNodeHelper {
    private static final String TAG_MAKER_SEQUENCE = "markerSequence";
    private static final String TAG_COMMENT = "com";
    private static final String ATTRIBUTE_COMMENT = "comment";

    public String getComment(IIOMetadataNode rootNode) {
        NodeList nl = rootNode.getElementsByTagName(TAG_MAKER_SEQUENCE);
        if (nl.getLength() == 0)
            return null;

        IIOMetadataNode parentNode = (IIOMetadataNode) nl.item(0);
        nl = parentNode.getElementsByTagName(TAG_COMMENT);
        if (nl.getLength() == 0)
            return null;

        return ((IIOMetadataNode) nl.item(0)).getAttribute(ATTRIBUTE_COMMENT);
    }

    public void setComment(IIOMetadataNode rootNode, String value) {
        IIOMetadataNode commentNode = new IIOMetadataNode(TAG_COMMENT);
        commentNode.setAttribute(ATTRIBUTE_COMMENT, value);
        NodeList nl = rootNode.getElementsByTagName(TAG_MAKER_SEQUENCE);
        if (nl.getLength() == 0) {
            IIOMetadataNode parentNode = new IIOMetadataNode(TAG_MAKER_SEQUENCE);
            parentNode.appendChild(commentNode);
            rootNode.appendChild(parentNode);
        } else {
            IIOMetadataNode parentNode = (IIOMetadataNode) nl.item(0);
            nl = parentNode.getElementsByTagName(TAG_COMMENT);
            if (nl.getLength() == 0) {
                parentNode.appendChild(commentNode);
            } else {
                parentNode.replaceChild(commentNode, nl.item(0));
            }
        }
    }

    public void removeComment(IIOMetadataNode rootNode) {
        NodeList nl = rootNode.getElementsByTagName(TAG_MAKER_SEQUENCE);
        if (nl.getLength() == 0)
            return;

        IIOMetadataNode parentNode = (IIOMetadataNode) nl.item(0);
        nl = parentNode.getElementsByTagName(TAG_COMMENT);
        if (nl.getLength() == 0)
            return;

        parentNode.removeChild(nl.item(0));
    }
}
