package com.mazzoca.imgconv

import java.io.{File, FileOutputStream, InputStream}

import org.apache.http.{HttpResponse, HttpEntity, Header}
import org.apache.http.client.methods.{HttpHead, HttpGet}
import org.apache.http.impl.client.DefaultHttpClient

class URLFetcher {

  var baseurl = "" 
  var datadir = "" 

  private var _statusCode = 0 
  private var _message = "" 

  def statusCode: Int = this._statusCode
  def message: String = this._message

  def fetch(path: String, timestamp: Long): Option[File] = {

    val BUFFER_SIZE = 4096

    this._statusCode = 403 
    this._message = "" 

    var source = new File(this.datadir + path)
    if (source.exists()) {
      if (source.lastModified() >= timestamp) {
        //println("Use cache. " + source.getPath())
        return Some(source)
      }
    }

    try {
      val dir: File = source.getParentFile()
      if (!dir.exists() && !dir.mkdirs()) {
        this._message = "mkdirs() failed - " + dir.getPath()
        source = null
      }
    } catch {
      case (e: Exception) => {
        source = null
        this._message = e.getMessage()
      }
    }

    if (source == null) {
      return None 
    }

    val httpGet = new HttpGet(this.baseurl + path)

    try {

      val hc = new DefaultHttpClient()
      val httpResponse: HttpResponse = hc.execute(httpGet)

      httpResponse.getStatusLine().getStatusCode() match {

        case 200 => {

          this._statusCode = 200

          Option(httpResponse.getEntity()) map { entity: HttpEntity => 

            var is: InputStream = null 
            var os: FileOutputStream = null 
            var bytes = new Array[Byte](BUFFER_SIZE) 

            try {
              is = entity.getContent()
              os = new FileOutputStream(source) 
              var n = 0 
              while ({n = is.read(bytes, 0, BUFFER_SIZE); n > 0}) { os.write(bytes, 0, n) }

            } catch {
              case (e: Exception) => {
                source = null
                this._message = e.getMessage() 
              }

            } finally {
              Option(is) map { _.close() }
              Option(os) map { _.close() }
            }
          }
        }

        case sc => {
          this._statusCode = sc 
          source = null
        }
      }

    } catch {
      case (e: Exception) => {
        source = null
        this._message = e.getMessage()
      }

    } finally {
      httpGet.abort()
    }

    Option(source)
  }
}
