package ru.russianpost.payments.data.repositories

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.base.di.PaymentScope
import ru.russianpost.payments.data.network.ChargesService
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.entities.charges.Charge
import ru.russianpost.payments.entities.charges.ChargesData
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import javax.inject.Inject

/**
 * Repository начислений
 */
@PaymentScope
internal class ChargesRepositoryImp @Inject constructor(
    private val service: ChargesService,
    appContextProvider: AppContextProvider,
    gson: Gson,
) : BaseRepository<ChargesData>(appContextProvider, gson), ChargesRepository {

    override fun getData() : ChargesData {
        return super.getData() ?: ChargesData(
            vehicleRegistrationCertificates = getStringSetPreference(VRC_KEY),
            driverLicenses = getStringSetPreference(DL_KEY),
            inn = getStringSetPreference(INN_KEY),
        )
    }

    override fun saveData(data: ChargesData?) {
        super.saveData(data)
        setStringSetPreference(VRC_KEY, data?.vehicleRegistrationCertificates)
        setStringSetPreference(DL_KEY, data?.driverLicenses)
        setStringSetPreference(INN_KEY, data?.inn)
    }

    override fun getCharges(data: ChargesData) : Flow<Result<List<Charge>>> =
        callAction { service.getCharges(
            dls = data.driverLicenses?.toTypedArray()?.toList().orEmpty(),
            vrcs = data.vehicleRegistrationCertificates?.toTypedArray()?.toList().orEmpty(),
            uin = data.uin,
            inn = data.inn?.toTypedArray()?.toList().orEmpty(),
        )}
//        callAction2 { throw Exception("") }
//        callAction2 { emptyList<Charge>() }

    override fun getChargeSaved(uin: String) : Flow<Result<Charge>> =
        callAction { service.getChargeSaved(uin) }

    companion object {
        private const val VRC_KEY = "VRC_KEY"
        private const val DL_KEY = "DL_KEY"
        private const val INN_KEY = "INN_KEY"
    }
}