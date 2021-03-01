package tv.mediastage.updater

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.WindowManager

object UpdateStarter {
    private const val INTERVAL = (60 * 60 * 1000).toLong()
    private const val START_DELAY = (10 * 60 * 1000).toLong()

    private const val TAG = "MediastageUpdater.UpdateStarter"

    internal var silentMode: Boolean = false
    private var repetionStarted: Boolean = false
    private var serviceIntent: PendingIntent? = null
    private var currentDialog: AlertDialog? = null

    internal fun canShowUI(context: Context) = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)

    fun checkAndStartService(context: Context, withStartDelay: Boolean, silentMode: Boolean) {
        if (!canShowUI(context)) {
            showRequestPermissionsDialog(context) { dialog, which ->
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Log.e(TAG, e.message)
                    showErrorRequestPermissionsDialog(context)
                }
            }
        }
        startService(context, withStartDelay, silentMode)
    }

    internal fun startService(context: Context, withStartDelay: Boolean, silentMode: Boolean) {
        if (!repetionStarted) {
            val i = Intent(context, VersionCheckService::class.java)
            val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            serviceIntent = PendingIntent.getService(context, 0, i, 0)
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
                    if (withStartDelay) START_DELAY else 0, INTERVAL, serviceIntent)
            repetionStarted = true
            this.silentMode = silentMode
            Log.i(TAG, "AlarmManager.setRepeating started")

            UpdaterAnalytics.init(context, false, "")
        }
    }

    fun stopService(context: Context) {
        if (serviceIntent != null) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            am.cancel(serviceIntent)
        }
    }

    private fun showRequestPermissionsDialog(context: Context, onPositiveClick: (DialogInterface, Int) -> Unit) {
        currentDialog?.dismiss()
        val dialog: AlertDialog = AlertDialog.Builder(context)
                .setTitle(R.string.request_permissions_title)
                .setMessage(R.string.request_permissions_text)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, onPositiveClick)
                .create()
        try {
            dialog.show()
            currentDialog = dialog
//            dialog.getWindow()?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
            Log.i(TAG, "RequestPermissionsDialog shown")
        } catch (expected: Throwable) {
            Log.e(TAG, "showRequestPermissionsDialog error: $expected")
            onPositiveClick(dialog, 0)
        }
    }

    private fun showErrorRequestPermissionsDialog(context: Context) {
        currentDialog?.dismiss()
        val dialog: AlertDialog = AlertDialog.Builder(context)
                .setTitle(R.string.error_request_permissions_title)
                .setMessage(R.string.error_request_permissions_text)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, null)
                .create()
        try {
            dialog.show()
            currentDialog = dialog
//            dialog.getWindow()?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
            Log.i(TAG, "ErrorRequestPermissionsDialog shown")
        } catch (expected: Throwable) {
            Log.e(TAG, "showErrorRequestPermissionsDialog error: $expected")
        }
    }

    internal fun showUpdateDialog(context: Context, onPositiveClick: (DialogInterface, Int) -> Unit,
                                                    onNegativeClick: (DialogInterface, Int) -> Unit ) {
        currentDialog?.dismiss()
        val dialog: AlertDialog = AlertDialog.Builder(context)
                .setTitle(R.string.update_title)
                .setMessage(R.string.update_text)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, onPositiveClick)
                .setNegativeButton(R.string.Cancel, onNegativeClick)
                .create()
        dialog.getWindow()?.setType(when {
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> WindowManager.LayoutParams.TYPE_TOAST
            else -> WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        })
        try {
            dialog.show()
            currentDialog = dialog
//            dialog.getWindow()?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
            Log.i(TAG, "UpdateDialog shown")
        } catch (expected: Throwable) {
            Log.e(TAG, "showUpdateDialog error: $expected")
            onPositiveClick(dialog, 0)
        }
    }
}
