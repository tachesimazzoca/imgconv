import org.scalatest._

import java.io._

import com.mazzoca.imgconv.plugins.DumpPlugin

class DumpPluginSpec extends FunSpec {

  describe("DumpPlugin") {

    it ("execute(input: InputStream, output: OutputStream)") {

      var bytes: Array[Byte] = Array(
        0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00,
        0x01, 0x00, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x21, 0xf9, 0x04, 0x01, 0x00,
        0x00, 0x00, 0x00, 0x2c, 0x00, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x01, 0x00, 0x00, 0x02, 0x02, 0x44,
        0x01, 0x00, 0x3b) map { _.toByte }

      withCloseable(
        Array(
          new ByteArrayInputStream(bytes),
          new ByteArrayOutputStream(),
          new ByteArrayOutputStream())
      ) { args =>

        val is = args(0).asInstanceOf[ByteArrayInputStream]
        val os = args(1).asInstanceOf[ByteArrayOutputStream]
        val baos = args(2).asInstanceOf[ByteArrayOutputStream]

        val plugin = new DumpPlugin

        plugin.writer = Some(new PrintWriter(baos))
        plugin.execute(is, os)
        plugin.writer map { _.close() }

        val expect = """javax_imageio_gif_image_1.0
 ImageDescriptor
  - imageLeftPosition: 0
  - imageTopPosition: 0
  - imageWidth: 1
  - imageHeight: 1
  - interlaceFlag: FALSE
 GraphicControlExtension
  - disposalMethod: none
  - userInputFlag: FALSE
  - transparentColorFlag: TRUE
  - delayTime: 0
  - transparentColorIndex: 0
javax_imageio_1.0
 Chroma
  ColorSpaceType
   - name: RGB
  NumChannels
   - value: 4
  BlackIsZero
   - value: TRUE
 Compression
  CompressionTypeName
   - value: lzw
  Lossless
   - value: TRUE
  NumProgressiveScans
   - value: 1
 Data
  SampleFormat
   - value: Index
 Dimension
  ImageOrientation
   - value: Normal
  HorizontalPixelOffset
   - value: 0
  VerticalPixelOffset
   - value: 0
 Transparency
  TransparentIndex
   - value: 0
"""
        assert(baos.toString == expect)
        assert(os.toByteArray.sameElements(bytes))
      }
    }
  }

  def withCloseable(args: Array[Closeable])(f: (Array[Closeable]) => Unit) {
    try {
      f(args)
    } finally {
      args map { _.close }
    }
  }
}
