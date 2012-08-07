package com.mazzoca.imgconv.plugins.factory

import scala.collection.mutable.HashMap 

import com.mazzoca.imgconv.Device
import com.mazzoca.imgconv.ConvertOption
import com.mazzoca.imgconv.plugins._

object DefaultPluginBrokerFactory extends PluginBrokerFactory {

    def create(option:ConvertOption): PluginBroker = {

        val broker = new PluginBroker()

        Option(option.formatName).map { s =>
            broker.plugins += new ReformatPlugin() { this.formatName = s }
        }

        val pin = new ResizePlugin()
        val sizePattern = """^([0-9]+)(p)?(w|h)$""".r
        val fitPattern = """^([0-9]+)?(p)?(fit|fitIfLarge)$""".r
        option.params.get("size").getOrElse("") match {
            case sizePattern(d, p, wh) => {
                pin.geometry = true
                pin.fit = false 
                val n = d.toInt
                if (wh == "w") {
                    pin.width = if (p == "p") (option.device.displayWidth * n / 100) else n 
                    pin.height = 0 
                } else {
                    pin.width = 0 
                    pin.height = if (p == "p") (option.device.displayHeight * n / 100) else n 
                }
            }
            case fitPattern(d, p, ft) => {
                pin.geometry = true
                pin.fit = (ft == "fit")
                var dn = Option(d).getOrElse("0").toInt
                if (option.device.displayWidth < option.device.displayHeight) {
                    if (p == null) {
                        pin.width = if (dn == 0) option.device.displayWidth else dn
                    } else {
                        pin.width = (option.device.displayWidth * dn / 100).floor.asInstanceOf[Int]
                    }
                    pin.height = 0 
                } else {
                    pin.width = 0 
                    if (p == null) {
                        pin.height = if (dn == 0) option.device.displayHeight else dn
                    } else {
                        pin.height = (option.device.displayHeight * dn / 100).floor.asInstanceOf[Int]
                    }
                }
            }
            case _ => {
                pin.geometry = true
                pin.fit = false 
                pin.width = option.device.displayWidth
                pin.height = 0 
            }
        }
        broker.plugins += pin

        broker.plugins += new StripPlugin() 

        if (option.copyright) {
            broker.plugins += new CommentPlugin() {
                this.comment = option.device.getCarrierId() match {
                    case Device.CARRIER_ID_DOCOMO => "copy=\"NO\""
                    case Device.CARRIER_ID_AU => "kddi_copyright=on"
                    case _ => "kddi_copyright=on,copy=\"NO\""
                }
            }
        }

        broker
    }
}
