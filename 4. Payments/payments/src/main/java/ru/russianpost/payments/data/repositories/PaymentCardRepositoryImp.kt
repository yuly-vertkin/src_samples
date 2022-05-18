package ru.russianpost.payments.data.repositories

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.CHECK_MIME_TYPE
import ru.russianpost.payments.data.network.PaymentCardService
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.PaymentCard
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * Repository оплаты картой
 */
@ActivityRetainedScoped
internal class PaymentCardRepositoryImp @Inject constructor(
    private val service: PaymentCardService,
    appContextProvider: AppContextProvider,
    gson: Gson,
) : BaseRepository<PaymentCard>(appContextProvider, gson), PaymentCardRepository {
    companion object {
        private const val PAYMENT_CARD_DEMO_ASSET = "demo/ps_payment_card.json"
    }

    override fun getData() : PaymentCard {
        return super.getData() ?:
               readFromAsset(PAYMENT_CARD_DEMO_ASSET, PaymentCard::class.java) ?:
               PaymentCard()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun saveCheckExt(context: Context, checkFileName: String) : Flow<Response<String>> = flow {
        emit(Response.Loading)

        val result = try {
            val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, checkFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, CHECK_MIME_TYPE)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues) ?:
                throw Exception(context.getString(R.string.ps_error_get_download_dir))
                resolver.openOutputStream(uri)
            } else {
                val appFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), checkFileName)
                FileOutputStream(appFile)
            }

            saveCheckToStream(outputStream)
            Response.Success("")
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun saveCheckInt(context: Context, checkFileName: String) : Flow<Response<String>> = flow {
        emit(Response.Loading)

        val result = try {
            val appPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context.filesDir
            else context.getExternalFilesDir(null)
            val appFile = File(appPath, checkFileName)
            val outputStream = FileOutputStream(appFile)

            saveCheckToStream(outputStream)
            Response.Success(appFile.toString())
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }

    private suspend fun saveCheckToStream(outputStream: OutputStream?) {
        val body = service.getCheck()

        withContext(Dispatchers.IO) {
            body.byteStream().use { input ->
                outputStream?.use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}