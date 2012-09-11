package com.mazzoca.imgconv.plugins

import java.io.{
  ByteArrayInputStream, ByteArrayOutputStream,
  InputStream, OutputStream, IOException,
  PrintWriter
}

import javax.imageio.{IIOImage, ImageIO, ImageReader}
import javax.imageio.stream.ImageInputStream
import javax.imageio.metadata.IIOMetadata

import org.w3c.dom.Node

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

class DumpPlugin extends Plugin {

  var writer: Option[PrintWriter] = None 

  def execute(input: InputStream, output: OutputStream) = {

    var baos = new ByteArrayOutputStream()

    var buf: Array[Byte] = new Array[Byte](4096) 
    try {
      var n: Int = 0
      while ({n = input.read(buf, 0, 4096); n > 0}) {
        output.write(buf, 0, n)
        baos.write(buf, 0, n)
      }
    } catch {
      case (e: Exception) => { throw e }
    }

    var ir: ImageReader = null

    try {

      ImageIO.setUseCache(false)

      val iis: ImageInputStream =
        ImageIO.createImageInputStream(new ByteArrayInputStream(baos.toByteArray()))
      Option(ImageIO.getImageReaders(iis)) map { readers =>
        if (readers.hasNext()) {
          ir = readers.next()
          ir.setInput(iis)
        }
      }
      if (ir == null) {
        throw new IOException()
      }

      var ni: Int = ir.getNumImages(true)
      for (i <- 0 until ni) {
        val meta: IIOMetadata = ir.getImageMetadata(i)
        Option(meta.getMetadataFormatNames()) map { formatNames =>
          formatNames.foreach { formatName =>
            this.displayNode(meta.getAsTree(formatName), 0)
          }
        }
      }

    } catch {
      case (e: Exception) => { throw e }
    } finally {
      Option(ir) map { _.dispose() }
      Option(baos) map { _.close() }
    }
  } 

  private def displayNode(node: Node, depth: Int): Unit = {

    val indent = " " * depth

    this.writer map { _.println(indent + node.getNodeName()) }

    Option(node.getAttributes()) map { attrs =>
      val c = attrs.getLength()
      for (i <- 0 until c) {
        val attr: Node = attrs.item(i)
        this.writer map { _.println(indent + " - " + attr.getNodeName() + ": " + attr.getNodeValue()) }
      }
    }
    var child = node.getFirstChild()
    while (child != null) {
      displayNode(child, depth + 1)
      child = child.getNextSibling()
    }
  }
}
