package tv.mediastage.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object UpdateManager {
    interface UpdateCallback {
        fun onHaveNewVersion(currentVersion: String, newVersion: String, versionUrl: String)
        fun onCancelUpdate()
        fun onVersionSame()
        fun onCheckVersionFail(t: Throwable)
        fun onDownloadFail(t: Throwable)
        fun onUpdateStart()
    }

    private const val URL = "https://upgrade.tv.ti.ru/netbynet/"
    private const val PACKAGE_NAME = "com.nbn.NBNTV"
    private const val FILE_ON_DISK_NAME = "mediastageapp.apk"
    private const val UPDATE_FILE_TYPE = "application/vnd.android.package-archive"
    private const val BUF_SIZE = 4096

    private const val TAG = "MediastageUpdater.UpdateManager"

    private lateinit var context: Context
    private var callback: UpdateCallback? = null
    private val versionService: VersionInfoService
    private lateinit var appPath: String

    init {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
                // if need change user agent
                //        .addInterceptor(new Interceptor() {
                //            @Override
                //            public okhttp3.Response intercept(Chain chain) throws IOException {
                //                Request request = chain.request();
                //                HttpUrl url = request.url();
                //                request = request.newBuilder()
                //                        .url(url)
                //                        .build();
                //                return chain.proceed(request);                    }
                //        })
                .addInterceptor(logging)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        versionService = retrofit.create<VersionInfoService>(VersionInfoService::class.java)
    }

    fun updateApp(context: Context, callback: UpdateCallback?) {
        Log.i(TAG, "updateApp start")
        this.context = context
        this.callback = callback

        val ignore = versionService.getApp(Utils.getMACAddress(), Utils.getIPAddress(true),
                                           Utils.getAppVersion(context, PACKAGE_NAME))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleGetAppResult, this::handleGetAppError)
    }

    private fun handleGetAppResult(versionInfo: VersionInfo) {
        val newVersionName = versionInfo.versionNumber ?: ""
        val newVersionUrl = versionInfo.versionUrl ?: ""
        val curVersionName = Utils.getAppVersion(context, PACKAGE_NAME)

        Log.i(TAG, "getApp onResponse: versionNumber: $newVersionName, versionUrl: $newVersionUrl")
        UpdaterAnalytics.updaterCheckEvent(true)

        when {
            curVersionName.isEmpty() || newVersionName.isEmpty() || newVersionUrl.isEmpty() ->
                callback?.onCheckVersionFail(Exception("Wrong response"))
            curVersionName == newVersionName ->
                callback?.onVersionSame()
            Utils.needUpdate(curVersionName, newVersionName) -> {
                callback?.onHaveNewVersion(curVersionName, newVersionName, newVersionUrl)

                if (!UpdateStarter.silentMode && UpdateStarter.canShowUI(context)) {
                    UpdateStarter.showUpdateDialog(context, { dialog, which -> downloadApp(newVersionUrl) },
                                                            { dialog, which -> callback?.onCancelUpdate() })
                } else {
                    downloadApp(newVersionUrl)
                }
            }
        }
    }

    private fun handleGetAppError(t: Throwable) {
        Log.i(TAG, "getApp onFailure: t: $t")
        UpdaterAnalytics.updaterCheckEvent(false)
        callback?.onCheckVersionFail(t)
    }

    private fun downloadApp(versionUrl: String) {
        Log.i(TAG, "downloadApp: versionUrl: $versionUrl")

        val ignore = versionService.downloadApp(versionUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::writeToDisk, this::handleDownloadAppError)
    }

    private fun handleDownloadAppError(t: Throwable) {
        Log.i(TAG, "downloadApp onFailure: t: $t")
        UpdaterAnalytics.updaterDownloadEvent(false)
        callback?.onDownloadFail(t)
    }

    private fun writeToDisk(body: ResponseBody) {
        if (body.contentLength() <= 0) {
            callback?.onDownloadFail(Exception("contentLength: $(body.contentLength())"))
            return
        }

        appPath = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context.filesDir
        else context.getExternalFilesDir(null)).toString() + File.separator + FILE_ON_DISK_NAME

        val ignore = Single.fromCallable { writeResponseBodyToDisk(body) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                UpdaterAnalytics.updaterDownloadEvent(true)
                installApp(context, appPath)
            }, {
                UpdaterAnalytics.updaterDownloadEvent(false)
                callback?.onDownloadFail(it)
            })
    }

    @Throws(Exception::class)
    private fun writeResponseBodyToDisk(body: ResponseBody) {
        Log.i(TAG, "writeResponseBodyToDisk: appPath: $appPath")
        val appFile = File(appPath)
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(BUF_SIZE)
            var fileSizeDownloaded: Long = 0

            inputStream = body.byteStream()
            outputStream = FileOutputStream(appFile)

            while (true) {
                val read = inputStream?.read(fileReader) ?: -1

                if (read == -1) {
                    break
                }

                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
            }

            outputStream.flush()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    private fun installApp(context: Context, location: String) {
        Log.i(TAG, "updateApp: location: $location")
        callback?.onUpdateStart()

        if (!UpdateStarter.silentMode) {
            val intent = Intent(Intent.ACTION_VIEW)
            val tempFile = File(location)

            val apkURI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", tempFile)
            } else {
                Uri.fromFile(tempFile)
            }
            intent.setDataAndType(apkURI, UPDATE_FILE_TYPE)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(intent)
        } else {
            installAppSilently(context, location)
        }

        UpdaterAnalytics.updaterUpdateEvent(true)
    }

    private fun installAppSilently(context: Context, location: String) {
        val args = arrayOf("pm", "install", "-r", location)
        @Suppress("SpreadOperator")
        val processBuilder = ProcessBuilder(*args)
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        val successMsg = StringBuilder()
        val errorMsg = StringBuilder()

        try {
            process = processBuilder.start()
            successResult = BufferedReader(InputStreamReader(process.inputStream))
            errorResult = BufferedReader(InputStreamReader(process.errorStream))
            var s: String? = null
            while (successResult.readLine().also({ s = it }) != null) {
                successMsg.append(s)
            }
            while (errorResult.readLine().also({ s = it }) != null) {
                errorMsg.append(s)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (successResult != null) successResult.close()
                if (errorResult != null) errorResult.close()
                process?.destroy()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (successMsg.toString().toLowerCase().contains("success")) {
            Log.i(TAG, "SilentInstall success msg:$successMsg")
        } else {
            Log.i(TAG, "SilentInstall error msg:$errorMsg")
        }
    }
}
