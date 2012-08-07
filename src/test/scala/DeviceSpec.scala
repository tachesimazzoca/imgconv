import org.scalatest._

import com.mazzoca.imgconv.Device
import scala.collection.mutable.HashMap 

class DeviceSpec extends FunSpec {

    describe("Device") {

        it ("Device#setUserAgent(userAgent:String) - parse carrierID with User-Agent string.") {

            val device = new Device()

            device.setUserAgent("DoCoMo/2.0 P903i(c100;TB;W24H12)")
            assert(device.isDocomo())
            assert(device.getCarrierId() == Device.CARRIER_ID_DOCOMO)

            device.setUserAgent("KDDI-HI3D UP.Browser/6.2_7.2.7.1.K.2.234 (GUI) MMP/2.0")
            assert(device.isAu())
            assert(device.getCarrierId() == Device.CARRIER_ID_AU)

            device.setUserAgent("SoftBank/1.0/831SH/SHJ003/SN123456789012345 "
                    + "Browser/NetFront/3.5 Profile/MIDP-2.0 Configuration/CLDC-1.1")
            assert(device.isSoftbank())
            assert(device.getCarrierId() == Device.CARRIER_ID_SOFTBANK)

            device.setUserAgent("Vodafone/1.0/V705SH/SHJ001/SN123456789012345 "
                    + "Browser/VF-NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1")
            assert(device.isSoftbank())
            assert(device.getCarrierId() == Device.CARRIER_ID_SOFTBANK)

            device.setUserAgent("J-PHONE/3.0/J-SH10")
            assert(device.isSoftbank())
            assert(device.getCarrierId() == Device.CARRIER_ID_SOFTBANK)

            device.setUserAgent("MOT-V980/80.2F.2E. MIB/2.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1")
            assert(device.isSoftbank())
            assert(device.getCarrierId() == Device.CARRIER_ID_SOFTBANK)

            device.setUserAgent("Mozilla/3.0(WILLCOM;KYOCERA/WX340K/2;3.0.3.11.000000/1/C256) NetFront/3.4")
            assert(device.isWillcom())
            assert(device.getCarrierId() == Device.CARRIER_ID_WILLCOM)
        }
    }
}
