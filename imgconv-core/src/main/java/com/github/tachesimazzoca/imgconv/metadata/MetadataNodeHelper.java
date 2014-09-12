package com.github.tachesimazzoca.imgconv.metadata;

import javax.imageio.metadata.IIOMetadataNode;

/**
 * Instances of classes that implement this interface helps to access
 * IIOImageMetadataNode.
 * 
 * @see javax.imageio.metadata.IIOMetadataNode
 */
public interface MetadataNodeHelper {
    /**
     * Returns a comment text in a given IIOMetadataNode object.
     * 
     * @param rootNode an IIOMetadataNode object
     * @return a comment text
     */
    public String getComment(IIOMetadataNode rootNode);

    /**
     * Sets a comment text in a given IIOMetadataNode object.
     * 
     * @param rootNode an IIOMetadataNode object
     * @param value a comment text
     */
    public void setComment(IIOMetadataNode rootNode, String value);

    /**
     * Removes comments in a given IIOMetadataNode object.
     * 
     * @param rootNode an IIOMetadataNode object
     */
    public void removeComment(IIOMetadataNode rootNode);
}
