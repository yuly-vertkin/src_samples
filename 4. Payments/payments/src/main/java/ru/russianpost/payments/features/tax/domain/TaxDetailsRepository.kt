package ru.russianpost.payments.features.tax.domain

import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.entities.tax.TaxConfirmation
import ru.russianpost.payments.entities.tax.TaxDetails
import ru.russianpost.payments.entities.tax.TreasuryData

/**
 * Repository ввода реквизитов платежа
 */
internal interface TaxDetailsRepository {
    fun getData() : TaxDetails
    fun saveData(data: TaxDetails?)
    fun sendTaxDetails(taxDetails: TaxDetails) : Flow<Result<String>>
    fun getTreasuryData(bic: String) : Flow<Result<TreasuryData>>
    fun getTaxDetails(id: String) : Flow<Result<TaxDetails>>
    fun getTaxConfirmation(id: String) : Flow<Result<TaxConfirmation>>
}