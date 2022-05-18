package ru.russianpost.payments.features.tax.domain

import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.data.network.TestResult
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.entities.tax.TaxConfirmation
import ru.russianpost.payments.entities.tax.TaxDetails
import ru.russianpost.payments.entities.tax.TreasuryData

/**
 * Repository ввода реквизитов платежа
 */
internal interface TaxDetailsRepository {
    fun getData() : TaxDetails
    fun saveData(data: TaxDetails?)
    fun sendTaxDetails(taxDetails: TaxDetails) : Flow<Response<String>>
    fun getTreasuryData(bic: String) : Flow<Response<TreasuryData>>
    fun getTaxDetails(id: String) : Flow<Response<TaxDetails>>
    fun getTaxConfirmation(id: String) : Flow<Response<TaxConfirmation>>
    suspend fun forTest() : TestResult
}