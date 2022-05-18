package ru.russianpost.payments.features.auto_fines.domain

import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.entities.auto_fines.AdviceData
import ru.russianpost.payments.entities.auto_fines.AutoFine
import ru.russianpost.payments.entities.auto_fines.AutoFinesData

/**
 * Repository платежей штрафов ГИБДД
 */
internal interface AutoFinesRepository {
    fun getData() : AutoFinesData
    fun saveData(data: AutoFinesData?)
    fun getAutoFines(data: AutoFinesData) : Flow<Response<List<AutoFine>>>
    fun sendFine(fine: AutoFine) : Flow<Response<String>>
    fun getFine(id: String) : Flow<Response<AutoFine>>
    fun getAdvices() : Flow<Response<List<AdviceData>>>
}