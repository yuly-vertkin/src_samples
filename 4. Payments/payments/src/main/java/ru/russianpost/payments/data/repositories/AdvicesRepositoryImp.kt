package ru.russianpost.payments.data.repositories

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.base.di.PaymentScope
import ru.russianpost.payments.data.network.AdvicesService
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.entities.advices.AdviceData
import ru.russianpost.payments.features.advices.domain.AdvicesRepository
import javax.inject.Inject

/**
 * Repository подсказок
 */
@PaymentScope
internal class AdvicesRepositoryImp @Inject constructor(
    private val service: AdvicesService,
    appContextProvider: AppContextProvider,
    gson: Gson,
) : BaseRepository<AdviceData>(appContextProvider, gson), AdvicesRepository {

    override fun getData() : AdviceData {
        return super.getData() ?: AdviceData(id = "", title = "", desc = "")
    }

    override fun getAdvices() : Flow<Result<List<AdviceData>>> =
        callAction { service.getAdvices() }
}