package ru.russianpost.payments.data.network

import retrofit2.http.GET
import ru.russianpost.payments.entities.advices.AdviceData

/** Сервис подсказок */
internal interface AdvicesService {
    @GET("advices/advice/")
    suspend fun getAdvices() : List<AdviceData>
}
