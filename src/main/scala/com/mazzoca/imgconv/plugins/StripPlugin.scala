package com.mazzoca.imgconv.plugins

import java.io.{InputStream, OutputStream, IOException}
import java.awt.image.{BufferedImage}

import javax.imageio.{ImageIO, IIOImage, ImageReader, ImageWriter}
import javax.imageio.stream.{ImageInputStream, ImageOutputStream}
import javax.imageio.metadata.{IIOMetadata}

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

class StripPlugin extends Plugin {

  def execute(input: InputStream, output: OutputStream) = {

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
      if (ir == null) throw new IOException()

      iw = ImageIO.getImageWriter(ir)
      if (iw == null) throw new IOException()

      ios = ImageIO.createImageOutputStream(output)
      iw.setOutput(ios)

      val imgs: ArrayBuffer[IIOImage] = ArrayBuffer[IIOImage]() 

      val ni: Int = ir.getNumImages(true)
      for (i <- 0 until ni) {
        Option(ir.readAll(i, null)) map { img =>
          img.setMetadata(null)
          img.setThumbnails(null)
          imgs += img 
        }
      }

      if (imgs.size() == 1) {
        iw.write(imgs(0))
      } else {
        iw.prepareWriteSequence(ir.getStreamMetadata())
        imgs.foreach { img =>
          iw.writeToSequence(img, null)
        }
        iw.endWriteSequence()
      }

    } catch {
      case (e: Exception) => { throw e }
    } finally {
      Option(ir) map { _.dispose() }
      Option(iw) map { _.dispose() }
      Option(ios) map {  _.flush() }
    }
  } 
}
