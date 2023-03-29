package ru.russianpost.payments.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.russianpost.payments.entities.charges.Charge

/** Сервис начислений */
internal interface ChargesService {
    @GET("charges/v1/searchCharges")
    suspend fun getCharges(@Query("vu") dls: List<String>,
                           @Query("sts") vrcs: List<String>,
                           @Query("uin") uin: String?,
                           @Query("inn") inn: List<String>) : List<Charge>

    @GET("charges/v1/charge/{uin}")
    suspend fun getChargeSaved(@Path("uin") uin: String) : Charge
}
