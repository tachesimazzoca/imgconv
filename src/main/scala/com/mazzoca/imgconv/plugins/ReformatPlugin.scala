package com.mazzoca.imgconv.plugins

import java.io.{InputStream, OutputStream, IOException}

import java.awt.image.BufferedImage

import javax.imageio.{IIOImage, ImageIO, ImageReader, ImageWriter, ImageTypeSpecifier}
import javax.imageio.stream.{ImageInputStream, ImageOutputStream}
import javax.imageio.metadata.IIOMetadata

import scala.collection.JavaConversions._

class ReformatPlugin extends Plugin {

  var formatName = "" 

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

      Option(ImageIO.getImageWritersByFormatName(this.formatName)) map { writers =>
        if (writers.hasNext()) {
          iw = writers.next()
        }
      }
      if (iw == null) {
        throw new IOException()
      }
      ios = ImageIO.createImageOutputStream(output)
      iw.setOutput(ios)

      iw.write(new IIOImage(ir.read(0), null, null))

    } catch {
      case (e: Exception) => { throw e }
    } finally {
      Option(ir) map { _.dispose() }
      Option(iw) map { _.dispose() }
      Option(ios) map { _.flush() }
    }
  }
}
