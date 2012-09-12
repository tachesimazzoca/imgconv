package com.mazzoca.imgconv.plugins

import java.io.{InputStream, OutputStream, IOException}

import javax.imageio.{ImageIO, IIOImage, ImageReader, ImageWriter}
import javax.imageio.stream.{ImageInputStream, ImageOutputStream}
import javax.imageio.metadata.{IIOMetadata, IIOMetadataNode}

import org.w3c.dom.{Node, NodeList}

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

class CommentPlugin extends Plugin {

  var comment = ""

  def execute(input: InputStream, output: OutputStream) {

    var ir: ImageReader = null
    var iw: ImageWriter = null
    var ios: ImageOutputStream = null 

    try {

      ImageIO.setUseCache(false)

      val iis: ImageInputStream = ImageIO.createImageInputStream(input)
      Option(ImageIO.getImageReaders(iis)) map { readers =>
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

      var imgs: ArrayBuffer[IIOImage] = ArrayBuffer[IIOImage]() 

      var ni: Int = ir.getNumImages(true)
      for (i <- 0 until ni) {
        Option(ir.readAll(i, null)) map { img: IIOImage =>
          var metadata: IIOMetadata = ir.getImageMetadata(i)
          val fn: String = metadata.getNativeMetadataFormatName()
          var rnode: IIOMetadataNode = metadata.getAsTree(fn).asInstanceOf[IIOMetadataNode]
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
      }

      if (imgs.size() == 1) {
        iw.write(imgs(0))
      } else {
        iw.prepareWriteSequence(ir.getStreamMetadata())
        imgs foreach { iw.writeToSequence(_, null) }
        iw.endWriteSequence()
      }

    } catch {
      case (e: Exception) => { throw e }
    } finally {
      Option(ir) map { _.dispose() }
      Option(iw) map { _.dispose() }
      Option(ios) map { _.flush() }
    }
  } 

  private def addCommentForJPEG(rootNode: IIOMetadataNode, comment: String) {

    // /markerSequence/com@comment="..."

    val JPEG_N_MARKER_SEQUENCE = "markerSequence"
    val JPEG_N_COM = "com"
    val JPEG_K_COMMENT = "comment"

    var nl: NodeList = rootNode.getElementsByTagName(JPEG_N_MARKER_SEQUENCE)

    if (nl.getLength() == 0) {

      rootNode.appendChild(
        new IIOMetadataNode(JPEG_N_MARKER_SEQUENCE) {
          this.appendChild(new IIOMetadataNode(JPEG_N_COM) { this.setAttribute(JPEG_K_COMMENT, comment) })
        })

    } else {

      val markerSequence: IIOMetadataNode = nl.item(0).asInstanceOf[IIOMetadataNode]
      nl = markerSequence.getElementsByTagName(JPEG_N_COM)

      if (nl.getLength() == 0) {
        markerSequence.appendChild(new IIOMetadataNode(JPEG_N_COM) { this.setAttribute(JPEG_K_COMMENT, comment) })
      } else {
        markerSequence.replaceChild(
          new IIOMetadataNode(JPEG_N_COM) { this.setAttribute(JPEG_K_COMMENT, comment) },
          nl.item(0).asInstanceOf[IIOMetadataNode])
      }
    }
  }

  private def addCommentForGIF(rootNode: IIOMetadataNode, comment: String) {

    // /CommentExtensions/CommentExtension@value="..."

    val GIF_N_COMMENT_EXTS = "CommentExtensions"
    val GIF_N_COMMENT_EXT  = "CommentExtension"
    val GIF_K_VALUE = "value"

    var nl: NodeList = rootNode.getElementsByTagName(GIF_N_COMMENT_EXTS)

    if (nl.getLength() == 0) {

      rootNode.appendChild(
        new IIOMetadataNode(GIF_N_COMMENT_EXTS) {
          this.appendChild(new IIOMetadataNode(GIF_N_COMMENT_EXT) { this.setAttribute(GIF_K_VALUE, comment) })
        })

    } else {

      val exts: IIOMetadataNode = nl.item(0).asInstanceOf[IIOMetadataNode]
      nl = exts.getElementsByTagName(GIF_N_COMMENT_EXT)

      if (nl.getLength() == 0) {
        exts.appendChild(new IIOMetadataNode(GIF_N_COMMENT_EXT) { this.setAttribute(GIF_K_VALUE, comment) })
      } else {
        nl.item(0).asInstanceOf[IIOMetadataNode].setAttribute(GIF_K_VALUE, comment)
      }
    }
  }

  private def addCommentForPNG(rootNode: IIOMetadataNode, comment: String) {

    // /tExt/tExtEntry@keyword="Copyright"
    // /tExt/tExtEntry@value="...."

    val PNG_N_T_EXT = "tEXt"
    val PNG_N_T_EXT_ENTRY = "tEXtEntry"
    val PNG_K_KEYWORD = "keyword"
    val PNG_V_KEYWORD = "Copyright"
    val PNG_K_VALUE = "value"

    var nl: NodeList = rootNode.getElementsByTagName(PNG_N_T_EXT)

    if (nl.getLength() == 0) {

      rootNode.appendChild(
        new IIOMetadataNode(PNG_N_T_EXT) {
          this.appendChild(
            new IIOMetadataNode(PNG_N_T_EXT_ENTRY) {
              this.setAttribute(PNG_K_KEYWORD, PNG_V_KEYWORD)
              this.setAttribute(PNG_K_VALUE, comment)
            })
        })

    } else {

      val ext: IIOMetadataNode = nl.item(0).asInstanceOf[IIOMetadataNode]
      nl = ext.getElementsByTagName(PNG_N_T_EXT_ENTRY)

      val n: Int = nl.getLength()
      var done = false
      if (n > 0) {
        for (i <- 0 until n if !done) {
          val child0 = nl.item(i).asInstanceOf[IIOMetadataNode]
          if (Option(child0.getAttribute(PNG_K_KEYWORD)).exists(str => str == PNG_V_KEYWORD)) {
            child0.setAttribute(PNG_K_VALUE, comment)
            done = true
          }
        }
      }
      if (!done) {
        ext.appendChild(
          new IIOMetadataNode(PNG_N_T_EXT_ENTRY) {
            this.setAttribute(PNG_K_KEYWORD, PNG_V_KEYWORD)
            this.setAttribute(PNG_K_VALUE, comment)
          })
      }
    }
  }
}
