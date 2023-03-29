package ru.russianpost.payments.features.charges.domain

import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.entities.charges.Charge
import ru.russianpost.payments.entities.charges.ChargesData

/**
 * Repository начислений
 */
internal interface ChargesRepository {
    fun getData() : ChargesData
    fun saveData(data: ChargesData?)
    fun getCharges(data: ChargesData) : Flow<Result<List<Charge>>>
    fun getChargeSaved(uin: String) : Flow<Result<Charge>>
}