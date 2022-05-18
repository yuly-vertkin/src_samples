package ru.russianpost.payments.data.repositories

import com.google.gson.Gson
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.russianpost.payments.data.network.AutoFinesService
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.entities.auto_fines.AdviceData
import ru.russianpost.payments.entities.auto_fines.AutoFine
import ru.russianpost.payments.entities.auto_fines.AutoFinesData
import ru.russianpost.payments.features.auto_fines.domain.AutoFinesRepository
import javax.inject.Inject

/**
 * Repository платежей штрафов ГИБДД
 */
@ActivityRetainedScoped
internal class AutoFinesRepositoryImp @Inject constructor(
    private val service: AutoFinesService,
    appContextProvider: AppContextProvider,
    gson: Gson,
) : BaseRepository<AutoFinesData>(appContextProvider, gson), AutoFinesRepository {

    companion object {
        private const val VRC_KEY = "VRC_KEY"
        private const val DL_KEY = "DL_KEY"
    }

    override fun getData() : AutoFinesData {
        return super.getData() ?: AutoFinesData(
            vehicleRegistrationCertificates = getStringListPreference(VRC_KEY),
            driverLicenses = getStringListPreference(DL_KEY),
        )
    }

    override fun saveData(data: AutoFinesData?) {
        super.saveData(data)
        setStringListPreference(VRC_KEY, data?.vehicleRegistrationCertificates)
        setStringListPreference(DL_KEY, data?.driverLicenses)
    }

    override fun getAutoFines(data: AutoFinesData) : Flow<Response<List<AutoFine>>> = flow {
        emit(Response.Loading)
//        delay(2000)

        val result = try {
            val fines: List<AutoFine> = service.getFines(data.driverLicenses.orEmpty(), data.vehicleRegistrationCertificates.orEmpty())
// for test
//            throw Exception("")
//            Response.Success(emptyList<AutoFine>())
            Response.Success(fines)
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }

    override fun sendFine(fine: AutoFine) : Flow<Response<String>> = flow {
        emit(Response.Loading)
//        delay(3000)

        val result = try {
            Response.Success(service.sendFine(fine))
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }

    override fun getFine(id: String) : Flow<Response<AutoFine>> = flow {
        emit(Response.Loading)

        val result = try {
            val fine = service.getFine(id)
            Response.Success(fine)
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }

    override fun getAdvices() : Flow<Response<List<AdviceData>>> = flow {
        emit(Response.Loading)

        val result = try {
            val advices: List<AdviceData> = service.getAdvices()
            Response.Success(advices)
        } catch (e: Exception) {
            Response.Error(e)
        }
        emit(result)
    }
}