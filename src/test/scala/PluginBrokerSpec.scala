import org.scalatest._

import java.io._

import com.mazzoca.imgconv.plugins._

class PluginBrokerSpec extends FunSpec {

  describe("PluginBroker") {
    it ("execute empty input.") {
      val broker = new PluginBroker()
      for (i <- 0 until 2) {
        var bais = new ByteArrayInputStream(Array[Byte]())
        var baos = new ByteArrayOutputStream()
        broker.execute(bais, baos) 
        val bytes:Array[Byte] = baos.toByteArray()
        assert(bytes.length == 0)
      }
    }
    it ("execute empty plugins.") {
      val broker = new PluginBroker()
      for (i <- 0 until 2) {
        var bais = new ByteArrayInputStream(Array[Byte](1, 2, 3))
        var baos = new ByteArrayOutputStream()
        broker.execute(bais, baos) 
        val bytes:Array[Byte] = baos.toByteArray()
        assert(bytes.sameElements(Array[Byte](1, 2, 3)))
      }
    }
  }
}
