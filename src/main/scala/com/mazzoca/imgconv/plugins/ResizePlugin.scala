package com.mazzoca.imgconv.plugins

import java.io.{InputStream, OutputStream, IOException}

import java.awt.{Color, Graphics2D, RenderingHints}
import java.awt.image.{BufferedImage, IndexColorModel}

import javax.imageio.{IIOImage, ImageIO, ImageReader, ImageWriter}
import javax.imageio.stream.{ImageInputStream, ImageOutputStream}
import javax.imageio.metadata.IIOMetadata

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

class ResizePlugin extends Plugin {

  var width = 0 
  var height = 0 
  var fit = true
  var geometry = true

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

      val ni: Int = ir.getNumImages(true)
      for (i <- 0 until ni) {

        val bimg: BufferedImage = ir.read(i)

        val biw: Int = bimg.getWidth() 
        val bih: Int = bimg.getHeight() 
        var w: Int = this.width
        var h: Int = this.height

        if (w == 0) {
          w = (biw * h / bih).floor.asInstanceOf[Int]
        }
        if (h == 0) {
          h = (bih * w / biw).floor.asInstanceOf[Int]
        }

        if (this.geometry) {
          if (w > biw) {
            w = biw
            h = (bih * w / biw).floor.asInstanceOf[Int]
          }
          if (h > bih) {
            h = bih
            w = (biw * h / bih).floor.asInstanceOf[Int]
          }
        }

        if (this.fit) {
          if (w < this.width) {
            w = this.width
            h = (bih * w / biw).floor.asInstanceOf[Int]
          }
          if (h < this.height) {
            h = this.height
            w = (biw * h / bih).floor.asInstanceOf[Int]
          }
        }

        if (w != bimg.getWidth() || h != bimg.getHeight()) {

          val rbimg: BufferedImage = bimg.getColorModel() match {
            case (icm: IndexColorModel) => new BufferedImage(w, h, bimg.getType(), icm)
            case _ => new BufferedImage(w, h, bimg.getType())
          }

          if (rbimg.getColorModel().hasAlpha() && rbimg.getColorModel().isInstanceOf[IndexColorModel]) {
            val tp: Int = rbimg.getColorModel().asInstanceOf[IndexColorModel].getTransparentPixel()
            for (x <- 0 until w; y <- 0 until h) {
              rbimg.setRGB(x, y, tp)
            }
          }

          val g2d: Graphics2D = rbimg.createGraphics()
          g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
          g2d.drawImage(bimg, 0, 0, w, h, null)
          g2d.dispose()

          imgs += new IIOImage(rbimg, null, null) 

        } else {
          imgs += new IIOImage(bimg, null, null)
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
}
