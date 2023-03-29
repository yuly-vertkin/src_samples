package ru.russianpost.payments.demo

import ru.russianpost.android.protocols.analytics.AnalyticsProtocol
import ru.russianpost.android.protocols.analytics.AnalyticsService
import timber.log.Timber

class AnalyticsProtocolImp : AnalyticsProtocol {
    override fun sendEvent(service: Set<AnalyticsService>, location: String, target: String, action: String) {
        Timber.tag("Payments demo").d("$location, $target, $action")
    }
}