package ru.russianpost.payments.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import ru.russianpost.payments.entities.history.History

/** Сервис истории платежей */
internal interface HistoryService {
    @GET("paymenthistory/operation/all/{id}")
    suspend fun getHistory(@Path("id") id: String) : List<History>
}
