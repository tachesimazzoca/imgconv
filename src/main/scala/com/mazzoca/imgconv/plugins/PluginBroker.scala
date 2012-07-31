package com.mazzoca.imgconv.plugins

import java.io._

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

class PluginBroker {

    var plugins:ArrayBuffer[Plugin] = ArrayBuffer[Plugin]()
    var debug:Boolean = false

    def execute(input:InputStream, output:OutputStream) = {

        var bais:ByteArrayInputStream = null
        var baos = new ByteArrayOutputStream()
        var buf:Array[Byte] = new Array[Byte](4096) 

        try {
            var n:Int = 0
            while ({n = input.read(buf, 0, 4096); n > 0}) {
                baos.write(buf, 0, n)
            }
            bais = new ByteArrayInputStream(baos.toByteArray())
            if (this.debug) {
                println(baos.size + " byte(s) transferred to initial input stream.")
            }
            baos = new ByteArrayOutputStream()
        } catch {
            case e:Exception => { throw e }
        }

        try {

            this.plugins.foreach { plugin =>

                if (this.debug) {
                    println("plugin: " + plugin)
                }
                plugin.execute(bais, baos)

                bais = new ByteArrayInputStream(baos.toByteArray())
                if (this.debug) {
                    println(baos.size + " byte(s) transferred to next input stream.")
                }
                baos = new ByteArrayOutputStream()
            }

            try {
                var n:Int = 0
                while ({n = bais.read(buf, 0, 4096); n > 0}) {
                    output.write(buf, 0, n)
                }
            } catch {
                case e:Exception => { throw e }
            }

        } catch {
            case e:Exception => { throw e }
        } finally {
            if (bais != null) bais.close()
            if (baos != null) baos.close()
        }
    } 
}
