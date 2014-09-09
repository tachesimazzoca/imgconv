package com.github.tachesimazzoca.imgconv.metadata;

import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NodeList;

/**
 * The class helps to access any attributes in the
 * <code>javax_imageio_gif_image_1.0</code> metadata (GIF) format.
 * 
 * For a comment text, the specified value will be set at
 * <code>CommentExtensions/CommentExtension@value</code>.
 */
public class GIFMetadataNodeHelper implements MetadataNodeHelper {
    private static final String TAG_COMMENT_EXTENSIONS = "CommentExtensions";
    private static final String TAG_COMMENT_EXTENSION = "CommentExtension";
    private static final String ATTRIBUTE_VALUE = "value";

    public String getComment(IIOMetadataNode rootNode) {
        NodeList nl = rootNode.getElementsByTagName(TAG_COMMENT_EXTENSIONS);
        if (nl.getLength() == 0)
            return null;

        IIOMetadataNode parentNode = (IIOMetadataNode) nl.item(0);
        nl = parentNode.getElementsByTagName(TAG_COMMENT_EXTENSION);
        if (nl.getLength() == 0)
            return null;

        return ((IIOMetadataNode) nl.item(0)).getAttribute(ATTRIBUTE_VALUE);
    }

    public void setComment(IIOMetadataNode rootNode, String value) {
        IIOMetadataNode commentNode = new IIOMetadataNode(TAG_COMMENT_EXTENSION);
        commentNode.setAttribute(ATTRIBUTE_VALUE, value);
        NodeList nl = rootNode.getElementsByTagName(TAG_COMMENT_EXTENSIONS);
        if (nl.getLength() == 0) {
            IIOMetadataNode parentNode = new IIOMetadataNode(TAG_COMMENT_EXTENSIONS);
            parentNode.appendChild(commentNode);
            rootNode.appendChild(parentNode);
        } else {
            IIOMetadataNode parentNode = (IIOMetadataNode) nl.item(0);
            nl = parentNode.getElementsByTagName(TAG_COMMENT_EXTENSION);
            if (nl.getLength() == 0) {
                parentNode.appendChild(commentNode);
            } else {
                ((IIOMetadataNode) nl.item(0)).setAttribute(ATTRIBUTE_VALUE, value);
            }
        }
    }

    public void removeComment(IIOMetadataNode rootNode) {
        NodeList nl = rootNode.getElementsByTagName(TAG_COMMENT_EXTENSIONS);
        if (nl.getLength() == 0)
            return;

        IIOMetadataNode parentNode = (IIOMetadataNode) nl.item(0);
        nl = parentNode.getElementsByTagName(TAG_COMMENT_EXTENSION);
        if (nl.getLength() == 0)
            return;

        ((IIOMetadataNode) nl.item(0)).removeAttribute(ATTRIBUTE_VALUE);
    }
}
