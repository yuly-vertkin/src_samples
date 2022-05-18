package ru.russianpost.payments.data.repositories

import com.google.gson.Gson
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.russianpost.payments.data.network.HistoryService
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.entities.history.History
import ru.russianpost.payments.entities.history.HistoryData
import ru.russianpost.payments.features.history.domain.HistoryRepository
import javax.inject.Inject

/**
 * Repository истории платежей
 */
@ActivityRetainedScoped
internal class HistoryRepositoryImp @Inject constructor(
    private val service: HistoryService,
    appContextProvider: AppContextProvider,
    gson: Gson,
) : BaseRepository<HistoryData>(appContextProvider, gson), HistoryRepository {

    companion object {
        private const val TAX_FILTER_KEY = "TAX_FILTER_KEY"
        private const val FINE_FILTER_KEY = "FINE_FILTER_KEY"
    }

    override fun getData() : HistoryData {
        return super.getData() ?: HistoryData(
            taxFilter = getBooleanPreference(TAX_FILTER_KEY),
            fineFilter = getBooleanPreference(FINE_FILTER_KEY),
        )
    }

    override fun saveData(data: HistoryData?) {
        super.saveData(data)
        setBooleanPreference(TAX_FILTER_KEY, data?.taxFilter)
        setBooleanPreference(FINE_FILTER_KEY, data?.fineFilter)
    }

    override fun getHistory() : Flow<Response<List<History>>> = flow {
        emit(Response.Loading)
//        delay(2000)

        val result = try {
//            val history: List<History> = service.getHistory(listOf("8221168066"), listOf("7844478122"))
// temp history service not work
            val history = listOf<History>(
                History(id = "kFbVr-202204102233", title = "Оплата налога", type = "Tax", desc = "Налог на имущество физических лиц", date = "2022-04-08T14:06:30.313+03:00", sum = 5835.50f),
                History(id = "xKiA2-202204102142", title = "Оплата штрафа ГИБДД", type = "AutoFine", desc = "Несоблюдение требований знаков или разметки, за искл.случаев, предусм...", date = "2022-04-01T14:06:30.313+03:00", sum = 251f),
                History(id = "kFbVr-202204102233", title = "Оплата налога", type = "Tax", desc = "Налог на имущество физических лиц", date = "2022-02-23T14:06:30.313+03:00", sum = 3210.30f),
                History(id = "xKiA2-202204102142", title = "Оплата штрафа ГИБДД", type = "AutoFine", desc = "Несоблюдение требований знаков или разметки, за искл.случаев, предусм...", date = "2022-02-01T14:06:30.313+03:00", sum = 251f),
                History(id = "kFbVr-202204102233", title = "Оплата налога", type = "Tax", desc = "Налог на имущество физических лиц", date = "2021-12-08T14:06:30.313+03:00", sum = 5835.50f),
                History(id = "xKiA2-202204102142", title = "Оплата штрафа ГИБДД", type = "AutoFine", desc = "Несоблюдение требований знаков или разметки, за искл.случаев, предусм...", date = "2022-12-01T14:06:30.313+03:00", sum = 251f),
            )
// for test
//            throw Exception("")
//            Response.Success(emptyList<History>())
            Response.Success(history)
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }
}