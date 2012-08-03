package com.mazzoca.imgconv

import java.io.{File, InputStream, FileOutputStream}

import org.apache.http.{HttpResponse, HttpEntity, Header}
import org.apache.http.client.methods.{HttpHead, HttpGet}
import org.apache.http.impl.client.{DefaultHttpClient}

class URLFetcher {

    var baseurl = "" 
    var datadir = "" 

    private var _statusCode = 0 
    private var _message = "" 

    def statusCode():Int = { _statusCode }
    def message():String = { _message }

    def fetch(path:String, timestamp:Long): File = {

        val BUFFER_SIZE = 4096

        this._statusCode = 403 
        this._message = "" 

        var source = new File(this.datadir + path)
        if (source.exists()) {
            if (source.lastModified() >= timestamp) {
                //println("Use cache. " + source.getPath())
                return source
            }
        }

        try {
            val dir:File = source.getParentFile()
            if (!dir.exists() && !dir.mkdirs()) {
                this._message = "mkdirs() failed - " + dir.getPath()
                source = null
            }
        } catch {
            case e:Exception => {
                source = null
                this._message = e.getMessage()
            }
        }

        if (source == null) {
            return source
        }

        val httpGet = new HttpGet(this.baseurl + path)

        try {
            val hc = new DefaultHttpClient()
            val httpResponse:HttpResponse = hc.execute(httpGet)
            httpResponse.getStatusLine().getStatusCode() match {
                case 200 => {
                    this._statusCode = 200
                    Option(httpResponse.getEntity()).map { entity:HttpEntity => 
                        var is:InputStream = null 
                        var os:FileOutputStream = null 
                        var bytes:Array[Byte] = new Array[Byte](BUFFER_SIZE) 
                        try {
                            is = entity.getContent()
                            os = new FileOutputStream(source) 
                            var n:Int = 0 
                            while ({n = is.read(bytes, 0, BUFFER_SIZE); n > 0}) {
                                os.write(bytes, 0, n)
                            }
                        } catch {
                            case e:Exception => {
                                source = null
                                this._message = e.getMessage() 
                            }
                        } finally {
                            if (is != null) is.close()
                            if (os != null) os.close()
                        }
                    }
                }
                case sc => {
                    source = null
                    this._statusCode = sc 
                }
            }
        } catch {
            case e:Exception => {
                source = null
                this._message = e.getMessage()
            }
        } finally {
            httpGet.abort()
        }

        source 
    }
}
