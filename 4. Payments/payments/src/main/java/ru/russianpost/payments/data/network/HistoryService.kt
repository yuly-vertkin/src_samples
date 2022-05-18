package ru.russianpost.payments.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.russianpost.payments.entities.history.History

/** Сервис истории платежей */
internal interface HistoryService {
    @GET("fines/")
    suspend fun getHistory(@Query("dl") dls: List<String>, @Query("vrc") vrcs: List<String>) : List<History>
}
