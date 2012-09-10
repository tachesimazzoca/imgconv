import org.scalatest._

import scala.collection.mutable.HashMap 

import com.mazzoca.imgconv.Device

class DeviceSpec extends FunSpec {

  describe("Device") {

    it ("Device#loadUserAgent(userAgent:String) - parse carrierID with User-Agent string.") {

      val device = Device()

      device.loadUserAgent("DoCoMo/2.0 P903i(c100;TB;W24H12)")
      assert(device.isDocomo)
      assert(device.carrierId == Device.CARRIER_ID_DOCOMO)

      device.loadUserAgent("KDDI-HI3D UP.Browser/6.2_7.2.7.1.K.2.234 (GUI) MMP/2.0")
      assert(device.isAu)
      assert(device.carrierId == Device.CARRIER_ID_AU)

      device.loadUserAgent("SoftBank/1.0/831SH/SHJ003/SN123456789012345 "
              + "Browser/NetFront/3.5 Profile/MIDP-2.0 Configuration/CLDC-1.1")
      assert(device.isSoftbank)
      assert(device.carrierId == Device.CARRIER_ID_SOFTBANK)

      device.loadUserAgent("Vodafone/1.0/V705SH/SHJ001/SN123456789012345 "
              + "Browser/VF-NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1")
      assert(device.isSoftbank)
      assert(device.carrierId == Device.CARRIER_ID_SOFTBANK)

      device.loadUserAgent("J-PHONE/3.0/J-SH10")
      assert(device.isSoftbank)
      assert(device.carrierId == Device.CARRIER_ID_SOFTBANK)

      device.loadUserAgent("MOT-V980/80.2F.2E. MIB/2.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1")
      assert(device.isSoftbank)
      assert(device.carrierId == Device.CARRIER_ID_SOFTBANK)

      device.loadUserAgent("Mozilla/3.0(WILLCOM;KYOCERA/WX340K/2;3.0.3.11.000000/1/C256) NetFront/3.4")
      assert(device.isWillcom)
      assert(device.carrierId == Device.CARRIER_ID_WILLCOM)
    }
  }
}
