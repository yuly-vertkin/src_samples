package ru.russianpost.payments.features.history.domain

import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.entities.history.History
import ru.russianpost.payments.entities.history.HistoryData

/**
 * Repository истории платежей
 */
internal interface HistoryRepository {
    fun getData() : HistoryData
    fun saveData(data: HistoryData?)
    fun getHistory() : Flow<Response<List<History>>>
}