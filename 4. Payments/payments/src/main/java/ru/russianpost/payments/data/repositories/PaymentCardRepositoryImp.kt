package ru.russianpost.payments.data.repositories

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.CHECK_MIME_TYPE
import ru.russianpost.payments.data.network.PaymentCardService
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.BaseResponse
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.entities.payment_card.*
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.tools.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * Repository оплаты картой
 */
internal class PaymentCardRepositoryImp @Inject constructor(
    private val service: PaymentCardService,
    appContextProvider: AppContextProvider,
    gson: Gson,
) : BaseRepository<PaymentCardData>(appContextProvider, gson), PaymentCardRepository {

    override fun getData() : PaymentCardData {
        return super.getData() ?: PaymentCardData(
            cardUid = getStringPreference(CARD_UID_KEY),
        )
    }

    override fun saveData(data: PaymentCardData?) {
        super.saveData(data)
        setStringPreference(CARD_UID_KEY, data?.cardUid)
    }

    override fun getCards() : Flow<Result<CardDetails>> =
        callAction { service.getCards() }
// for testing different responses
//        callAction { throw Exception("") }
//        callAction { CardDetails() }
//        callAction { CardDetails(cards = emptyList()) }

    override fun paymentAvailabilityCheck(uin: String) : Flow<Result<BaseResponse>> =
        callAction { service.paymentAvailabilityCheck(uin) }

    override fun getEsiaData(params: EsiaParams) : Flow<Result<EsiaData>> =
        callAction { service.getEsiaData(params) }

    override fun getCommission(uin: String) : Flow<Result<Commission>> =
        callAction { service.getCommission(uin) }

    override fun paymentCard(card: PaymentCard) : Flow<Result<PaymentCardResponse>> =
        callAction { service.paymentCard(card) }

    override fun createReceipt(receipt: Receipt) : Flow<Result<String>> =
        callAction { service.createReceipt(receipt) }

    override fun deletePaymentLink(uin: String) : Flow<Result<String>> =
        callAction { service.deletePaymentLink(uin) }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun saveCheckExt(context: Context, checkFileName: String) : Flow<Result<String>> = flow {
        emit(Result.Loading)

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
// TODO: check it return the same dir
                val appFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), checkFileName)
//                val appFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), checkFileName)
                FileOutputStream(appFile)
            }

            saveCheckToStream(outputStream)
            Result.Success("")
        } catch (e: Exception) {
            Log.e(e)
            Result.Error(e)
        }
        emit(result)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun saveCheckInt(context: Context, checkFileName: String) : Flow<Result<String>> = flow {
        emit(Result.Loading)

        val result = try {
            val appPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context.filesDir
            else context.getExternalFilesDir(null)
            val appFile = File(appPath, checkFileName)
            val outputStream = FileOutputStream(appFile)

            saveCheckToStream(outputStream)
            Result.Success(appFile.toString())
        } catch (e: Exception) {
            Log.e(e)
            Result.Error(e)
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

    companion object {
        private const val CARD_UID_KEY = "CARD_UID_KEY"
    }
}