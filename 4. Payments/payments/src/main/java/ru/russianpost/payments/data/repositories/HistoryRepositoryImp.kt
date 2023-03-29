package ru.russianpost.payments.data.repositories

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.base.di.PaymentScope
import ru.russianpost.payments.data.network.HistoryService
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.entities.history.History
import ru.russianpost.payments.entities.history.HistoryData
import ru.russianpost.payments.features.history.domain.HistoryRepository
import javax.inject.Inject

/**
 * Repository истории платежей
 */
@PaymentScope
internal class HistoryRepositoryImp @Inject constructor(
    private val service: HistoryService,
    appContextProvider: AppContextProvider,
    gson: Gson,
) : BaseRepository<HistoryData>(appContextProvider, gson), HistoryRepository {

    companion object {
        private const val TAX_FILTER_KEY = "TAX_FILTER_KEY"
        private const val FINE_FILTER_KEY = "FINE_FILTER_KEY"
        private const val PERIOD = "PERIOD"
    }

    override fun getData() : HistoryData {
        return super.getData() ?: HistoryData(
            taxFilter = getBooleanPreference(TAX_FILTER_KEY),
            fineFilter = getBooleanPreference(FINE_FILTER_KEY),
            period = getStringPreference(PERIOD),
        )
    }

    override fun saveData(data: HistoryData?) {
        super.saveData(data)
        setBooleanPreference(TAX_FILTER_KEY, data?.taxFilter)
        setBooleanPreference(FINE_FILTER_KEY, data?.fineFilter)
        setStringPreference(PERIOD, data?.period)
    }

    override fun getHistory() : Flow<Result<List<History>>> =
        callAction { service.getHistory("f266b267-c312-479d-b5d5-a01d5ba59fe7") } // temp payerId hardcode
}