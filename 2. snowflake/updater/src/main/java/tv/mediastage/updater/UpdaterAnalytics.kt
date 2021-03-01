package tv.mediastage.updater

import ltd.abtech.plombir.domain.analytcis.BaseAnalyticsManager

object UpdaterAnalytics : BaseAnalyticsManager() {
    const val UPDATER_CHECK_EVENT = "updater_check_event"
    const val UPDATER_DOWNLOAD_EVENT = "updater_download_event"
    const val UPDATER_UPDATE_EVENT = "updater_update_event"
    const val UPDATER_PARAM = "success"
    const val YES = "yes"
    const val NO = "no"

    private fun Boolean.toYesNo() = if (this) YES else NO

    fun updaterCheckEvent(res: Boolean) {
        customEvent(UPDATER_CHECK_EVENT, UPDATER_PARAM, res.toYesNo())
    }

    fun updaterDownloadEvent(res: Boolean) {
        customEvent(UPDATER_DOWNLOAD_EVENT, UPDATER_PARAM, res.toYesNo())
    }

    fun updaterUpdateEvent(res: Boolean) {
        customEvent(UPDATER_UPDATE_EVENT, UPDATER_PARAM, res.toYesNo())
    }
}
