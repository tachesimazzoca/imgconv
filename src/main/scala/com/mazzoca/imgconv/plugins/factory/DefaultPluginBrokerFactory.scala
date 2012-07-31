package com.mazzoca.imgconv.plugins.factory

import scala.collection.mutable.HashMap 

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
                    pin.width = if (p == "p") (option.displayWidth * n / 100) else n 
                    pin.height = 0 
                } else {
                    pin.width = 0 
                    pin.height = if (p == "p") (option.displayHeight * n / 100) else n 
                }
            }
            case fitPattern(d, p, ft) => {
                pin.geometry = true
                pin.fit = (ft == "fit")
                var dn = Option(d).getOrElse("0").toInt
                if (option.displayWidth < option.displayHeight) {
                    if (p == null) {
                        pin.width = if (dn == 0) option.displayWidth else dn
                    } else {
                        pin.width = (option.displayWidth * dn / 100).floor.asInstanceOf[Int]
                    }
                    pin.height = 0 
                } else {
                    pin.width = 0 
                    if (p == null) {
                        pin.height = if (dn == 0) option.displayHeight else dn
                    } else {
                        pin.height = (option.displayHeight * dn / 100).floor.asInstanceOf[Int]
                    }
                }
            }
            case _ => {
                pin.geometry = true
                pin.fit = false 
                pin.width = option.displayWidth
                pin.height = 0 
            }
        }
        broker.plugins += pin

        broker.plugins += new StripPlugin() 

        if (option.copyright) {
            broker.plugins += new CopyrightPlugin() 
        }

        broker
    }
}
