package ru.russianpost.payments.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.russianpost.payments.entities.tax.TaxConfirmation
import ru.russianpost.payments.entities.tax.TaxDetails
import ru.russianpost.payments.entities.tax.TreasuryData

internal data class TestResult(val errorCode: Int)

/** Сервис налоговых платежей */
internal interface TaxService {
    @POST("paymentstorage/payment/")
    suspend fun sendTaxDetails(@Body taxDetails: TaxDetails) : String

    @GET("paymentstorage/payment/{id}/")
    suspend fun getTaxDetails(@Path("id") id: String) : TaxDetails

    @GET("dictionary/bank/{bic}/")
    suspend fun getTreasuryData(@Path("bic") bic: String) : TreasuryData

    @GET("transferstorage/postalTransfer/{id}/")
    suspend fun getTaxConfirmation(@Path("id") id: String) : TaxConfirmation

    // for test
    @GET("https://try3.mediastage.tv/Subscriber/openAPI/jsonRequest?action=getChannelCategoryList")
    suspend fun forTest(): TestResult
}
