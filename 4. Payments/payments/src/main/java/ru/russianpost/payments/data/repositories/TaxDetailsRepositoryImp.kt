package ru.russianpost.payments.data.repositories

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.PaymentContract
import ru.russianpost.payments.base.di.PaymentScope
import ru.russianpost.payments.data.network.TaxService
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.entities.tax.TaxConfirmation
import ru.russianpost.payments.entities.tax.TaxDetails
import ru.russianpost.payments.entities.tax.TreasuryData
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository
import javax.inject.Inject

/**
 * Repository ввода реквизитов платежа
 */
@PaymentScope
internal class TaxDetailsRepositoryImp @Inject constructor(
    private val service: TaxService,
    appContextProvider: AppContextProvider,
    gson: Gson,
) : BaseRepository<TaxDetails>(appContextProvider, gson), TaxDetailsRepository {
    companion object {
        private const val TAX_DETAILS_DEMO_ASSET = "demo/ps_tax_details.json"
    }

    override fun getData() : TaxDetails {
        return super.getData() ?:
               if (PaymentContract.isTestVersion) readFromAsset(TAX_DETAILS_DEMO_ASSET, TaxDetails::class.java) ?: TaxDetails()
               else TaxDetails()
    }

    override fun sendTaxDetails(taxDetails: TaxDetails) : Flow<Result<String>> {
        saveData(taxDetails)
        return callAction { service.sendTaxDetails(taxDetails) }
    }

    override fun getTreasuryData(bic: String) : Flow<Result<TreasuryData>> =
        callAction { service.getTreasuryData(bic) }

    override fun getTaxDetails(id: String) : Flow<Result<TaxDetails>> =
        callAction { service.getTaxDetails(id) }

    override fun getTaxConfirmation(id: String) : Flow<Result<TaxConfirmation>> =
        callAction { service.getTaxConfirmation(id) }
}