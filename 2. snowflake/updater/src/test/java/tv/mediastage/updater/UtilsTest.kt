package tv.mediastage.updater

import org.junit.Assert
import org.junit.Test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.any
import org.junit.Before
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections

class UtilsTest {
    companion object {
        val IP_ADDR_RES = "192.168.53.88"
        val MAC_ADDR_RES = "94:A1:A2:DB:33:D5"
        val HARDWARE_ADDRESS = MAC_ADDR_RES.convertToByteArray()

        private fun String.convertToByteArray() : ByteArray {
            val res = ArrayList<Byte>()
            this.split(":").forEach{
                res.add(it.toUByte(radix = 16).toByte())
            }
            return res.toByteArray()
        }
    }

    private val mockUtils: Utils = mock()
    private val networkInterface: NetworkInterface = mock()
    private val inetAddress: InetAddress = mock()
    private val inetAddressEmpty: InetAddress = mock()

    @Before
    fun setUp(){
        whenever(mockUtils.getMACAddress()).thenCallRealMethod()
        whenever(mockUtils.getIPAddress(true)).thenCallRealMethod()
        whenever(mockUtils.needUpdate(any(), any())).thenCallRealMethod()
    }

    @Test
    fun testNeedUpdate() {
        Assert.assertTrue(mockUtils.needUpdate("5.23.209", "6.01.211"))
        Assert.assertTrue(mockUtils.needUpdate("5.23.209", "5.24.000"))
        Assert.assertTrue(mockUtils.needUpdate("5.23.209", "5.23.211"))

        Assert.assertFalse(mockUtils.needUpdate("5.23.209", "4.99.211"))
        Assert.assertFalse(mockUtils.needUpdate("5.23.209", "5.-1.000"))
        Assert.assertFalse(mockUtils.needUpdate("5.23.209", "5.23.208"))

        Assert.assertFalse(mockUtils.needUpdate("5.23.209", "bla-bla-bla"))
        Assert.assertTrue(mockUtils.needUpdate("5.23.209", "6"))
        Assert.assertFalse(mockUtils.needUpdate("5.23.209", "5.24-alpha"))
    }

    @Test
    fun testEmptyNetworkInterfaceList() {
        whenever(mockUtils.getNetworkInterfaces()).thenReturn(null)

        Assert.assertEquals("", mockUtils.getMACAddress())
        Assert.assertEquals("", mockUtils.getIPAddress(true))
    }

    @Test
    fun testGetMACAddress_Wlan() {
        whenever(mockUtils.getNetworkInterfaces()).thenReturn(listOf(networkInterface))
        whenever(networkInterface.name).thenReturn(Utils.INTERFACE_WLAN)
        whenever(networkInterface.hardwareAddress).thenReturn(HARDWARE_ADDRESS)

        Assert.assertEquals(MAC_ADDR_RES, mockUtils.getMACAddress())
    }

    @Test
    fun testGetMACAddress_Eth() {
        whenever(mockUtils.getNetworkInterfaces()).thenReturn(listOf(networkInterface))
        whenever(networkInterface.name).thenReturn(Utils.INTERFACE_ETH)
        whenever(networkInterface.hardwareAddress).thenReturn(HARDWARE_ADDRESS)

        Assert.assertEquals(MAC_ADDR_RES, mockUtils.getMACAddress())
    }

    @Test
    fun testGetIPAddress() {
        whenever(mockUtils.getNetworkInterfaces()).thenReturn(listOf(networkInterface))
        whenever(networkInterface.inetAddresses).thenReturn(Collections.enumeration(listOf(inetAddressEmpty, inetAddress)))
        whenever(inetAddress.isLoopbackAddress).thenReturn(false)
        whenever(inetAddress.hostAddress).thenReturn(IP_ADDR_RES)
        whenever(inetAddressEmpty.isLoopbackAddress).thenReturn(false)
        whenever(inetAddressEmpty.hostAddress).thenReturn("")

        Assert.assertEquals(IP_ADDR_RES, mockUtils.getIPAddress(true))
    }
}
