package ru.russianpost.payments.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.russianpost.payments.entities.tax.TaxConfirmation
import ru.russianpost.payments.entities.tax.TaxDetails
import ru.russianpost.payments.entities.tax.TreasuryData

/** Сервис налоговых платежей */
internal interface TaxService {
    @POST("paymentdetails/payment/")
    suspend fun sendTaxDetails(@Body taxDetails: TaxDetails) : String

    @GET("paymentdetails/payment/{id}/")
    suspend fun getTaxDetails(@Path("id") id: String) : TaxDetails

    @GET("dictionary/bank/{bic}/")
    suspend fun getTreasuryData(@Path("bic") bic: String) : TreasuryData

    @GET("transferstorage/postalTransfer/{id}/")
    suspend fun getTaxConfirmation(@Path("id") id: String) : TaxConfirmation
}
