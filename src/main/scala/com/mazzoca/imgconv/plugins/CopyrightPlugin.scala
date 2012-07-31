package com.mazzoca.imgconv.plugins

import java.io.{InputStream, OutputStream, IOException}

import javax.imageio.{ImageIO, IIOImage, ImageReader, ImageWriter}
import javax.imageio.stream.{ImageInputStream, ImageOutputStream}
import javax.imageio.metadata.{IIOMetadata, IIOMetadataNode}

import org.w3c.dom.{Node, NodeList}

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

class CopyrightPlugin extends Plugin {

    var comment = """copy="NO",kdd_copyright=on"""

    def execute(input:InputStream, output:OutputStream) = {

        var ir:ImageReader = null
        var iw:ImageWriter = null
        var ios:ImageOutputStream = null 

        try {

            val iis:ImageInputStream = ImageIO.createImageInputStream(input)
            Option(ImageIO.getImageReaders(iis)).map { readers =>
                if (readers.hasNext()) {
                    ir = readers.next()
                    ir.setInput(iis)
                }
            }
            if (ir == null) {
                throw new IOException()
            }

            iw = ImageIO.getImageWriter(ir)
            if (iw == null) {
                throw new IOException()
            }
            ios = ImageIO.createImageOutputStream(output)
            iw.setOutput(ios)

            val streamMeta:IIOMetadata = ir.getStreamMetadata()
            var imgs:ArrayBuffer[IIOImage] = ArrayBuffer() 

            var ni:Int = ir.getNumImages(true)
            for (i <- 0 until ni) {
                val img:IIOImage = ir.readAll(i, null)
                var metadata:IIOMetadata = ir.getImageMetadata(i)
                val fn:String = metadata.getNativeMetadataFormatName();
                var rnode:IIOMetadataNode = metadata.getAsTree(fn).asInstanceOf[IIOMetadataNode]
                if (i == 0) {
                    if (metadata.isReadOnly()) {
                        metadata = iw.getDefaultImageMetadata(ir.getRawImageType(0), null)
                    }
                    fn match {
                        case "javax_imageio_jpeg_image_1.0" => addCommentForJPEG(rnode, this.comment)
                        case "javax_imageio_png_1.0" => addCommentForPNG(rnode, this.comment)
                        case "javax_imageio_gif_image_1.0" => addCommentForGIF(rnode, this.comment)
                        case _ =>
                    }
                    metadata.setFromTree(fn, rnode)
                }
                img.setMetadata(metadata)
                imgs += img
            }

            if (imgs.size() == 1) {
                iw.write(imgs(0))
            } else {
                iw.prepareWriteSequence(streamMeta)
                imgs.foreach { img =>
                    iw.writeToSequence(img, null)
                }
                iw.endWriteSequence()
            }

        } catch {
            case e:Exception => { throw e }
        } finally {
            if (ir != null) ir.dispose()
            if (iw != null) iw.dispose()
            if (ios != null) ios.flush()
        }
    } 

    private def addCommentForJPEG(rootNode:IIOMetadataNode, comment:String): Unit = {

        // /markerSequence/com@comment="..."

        val MARKER_SEQUENCE = "markerSequence"
        val COM             = "com"
        val COMMENT         = "comment"

        var nl:NodeList = rootNode.getElementsByTagName(MARKER_SEQUENCE)

        if (nl.getLength() == 0) {
            var child0 = new IIOMetadataNode(MARKER_SEQUENCE)
            var child1 = new IIOMetadataNode(COM)
            child1.setAttribute(COMMENT, comment)
            child0.appendChild(child1)
            rootNode.appendChild(child0)
        } else {
            var markerSequence:IIOMetadataNode = nl.item(0).asInstanceOf[IIOMetadataNode]
            nl = markerSequence.getElementsByTagName(COM)
            if (nl.getLength() == 0) {
                var child0 = new IIOMetadataNode(COM)
                child0.setAttribute(COMMENT, comment)
                markerSequence.appendChild(child0)
            } else {
                var child0:IIOMetadataNode = nl.item(0).asInstanceOf[IIOMetadataNode]
                var child1 = new IIOMetadataNode(COM)
                child1.setAttribute(COMMENT, comment)
                markerSequence.replaceChild(child1, child0)
            }
        }
    }

    private def addCommentForGIF(rootNode:IIOMetadataNode, comment:String) = {

        // /CommentExtensions/CommentExtension@value="..."

        val COM_EXTS = "CommentExtensions"
        val COM_EXT  = "CommentExtension"
        val VALUE    = "value"

        var nl:NodeList = rootNode.getElementsByTagName(COM_EXTS)

        if (nl.getLength() == 0) {
            var child0 = new IIOMetadataNode(COM_EXTS)
            var child1 = new IIOMetadataNode(COM_EXT)
            child1.setAttribute(VALUE, comment)
            child0.appendChild(child1)
            rootNode.appendChild(child0)
        } else {
            var ext:IIOMetadataNode  = nl.item(0).asInstanceOf[IIOMetadataNode]
            nl = ext.getElementsByTagName(COM_EXT)
            if (nl.getLength() == 0) {
                var child0 = new IIOMetadataNode(COM_EXT);
                child0.setAttribute(VALUE, comment);
                ext.appendChild(child0)
            } else {
                var child0:IIOMetadataNode = nl.item(0).asInstanceOf[IIOMetadataNode];
                child0.setAttribute(VALUE, comment);
            }
        }
    }

    private def addCommentForPNG(rootNode:IIOMetadataNode, comment:String) {

        // /tExt/tExtEntry@keyword="Copyright"
        // /tExt/tExtEntry@value="...."

        val T_EXT       = "tEXt"
        val T_EXT_ENTRY = "tEXtEntry"
        val KEYWORD     = "keyword"
        val COPYRIGHT   = "Copyright"
        val VALUE       = "value"

        var nl:NodeList = rootNode.getElementsByTagName("tExt")

        if (nl.getLength() == 0) {
            var child0 = new IIOMetadataNode(T_EXT)
            var child1 = new IIOMetadataNode(T_EXT_ENTRY)
            child1.setAttribute(KEYWORD, COPYRIGHT)
            child1.setAttribute(VALUE, comment)
            child0.appendChild(child1)
            rootNode.appendChild(child0)
        } else {
            var ext:IIOMetadataNode = nl.item(0).asInstanceOf[IIOMetadataNode]
            nl = ext.getElementsByTagName(T_EXT_ENTRY)
            val n:Int = nl.getLength()
            var done:Boolean = false
            if (n > 0) {
                for (i <- 0 until n if !done) {
                    var child0 = nl.item(i).asInstanceOf[IIOMetadataNode]
                    var v = child0.getAttribute(KEYWORD)
                    if (Option(v).exists(str => str == COPYRIGHT)) {
                        child0.setAttribute(VALUE, comment)
                        done = true
                    }
                }
            }
            if (!done) {
                var child0 = new IIOMetadataNode(T_EXT_ENTRY)
                child0.setAttribute(KEYWORD, COPYRIGHT)
                child0.setAttribute(VALUE, comment)
                ext.appendChild(child0)
            }
        }
    }
}
