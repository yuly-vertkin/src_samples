@file:Suppress("unused")

package ru.russianpost.payments.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import coil.ImageLoader
import ru.russianpost.android.protocols.analytics.AnalyticsService
import ru.russianpost.payments.PaymentContract
import ru.russianpost.payments.base.ui.METRICS_LOCATION_PREFIX

internal fun getCoilImageLoader(context: Context) = ImageLoader.Builder(context)
    // Create the OkHttpClient inside a lambda so it will be initialized lazily on a background thread.
    .okHttpClient { PaymentContract.okHttpClient }
    .build()

internal fun sendAnalyticsEvent(location: String?, target: String?, action: String?) {
    if (!location.isNullOrEmpty() && !target.isNullOrEmpty() && !action.isNullOrEmpty())
        PaymentContract.analytics.sendEvent(setOf(AnalyticsService.YANDEX_METRICA), METRICS_LOCATION_PREFIX + location, target, action)
}

// TODO: может не работать на не стандартных девайсах,
//  поэтому используется в качестве проверки только в случае получения HTTP error
internal fun isNetworkAvailable(): Boolean {
    // register activity with the connectivity manager service
    val connectivityManager = PaymentContract.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // if the android version is equal to M or greater we need to use the NetworkCapabilities to check what type of network has the internet connection
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Returns a Network object corresponding to the currently active default data network.
        val network = connectivityManager.activeNetwork ?: return false
        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Indicates this network uses a Wi-Fi transport or WiFi has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Indicates this network uses a Cellular transport or Cellular has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    } else { // if the android version is below M
        @Suppress("DEPRECATION") val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION") return networkInfo.isConnected
    }
}