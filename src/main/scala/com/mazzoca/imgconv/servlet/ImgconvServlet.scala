package com.mazzoca.imgconv.servlet

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import javax.servlet.{ServletConfig, ServletOutputStream}

import java.io.{File, FileInputStream, ByteArrayInputStream, ByteArrayOutputStream}
import java.util.{Date, Properties}

import com.mazzoca.imgconv.URLFetcher
import com.mazzoca.imgconv.ConvertOption
import com.mazzoca.imgconv.plugins.factory.DefaultPluginBrokerFactory

import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap 

class ImgconvServlet extends HttpServlet {

    val params = new HashMap[String, String]
    val backends = new HashMap[String, Properties]

    override def init(): Unit = {
        Option(super.getServletConfig()).map { cfg:ServletConfig =>
            cfg.getInitParameterNames().foreach { nm =>
                this.params(nm.asInstanceOf[String]) = cfg.getInitParameter(nm.asInstanceOf[String])
            }
        }
    }

    override def doGet(request:HttpServletRequest, response:HttpServletResponse): Unit = {

        val ptn = """^/([^/]+)(/.+)\.(jpz|jpg|png|pnz|gif)$""".r
        val (validURL, noTransfer, backendName, filename, suffix) = ptn.findFirstIn(request.getPathInfo()) match {
            case Some(ptn(m1, m2, m3)) => {
                (true, m3.endsWith("z"), m1, m2, (if (m3.endsWith("z")) m3.init + "g" else m3)) 
            }
            case None => (false, false, null, null, null)
        }
        if (!validURL) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN)
            return
        }

        // Load a backend configuration. 

        var prop:Properties = this.backends.get(backendName) match {
            case Some(pr) => pr
            case None => {
                val pr = new Properties()
                var fis:FileInputStream = null
                try {
                    fis = new FileInputStream(this.params.getOrElse("conf.backend", "conf/backend") + "/" + backendName + ".properties")
                    pr.load(fis)
                } catch {
                    case e:Exception => {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN)
                        return
                    }
                } finally {
                    if (fis != null) fis.close()
                }
                this.backends(backendName) = pr
                pr
            }
        }

        // Fetch a image source.

        val uf = new URLFetcher()
        uf.baseurl = prop.getProperty("baseurl")
        uf.datadir = prop.getProperty("datadir")
        if (!uf.datadir.startsWith("/")) {
            uf.datadir = System.getProperty("java.io.tmpdir") + "/" +  uf.datadir
        }
        val maxage:Long = Option(prop.getProperty("maxage")).getOrElse("60").toLong * 1000
        val formats = suffix match {
            case "gif" => Array("gif", "jpg", "png")
            case "png" => Array("png", "jpg", "gif")
            case _ => Array("jpg", "gif", "png")
        }
        var source:File = null 
        for (fm <- formats if source == null) {
            source = uf.fetch(filename + "." + fm, new Date().getTime() - maxage)
        }
        if (source == null) {
            response.sendError(uf.statusCode())
            return
        }

        // Convert the image.

        val cvopt = new ConvertOption() 
        cvopt.formatName = suffix
        cvopt.copyright = noTransfer || Option(request.getParameter("copyright")).getOrElse("no") == "yes"
        request.getParameterNames().foreach { key =>
            val values:Array[String] = request.getParameterValues(key.asInstanceOf[String]).asInstanceOf[Array[String]]
            if (values.size > 0) {
                cvopt.params(key.asInstanceOf[String]) = values(0)
            }
        }

        var baos:ByteArrayOutputStream = new ByteArrayOutputStream() 

        try {
            DefaultPluginBrokerFactory.create(cvopt).execute(new FileInputStream(source), baos)
        } catch {
            case e:Exception => {
                e.printStackTrace()
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                return
            }
        } finally {
            cvopt.params.clear()
        }

        // Send response.

        val bytes:Array[Byte] = baos.toByteArray()
        if (bytes.length == 0) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            return
        }

        cvopt.formatName match {
            case "jpg" => response.setContentType("image/jpeg")
            case "gif" => response.setContentType("image/gif")
            case "png" => response.setContentType("image/png")
            case _ => response.setContentType("application/octet-stream")
        }

        response.setHeader("Content-Length", bytes.size.toString())

        val BUFFER_SIZE = 4096
        var is:ByteArrayInputStream = null
        var os:ServletOutputStream = null
        var buf:Array[Byte] = new Array[Byte](BUFFER_SIZE) 
        try {
            is = new ByteArrayInputStream(bytes) 
            os = response.getOutputStream() 
            var n:Int = 0 
            while ({n = is.read(buf, 0, BUFFER_SIZE); n > 0}) {
                os.write(buf, 0, n)
            }
        } catch {
            case e:Exception => { e.printStackTrace() }
        } finally {
            if (is != null) is.close()
            if (os != null) os.close()
        }
    }
}
