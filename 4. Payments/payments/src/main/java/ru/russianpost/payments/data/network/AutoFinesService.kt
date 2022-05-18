package ru.russianpost.payments.data.network

import retrofit2.http.*
import ru.russianpost.payments.entities.auto_fines.AdviceData
import ru.russianpost.payments.entities.auto_fines.AutoFine

/** Сервис платежей штрафов ГИБДД */
internal interface AutoFinesService {
    @GET("fines/")
    suspend fun getFines(@Query("dl") dls: List<String>, @Query("vrc") vrcs: List<String>) : List<AutoFine>

    @POST("finepayment/finePayment")
    suspend fun sendFine(@Body fine: AutoFine) : String

    @GET("finepayment/finePayment/{id}/")
    suspend fun getFine(@Path("id") id: String) : AutoFine

    @GET("advices/advice/")
    suspend fun getAdvices() : List<AdviceData>
}
