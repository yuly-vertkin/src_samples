package ru.russianpost.payments.data.repositories

import com.google.gson.Gson
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.PaymentStartParams
import javax.inject.Inject

/**
 * Repository стартовых параметров платежа
 */
internal class PaymentStartParamsRepositoryImp @Inject constructor(
    appContextProvider: AppContextProvider,
    gson: Gson,
) : BaseRepository<PaymentStartParams>(appContextProvider, gson), PaymentStartParamsRepository {

    override fun getData() : PaymentStartParams {
        return super.getData() ?: PaymentStartParams()
    }
}