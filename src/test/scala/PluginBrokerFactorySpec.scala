import org.scalatest._

import com.mazzoca.imgconv.ConvertOption
import com.mazzoca.imgconv.plugins._
import com.mazzoca.imgconv.plugins.factory._

class PluginBrokerFactorySpec extends FunSpec {

  describe("DefaultPluginBrokerFactory") {

    it ("create(option: ConvertOption): PluginBroker") {

      var broker: PluginBroker = null

      // Default convert option
      broker = DefaultPluginBrokerFactory.create(new ConvertOption)
      assert(broker.plugins(0).isInstanceOf[ResizePlugin])
      assert(broker.plugins(1).isInstanceOf[StripPlugin])
    }
  }
}
