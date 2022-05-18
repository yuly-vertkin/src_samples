package ru.russianpost.payments.data.repositories

import com.google.gson.Gson
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.russianpost.payments.data.network.TaxService
import ru.russianpost.payments.data.network.TestResult
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.entities.tax.TaxConfirmation
import ru.russianpost.payments.entities.tax.TaxDetails
import ru.russianpost.payments.entities.tax.TreasuryData
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository
import javax.inject.Inject

/**
 * Repository ввода реквизитов платежа
 */
@ActivityRetainedScoped
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
               readFromAsset(TAX_DETAILS_DEMO_ASSET, TaxDetails::class.java) ?:
               TaxDetails()
    }

    override fun sendTaxDetails(taxDetails: TaxDetails) : Flow<Response<String>> = flow {
        emit(Response.Loading)
//        delay(3000)
        saveData(taxDetails)

        val result = try {
            Response.Success(service.sendTaxDetails(taxDetails))
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }

    override fun getTreasuryData(bic: String) : Flow<Response<TreasuryData>> = flow {
        emit(Response.Loading)

        val result = try {
            Response.Success(service.getTreasuryData(bic))
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }

    override fun getTaxDetails(id: String) : Flow<Response<TaxDetails>> = flow {
        emit(Response.Loading)

        val result = try {
            val taxDetails = service.getTaxDetails(id)
            saveData(taxDetails)
            Response.Success(taxDetails)
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }

    override fun getTaxConfirmation(id: String) : Flow<Response<TaxConfirmation>> = flow {
        emit(Response.Loading)

        val result = try {
            val taxConfirmation = service.getTaxConfirmation(id)
            Response.Success(taxConfirmation)
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }

    override suspend fun forTest() : TestResult {
        return service.forTest()
    }
}