package com.github.tachesimazzoca.imgconv.metadata;

import java.io.PrintWriter;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.metadata.IIOMetadata;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

/**
 * A class is used to dump IIOMetadata object.
 * 
 * @see javax.imageio.metadata.IIOMetadata
 */
public class MetadataDumper {
    /**
     * Writes a dump of the metadata to the output stream.
     * 
     * @param metadata the metadata to read from
     * @param output the output stream to write to
     */
    public void dump(IIOMetadata metadata, OutputStream output) {
        PrintWriter pw = new PrintWriter(output);
        String[] names = metadata.getMetadataFormatNames();
        for (int i = 0; i < names.length; i++) {
            writeAsText(pw, metadata.getAsTree(names[i]), 0);
        }
        pw.close();
    }

    private static void writeAsText(PrintWriter writer, Node node, int depth) {
        char[] chrs = new char[depth];
        Arrays.fill(chrs, ' ');
        String indent = new String(chrs);
        writer.print(indent);
        writer.print(node.getNodeName());
        writer.println();

        NamedNodeMap nodeMap = node.getAttributes();
        int n = nodeMap.getLength();
        for (int i = 0; i < n; i++) {
            Node item = nodeMap.item(i);
            writer.print(indent);
            writer.print(" - ");
            writer.print(item.getNodeName());
            writer.print(" : ");
            writer.print(item.getNodeValue());
            writer.println();
        }
        Node child = node.getFirstChild();
        while (child != null) {
            writeAsText(writer, child, depth + 1);
            child = child.getNextSibling();
        }
    }
}
