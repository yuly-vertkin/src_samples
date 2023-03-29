package ru.russianpost.payments.features.advices.domain

import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.entities.advices.AdviceData

/**
 * Repository подсказок
 */
internal interface AdvicesRepository {
    fun getData() : AdviceData
    fun saveData(data: AdviceData?)
    fun getAdvices() : Flow<Result<List<AdviceData>>>
}