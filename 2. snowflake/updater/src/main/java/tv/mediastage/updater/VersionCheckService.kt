package tv.mediastage.updater

import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class VersionCheckService : IntentService(TAG) {
    companion object {
        private val TAG = "MediastageUpdater.VersionCheckService"
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.i(TAG, "onHandleIntent")

        UpdateManager.updateApp(this@VersionCheckService, object : UpdateManager.UpdateCallback {
            override fun onHaveNewVersion(currentVersion: String, newVersion: String, versionUrl: String) {
                Log.i(TAG,"Need update app Current version: $currentVersion, New version: $newVersion, Version url: $versionUrl")
            }

            override fun onCancelUpdate() {
                Log.i(TAG, "User cancel update")
            }

            override fun onVersionSame() {
                Log.i(TAG, "There is no new version of app")
            }

            override fun onCheckVersionFail(t: Throwable) {
                Log.e(TAG, "Version check error: $t")
            }

            override fun onDownloadFail(t: Throwable) {
                Log.e(TAG, "Version update error: $t")
            }

            override fun onUpdateStart() {
                Log.i(TAG, "Start update of app")
            }
        })
    }
}

class BootReceiver : BroadcastReceiver() {
    companion object {
        private val TAG = "MediastageUpdater.BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i(TAG, "ACTION_BOOT_COMPLETED")

            UpdateStarter.startService(context, true, true)
        }
    }
}
