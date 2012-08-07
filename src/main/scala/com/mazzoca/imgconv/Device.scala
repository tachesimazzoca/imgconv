package com.mazzoca.imgconv

import javax.servlet.http.{HttpServletRequest}

class Device {

    private var _carrierId:Int = 0 
    private var _userAgent:String = "" 

    var displayWidth:Int = 240
    var displayHeight:Int = 320

    def this(userAgent:String) = {
        this
        this.setUserAgent(userAgent)
    }

    def this(request:HttpServletRequest) = {
        this
        this.setUserAgent(Option(request.getHeader("User-Agent")).getOrElse(""))
        if (this.isSoftbank()) {
            Option(request.getHeader("x-jphone-display")).map { str =>
                val regexp = """^([0-9]{1,4})\*([0-9]{1,4})$""".r
                regexp.findFirstIn(str) match {
                    case Some(regexp(m1, m2)) => {
                        this.displayWidth = m1.toInt
                        this.displayHeight = m2.toInt
                    }
                    case None =>
                }
            }
        }
    }

    def getCarrierId():Int = { this._carrierId }

    def isDocomo():Boolean = { this._carrierId == Device.CARRIER_ID_DOCOMO }
    def isAu():Boolean = { this._carrierId == Device.CARRIER_ID_AU }
    def isSoftbank():Boolean = { this._carrierId == Device.CARRIER_ID_SOFTBANK }
    def isWillcom():Boolean = { this._carrierId == Device.CARRIER_ID_WILLCOM }

    def getUserAgent():String = { this._userAgent }
    def setUserAgent(userAgent:String): Device = {

        this._userAgent = userAgent

        val dPtn = """^DoCoMo/\d\.\d[ /].+$""".r
        val aPtn = """^(?:KDDI-[A-Z]+\d+[A-Z]? )?UP\.Browser\/.+$""".r
        val sPtn = """^(?:(?:SoftBank|Vodafone|J-PHONE)/\d\.\d|MOT-).+$""".r
        val wPtn = """^Mozilla/3\.0\((?:DDIPOCKET|WILLCOM);.+$""".r

        this._carrierId = this._userAgent match {
            case dPtn() => Device.CARRIER_ID_DOCOMO
            case aPtn() => Device.CARRIER_ID_AU
            case sPtn() => Device.CARRIER_ID_SOFTBANK
            case wPtn() => Device.CARRIER_ID_WILLCOM
            case _ => Device.CARRIER_ID_UNKNOWN
        }

        this
    }
}

object Device {

    val CARRIER_ID_UNKNOWN = 0
    val CARRIER_ID_DOCOMO = 1
    val CARRIER_ID_AU = 2 
    val CARRIER_ID_SOFTBANK = 3 
    val CARRIER_ID_WILLCOM = 4 

    def apply() = {
        new Device()
    }

    def apply(userAgent:String) = {
        new Device(userAgent)
    }

    def apply(request:HttpServletRequest) = {
        new Device(request)
    }
}
