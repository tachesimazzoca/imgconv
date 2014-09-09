package com.github.tachesimazzoca.imgconv.metadata;

import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NodeList;

/**
 * The class helps to access any attributes in the
 * <code>javax_imageio_png_1.0</code> metadata (PNG) format.
 * 
 * For a comment text, the specified value will be set at
 * <code>tEXt/tEXtEntry/@value</code> corresponding
 * <code>tEXt/tEXtEntry/@keyword="Comment"</code>.
 */
public class PNGMetadataNodeHelper implements MetadataNodeHelper {
    private static final String TAG_TEXT = "tEXt";
    private static final String TAG_TEXT_ENTRY = "tEXtEntry";
    private static final String ATTRIBUTE_KEYWORD = "keyword";
    private static final String ATTRIBUTE_VALUE = "value";

    private final String keywordName;

    /**
     * Creates a new object. For a comment text, the node of keyword will be set
     * <code>tEXt/tEXtEntry/@keyword="Comment"</code>.
     */
    public PNGMetadataNodeHelper() {
        this("Comment");
    }

    /**
     * Creates a new object with the specified keyword for a comment text.
     * 
     * If the parameter <code>keywordName</code> is given as "Copyright", the
     * node of keyword will be <code>tEXt/tEXtEntry/@keyword="Copyright"</code>.
     * 
     * @param keywordName keyword of textual data.
     */
    public PNGMetadataNodeHelper(String keywordName) {
        this.keywordName = keywordName;
    }

    public String getComment(IIOMetadataNode rootNode) {
        NodeList nl = rootNode.getElementsByTagName(TAG_TEXT);
        if (nl.getLength() == 0)
            return null;

        IIOMetadataNode textNode = (IIOMetadataNode) nl.item(0);
        nl = textNode.getElementsByTagName(TAG_TEXT_ENTRY);
        if (nl.getLength() == 0)
            return null;

        int N = nl.getLength();
        for (int i = 0; i < N; i++) {
            IIOMetadataNode textEntryNode = (IIOMetadataNode) nl.item(i);
            if (textEntryNode.getAttribute(ATTRIBUTE_KEYWORD).equals(keywordName)) {
                return textEntryNode.getAttribute(ATTRIBUTE_VALUE);
            }
        }
        return null;
    }

    public void setComment(IIOMetadataNode rootNode, String value) {
        NodeList nl = rootNode.getElementsByTagName(TAG_TEXT);
        IIOMetadataNode textEntryNode = new IIOMetadataNode(TAG_TEXT_ENTRY);
        textEntryNode.setAttribute(ATTRIBUTE_KEYWORD, keywordName);
        textEntryNode.setAttribute(ATTRIBUTE_VALUE, value);
        if (nl.getLength() == 0) {
            IIOMetadataNode textNode = new IIOMetadataNode(TAG_TEXT);
            textNode.appendChild(textEntryNode);
            rootNode.appendChild(textNode);
            return;
        }

        IIOMetadataNode textNode = (IIOMetadataNode) nl.item(0);
        nl = textNode.getElementsByTagName(TAG_TEXT_ENTRY);

        int N = nl.getLength();
        IIOMetadataNode oldNode = null;
        for (int i = 0; i < N; i++) {
            oldNode = (IIOMetadataNode) nl.item(i);
            if (oldNode.getAttribute(ATTRIBUTE_KEYWORD).equals(keywordName))
                break;
        }
        if (oldNode != null) {
            oldNode.setAttribute(ATTRIBUTE_VALUE, value);
        } else {
            textNode.replaceChild(textEntryNode, oldNode);
        }
    }

    public void removeComment(IIOMetadataNode rootNode) {
        NodeList nl = rootNode.getElementsByTagName(TAG_TEXT);
        if (nl.getLength() == 0)
            return;

        IIOMetadataNode textNode = (IIOMetadataNode) nl.item(0);
        nl = textNode.getElementsByTagName(TAG_TEXT_ENTRY);
        if (nl.getLength() == 0)
            return;

        int N = nl.getLength();
        IIOMetadataNode textEntryNode = null;
        for (int i = 0; i < N; i++) {
            textEntryNode = (IIOMetadataNode) nl.item(i);
            if (textEntryNode.getAttribute(ATTRIBUTE_KEYWORD).equals(keywordName))
                break;
        }
        if (textEntryNode != null)
            textNode.removeChild(textEntryNode);
    }
}
