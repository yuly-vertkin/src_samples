package ru.russianpost.payments.data.network

import okhttp3.ResponseBody
import retrofit2.http.*
import ru.russianpost.payments.entities.BaseResponse
import ru.russianpost.payments.entities.payment_card.*

/** Сервис оплаты картой */
internal interface PaymentCardService {
    // Проверка возможности оплаты начисления, например при превышении 15к р.
    @GET("charges/v1/canPayOnline")
    suspend fun paymentAvailabilityCheck(@Query("uin") uin: String) : BaseResponse

    @Headers("RU-POST-MA-SLOW: -")
    @POST("transfers/v1/transfer.create")
    suspend fun getEsiaData(@Body params: EsiaParams) : EsiaData

    @GET("transfers/v1/transfer")
    suspend fun getCommission(@Query("uin") uin: String) : Commission

    @Headers("Content-Type: application/json")
    @POST("cps/v1/payment.create")
    suspend fun paymentCard(@Body paymentCard: PaymentCard) : PaymentCardResponse

    @GET("cps/v1/card.list")
    suspend fun getCards() : CardDetails

    @POST("cps/v1/receipt.create")
    suspend fun createReceipt(@Body receipt: Receipt) : String

    @DELETE("cps/v1/paymentLink")
    suspend fun deletePaymentLink(@Query("uin") uin: String) : String

//    "http://www.africau.edu/images/default/sample.pdf"
    @GET("https://mindorks.s3.ap-south-1.amazonaws.com/courses/MindOrks_Android_Online_Professional_Course-Syllabus.pdf")
    suspend fun getCheck() : ResponseBody
}
