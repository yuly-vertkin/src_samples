package tv.mediastage.updater

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException

import java.net.NetworkInterface
import java.util.Locale

object Utils {
    const val INTERFACE_WLAN: String = "wlan0"
    const val INTERFACE_ETH: String = "eth0"

    fun getAppVersion(context: Context, packageName: String): String {
        try {
            val pinfo = context.packageManager.getPackageInfo(packageName, 0)
            return pinfo.versionName + "." + pinfo.versionCode
        } catch (e: NameNotFoundException) {
            return ""
        }
    }

    fun needUpdate(curVersionName: String, newVersionName: String) : Boolean {
        val curVers = curVersionName.split('.')
        val newVers = newVersionName.split('.')
        try {
            for (i in 0..curVers.size) {
                if (newVers[i].toInt() > curVers[i].toInt())
                    return true
                else if (newVers[i].toInt() < curVers[i].toInt())
                    return false
            }
        } catch (ignore: Exception) {
        }
        return false
    }

    fun getMACAddress(): String {
        var res = getMACAddressInt(INTERFACE_WLAN)
        if (res.isEmpty()) {
            res = getMACAddressInt(INTERFACE_ETH)
        }
        return res
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    private fun getMACAddressInt(interfaceName: String?): String {
        try {
            return getNetworkInterfaces()
                    .filter { it.name.equals(interfaceName, ignoreCase = true) || interfaceName == null }
                    .mapNotNull { it.hardwareAddress }
                    .first()
                    .joinToString(":") { String.format("%02X", it) }
        } catch (ignored: Exception) {
        }
        // for now eat exceptions
        return ""
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            return getNetworkInterfaces()
                    .filter { it.inetAddresses != null }
                    .flatMap { it.inetAddresses.toList() }
                    .filter { !it.isLoopbackAddress }
                    .map { getIPAddressInt(it.hostAddress, useIPv4) }
                    .filter { it.isNotEmpty() }
                    .first()
        } catch (ignored: Exception) {
        }
        // for now eat exceptions
        return ""
    }

    private fun getIPAddressInt(sAddr: String, useIPv4: Boolean): String {
        // boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
        val isIPv4 = sAddr.indexOf(':') < 0

        return when {
            useIPv4 && isIPv4 -> sAddr
            !useIPv4 && !isIPv4 -> {
                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                if (delim < 0) sAddr.toUpperCase(Locale.ROOT) else sAddr.substring(0, delim).toUpperCase(Locale.ROOT)
            }
            else -> ""
        }
    }

    internal fun getNetworkInterfaces() = NetworkInterface.getNetworkInterfaces().toList()
}
